package com.example;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static Map<String, PendingRtp> pendingRtp = new HashMap<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 500L;

    public static class PendingRtp {
        public PlayerData data;
        public long detectedAt;
        
        public PendingRtp(PlayerData data) {
            this.data = data;
            this.detectedAt = System.currentTimeMillis();
        }
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_INTERVAL_MS) {
            lastTickTime = now;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null) {
                List<PlayerData> newList = new ArrayList<>();
                Set<String> currentNames = new HashSet<>();

                // Skanuj graczy w zasięgu
                for (PlayerEntity player : client.world.getPlayers()) {
                    if (player != client.player && !BlacklistTracker.isBlacklisted(player.getName().getString())) {
                        String name = player.getName().getString();
                        currentNames.add(name);
                        
                        // Sprawdź czy gracz jest blisko stone button
                        if (isNearStoneButton(player, client)) {
                            PlayerData pd = new PlayerData(player);
                            pd.nearButton = true;
                            pd.lastSeenNearButton = now;
                            
                            // Dodaj do pending RTP jeśli jeszcze nie ma
                            if (!pendingRtp.containsKey(name)) {
                                pendingRtp.put(name, new PendingRtp(pd));
                            } else {
                                // Aktualizuj dane
                                pendingRtp.get(name).data = pd;
                            }
                        }
                    }
                }

                // Sprawdź pending RTP - czy gracze zniknęli?
                Iterator<Map.Entry<String, PendingRtp>> it = pendingRtp.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, PendingRtp> entry = it.next();
                    String name = entry.getKey();
                    PendingRtp pending = entry.getValue();
                    long elapsed = now - pending.detectedAt;

                    if (!currentNames.contains(name)) {
                        // Gracz zniknął!
                        if (elapsed <= 10000) { // Zniknął w ciągu 10 sekund
                            // Dodaj do listy wykrytych RTP
                            DisappearedTracker.markDisappeared(pending.data);
                        }
                        it.remove();
                    } else if (elapsed > 15000) {
                        // Za długo przy buttonie bez zniknięcia - usuń
                        it.remove();
                    }
                }

                // Aktualizuj listę graczy przy buttonach (do wyświetlenia w GUI)
                for (PendingRtp pending : pendingRtp.values()) {
                    newList.add(pending.data);
                }

                DisappearedTracker.update(newList);
                currentList = newList;
            }
        }
    }

    private static boolean isNearStoneButton(PlayerEntity player, MinecraftClient client) {
        BlockPos pos = player.getBlockPos();
        int radius = 4;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    try {
                        if (client.world.getBlockState(checkPos).getBlock() == Blocks.STONE_BUTTON) {
                            return true;
                        }
                    } catch (Exception e) {
                        // Chunk not loaded
                    }
                }
            }
        }
        return false;
    }

    public static List<PlayerData> getCurrentList() {
        return currentList;
    }

    public static int getPendingCount() {
        return pendingRtp.size();
    }
}

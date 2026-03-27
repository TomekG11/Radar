package com.example;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static Map<String, PendingRtp> pendingRtp = new LinkedHashMap<>();
    private static Set<String> clickedButton = new HashSet<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 500L;

    public static class PendingRtp {
        public PlayerData data;
        public long detectedAt;
        public boolean wasNearButton;
        
        public PendingRtp(PlayerData data) {
            this.data = data;
            this.detectedAt = System.currentTimeMillis();
            this.wasNearButton = false;
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

                for (PlayerEntity player : client.world.getPlayers()) {
                    if (player != client.player && !BlacklistTracker.isBlacklisted(player.getName().getString())) {
                        String name = player.getName().getString();
                        PlayerData pd = new PlayerData(player);
                        
                        // Sprawdź czy gracz ma wymagane itemy
                        if (!ItemChecker.hasRequiredItems(pd)) {
                            continue;
                        }
                        
                        currentNames.add(name);
                        
                        boolean nearButton = isNearStoneButton(player, client);
                        
                        if (nearButton) {
                            clickedButton.add(name);
                            pd.nearButton = true;
                            pd.lastSeenNearButton = now;
                            
                            if (!pendingRtp.containsKey(name)) {
                                pendingRtp.put(name, new PendingRtp(pd));
                            }
                            PendingRtp pending = pendingRtp.get(name);
                            pending.data = pd;
                            pending.wasNearButton = true;
                        } else if (pendingRtp.containsKey(name)) {
                            pendingRtp.get(name).data = pd;
                        }
                    }
                }

                // Sprawdź czy gracze którzy KLIKNĘLI BUTTON zniknęli
                Iterator<Map.Entry<String, PendingRtp>> it = pendingRtp.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, PendingRtp> entry = it.next();
                    String name = entry.getKey();
                    PendingRtp pending = entry.getValue();
                    long elapsed = now - pending.detectedAt;

                    if (!currentNames.contains(name)) {
                        // Gracz zniknął - ale TYLKO dodaj jeśli kliknął button!
                        if (clickedButton.contains(name) && pending.wasNearButton && elapsed <= 15000) {
                            DisappearedTracker.markDisappeared(pending.data);
                        }
                        it.remove();
                        clickedButton.remove(name);
                    } else if (elapsed > 20000) {
                        // Za długo bez zniknięcia
                        it.remove();
                        clickedButton.remove(name);
                    }
                }

                // Lista graczy przy buttonach (najnowsi na górze)
                List<PendingRtp> sortedPending = new ArrayList<>(pendingRtp.values());
                sortedPending.sort((a, b) -> Long.compare(b.detectedAt, a.detectedAt));
                
                for (PendingRtp pending : sortedPending) {
                    if (pending.wasNearButton) {
                        newList.add(pending.data);
                    }
                }

                DisappearedTracker.update(newList);
                currentList = newList;
            }
        }
    }

    private static boolean isNearStoneButton(PlayerEntity player, MinecraftClient client) {
        BlockPos pos = player.getBlockPos();
        int radius = 3;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    try {
                        var block = client.world.getBlockState(checkPos).getBlock();
                        if (block == Blocks.STONE_BUTTON) {
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
    
    public static void reset() {
        pendingRtp.clear();
        clickedButton.clear();
        currentList.clear();
    }
}

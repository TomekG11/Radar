package com.example;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static Map<String, PlayerData> nearButtonPlayers = new LinkedHashMap<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 500L;

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
                        
                        if (!ItemChecker.hasRequiredItems(pd)) {
                            continue;
                        }
                        
                        currentNames.add(name);
                        
                        // Najnowsi na górze
                        newList.add(0, pd);
                        
                        // Sprawdź czy gracz jest blisko buttona (max 5 bloków)
                        if (isNearStoneButton(player, client, 5)) {
                            nearButtonPlayers.put(name, pd);
                        }
                    }
                }

                // Sprawdź czy gracze którzy byli blisko buttona zniknęli
                Iterator<Map.Entry<String, PlayerData>> it = nearButtonPlayers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, PlayerData> entry = it.next();
                    String name = entry.getKey();
                    
                    if (!currentNames.contains(name)) {
                        // Gracz zniknął będąc blisko buttona → RTP!
                        DisappearedTracker.markDisappeared(entry.getValue());
                        it.remove();
                    }
                }

                currentList = newList;
            }
        }
    }

    private static boolean isNearStoneButton(PlayerEntity player, MinecraftClient client, int radius) {
        BlockPos pos = player.getBlockPos();
        
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
    
    public static void reset() {
        nearButtonPlayers.clear();
        currentList.clear();
    }
}

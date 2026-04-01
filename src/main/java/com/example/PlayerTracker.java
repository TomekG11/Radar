package com.example;

import net.minecraft.block.Block;
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
                        
                        // Sprawdź czy gracz jest blisko buttona na gąbce (min 4 kratki)
                        if (isNearSpongeButton(player, client, 4)) {
                            nearButtonPlayers.put(name, pd);
                        }
                    }
                }

                // Usuń graczy z listy RTP jeśli wrócili do zasięgu
                for (String name : currentNames) {
                    DisappearedTracker.remove(name);
                }

                // Sprawdź czy gracze którzy byli blisko buttona zniknęli
                Iterator<Map.Entry<String, PlayerData>> it = nearButtonPlayers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, PlayerData> entry = it.next();
                    String name = entry.getKey();
                    
                    if (!currentNames.contains(name)) {
                        // Gracz zniknął będąc blisko buttona na gąbce → RTP!
                        DisappearedTracker.markDisappeared(entry.getValue());
                        it.remove();
                    }
                }

                currentList = newList;
            }
        }
    }

    /**
     * Sprawdza czy gracz jest w odległości co najmniej minDistance kratek od stone_button,
     * który stoi na sponge (gąbce). Szukamy w promieniu 10 bloków.
     */
    private static boolean isNearSpongeButton(PlayerEntity player, MinecraftClient client, int minDistance) {
        BlockPos playerPos = player.getBlockPos();
        int searchRadius = 10;
        
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    try {
                        Block block = client.world.getBlockState(checkPos).getBlock();
                        if (block == Blocks.STONE_BUTTON) {
                            // Sprawdź czy pod buttonem jest gąbka
                            BlockPos belowButton = checkPos.down();
                            Block blockBelow = client.world.getBlockState(belowButton).getBlock();
                            if (blockBelow == Blocks.SPONGE || blockBelow == Blocks.WET_SPONGE) {
                                // Oblicz odległość gracza od buttona
                                double distance = Math.sqrt(playerPos.getSquaredDistance(checkPos));
                                if (distance >= minDistance) {
                                    return true;
                                }
                            }
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

package com.example;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static Map<String, PlayerData> nearSpongePlayers = new LinkedHashMap<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 500L;
    private static final int SPONGE_SEARCH_RADIUS = 15;
    private static final double MIN_HORIZONTAL_DISTANCE = 4.0;

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

                        // Sprawdź czy gracz jest w pobliżu gąbki (min 4 kratki w XZ)
                        if (isNearSpongeHorizontally(player, client)) {
                            nearSpongePlayers.put(name, pd);
                        }
                    }
                }

                // Usuń graczy z listy RTP jeśli wrócili do zasięgu
                for (String name : currentNames) {
                    DisappearedTracker.remove(name);
                }

                // Sprawdź czy gracze którzy byli blisko gąbki zniknęli
                Iterator<Map.Entry<String, PlayerData>> it = nearSpongePlayers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, PlayerData> entry = it.next();
                    String name = entry.getKey();

                    if (!currentNames.contains(name)) {
                        // Gracz zniknął będąc w pobliżu gąbki → RTP!
                        DisappearedTracker.markDisappeared(entry.getValue());
                        it.remove();
                    }
                }

                currentList = newList;
            }
        }
    }

    /**
     * Sprawdza czy gracz jest w poziomej odległości (XZ, ignorując Y) 
     * co najmniej MIN_HORIZONTAL_DISTANCE kratek od jakiejkolwiek gąbki w promieniu.
     */
    private static boolean isNearSpongeHorizontally(PlayerEntity player, MinecraftClient client) {
        BlockPos playerPos = player.getBlockPos();
        double playerX = player.getX();
        double playerZ = player.getZ();

        for (int x = -SPONGE_SEARCH_RADIUS; x <= SPONGE_SEARCH_RADIUS; x++) {
            for (int y = -SPONGE_SEARCH_RADIUS; y <= SPONGE_SEARCH_RADIUS; y++) {
                for (int z = -SPONGE_SEARCH_RADIUS; z <= SPONGE_SEARCH_RADIUS; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    try {
                        Block block = client.world.getBlockState(checkPos).getBlock();
                        if (block == Blocks.SPONGE || block == Blocks.WET_SPONGE) {
                            // Oblicz odległość poziomą (tylko XZ, bez Y)
                            double dx = playerX - (checkPos.getX() + 0.5);
                            double dz = playerZ - (checkPos.getZ() + 0.5);
                            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

                            if (horizontalDistance >= MIN_HORIZONTAL_DISTANCE) {
                                return true;
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
        nearSpongePlayers.clear();
        currentList.clear();
    }
}

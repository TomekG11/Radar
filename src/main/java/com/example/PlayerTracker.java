package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 1000L;

    public static void tick() {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_INTERVAL_MS) {
            lastTickTime = now;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null) {
                List<PlayerData> newList = new ArrayList<>();
                for (PlayerEntity player : client.world.getPlayers()) {
                    if (player != client.player && !BlacklistTracker.isBlacklisted(player.getName().getString())) {
                        PlayerData pd = new PlayerData(player);
                        boolean matches = NetheriteChecker.hasNetheriteItem(pd);
                        if (!matches && ModSettings.showElytra) {
                            matches = NetheriteChecker.hasElytra(pd);
                        }
                        if (matches) {
                            newList.add(pd);
                        }
                    }
                }
                Set<String> newNames = new HashSet<>();
                for (PlayerData pd : newList) {
                    newNames.add(pd.name);
                }
                for (PlayerData old : currentList) {
                    if (!newNames.contains(old.name)) {
                        DisappearedTracker.markDisappeared(old);
                    }
                }
                DisappearedTracker.update(newList);
                currentList = newList;
            }
        }
    }

    public static List<PlayerData> getCurrentList() {
        return currentList;
    }
}

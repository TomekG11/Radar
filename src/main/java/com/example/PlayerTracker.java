package com.example;

import net.minecraft.class_1657;
import net.minecraft.class_310;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 1000L;

    public static void tick() {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_INTERVAL_MS) {
            lastTickTime = now;
            class_310 client = class_310.method_1551();
            
            if (client.field_1687 != null && client.field_1724 != null) {
                List<PlayerData> newList = new ArrayList<>();
                
                for (class_1657 player : client.field_1687.method_18456()) {
                    if (player != client.field_1724 && 
                        !BlacklistTracker.isBlacklisted(player.method_5477().getString())) {
                        
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

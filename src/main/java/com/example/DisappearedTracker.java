package com.example;

import java.util.*;

public class DisappearedTracker {
    private static final Map<String, DisappearedEntry> disappeared = new LinkedHashMap<>();

    public static void update(List<PlayerData> currentVisible) {
        Set<String> currentNames = new HashSet<>();
        for (PlayerData pd : currentVisible) {
            currentNames.add(pd.name);
        }
        
        disappeared.entrySet().removeIf(e -> currentNames.contains(e.getKey()));
    }

    public static void markDisappeared(PlayerData pd) {
        if (!disappeared.containsKey(pd.name)) {
            disappeared.put(pd.name, new DisappearedEntry(pd));
        }
    }

    public static Collection<DisappearedEntry> getDisappeared() {
        return disappeared.values();
    }

    public static void remove(String name) {
        disappeared.remove(name);
    }

    public static void clearAll() {
        disappeared.clear();
    }

    public static class DisappearedEntry {
        public final PlayerData data;
        public final long disappearedAt;

        public DisappearedEntry(PlayerData data) {
            this.data = data;
            this.disappearedAt = System.currentTimeMillis();
        }

        public String getTimeAgo() {
            long seconds = (System.currentTimeMillis() - disappearedAt) / 1000;
            if (seconds < 60) {
                return seconds + "s temu";
            } else {
                long minutes = seconds / 60;
                if (minutes < 60) {
                    return minutes + "min temu";
                } else {
                    long hours = minutes / 60;
                    if (hours < 24) {
                        return hours + "h temu";
                    } else {
                        long days = hours / 24;
                        return days + "d temu";
                    }
                }
            }
        }
    }
}

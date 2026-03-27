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
            long seconds = (System.currentTimeMillis() - this.disappearedAt) / 1000L;
            if (seconds < 60L) return seconds + "s temu";
            long minutes = seconds / 60L;
            if (minutes < 60L) return minutes + "min temu";
            long hours = minutes / 60L;
            if (hours < 24L) return hours + "h temu";
            long days = hours / 24L;
            return days + "d temu";
        }
    }
}

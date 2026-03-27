package com.example;

import java.util.*;

public class DisappearedTracker {
    private static final Map<String, DisappearedEntry> disappeared = new LinkedHashMap<>();

    public static void update(List<PlayerData> currentVisible) {
        Set<String> currentNames = new HashSet<>();
        for (PlayerData pd : currentVisible) currentNames.add(pd.name);
        disappeared.entrySet().removeIf(e -> currentNames.contains(e.getKey()));
    }

    public static void markDisappeared(PlayerData pd) {
        if (!disappeared.containsKey(pd.name)) {
            disappeared.put(pd.name, new DisappearedEntry(pd));
        }
    }

    public static Collection<DisappearedEntry> getDisappeared() { return disappeared.values(); }
    public static void remove(String name) { disappeared.remove(name); }
    public static void clearAll() { disappeared.clear(); }

    public static class DisappearedEntry {
        public final PlayerData data;
        public final long disappearedAt;

        public DisappearedEntry(PlayerData data) {
            this.data = data;
            this.disappearedAt = System.currentTimeMillis();
        }

        public String getTimeAgo() {
            long s = (System.currentTimeMillis() - disappearedAt) / 1000L;
            if (s < 60L) return s + "s temu";
            long m = s / 60L;
            if (m < 60L) return m + "min temu";
            long h = m / 60L;
            if (h < 24L) return h + "h temu";
            return (h / 24L) + "d temu";
        }
    }
}

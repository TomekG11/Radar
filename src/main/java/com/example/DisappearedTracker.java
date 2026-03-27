package com.example;

import java.util.*;

public class DisappearedTracker {
    private static final LinkedList<DisappearedEntry> disappeared = new LinkedList<>();

    public static void markDisappeared(PlayerData pd) {
        // Usuń jeśli już istnieje
        disappeared.removeIf(e -> e.data.name.equals(pd.name));
        // Dodaj na początek (najnowsi na górze)
        disappeared.addFirst(new DisappearedEntry(pd));
    }

    public static List<DisappearedEntry> getDisappeared() {
        return new ArrayList<>(disappeared);
    }

    public static void remove(String name) {
        disappeared.removeIf(e -> e.data.name.equals(name));
    }

    public static void clearAll() {
        disappeared.clear();
        PlayerTracker.reset();
    }
    
    public static int getCount() {
        return disappeared.size();
    }

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

package com.example;

import java.util.*;

public class WatchlistTracker {
    private static final Map<String, WatchlistEntry> watchlist = new LinkedHashMap<>();

    public static void add(PlayerData pd) {
        if (!watchlist.containsKey(pd.name)) {
            watchlist.put(pd.name, new WatchlistEntry(pd));
        }
    }

    public static void remove(String name) {
        watchlist.remove(name);
    }

    public static boolean isWatched(String name) {
        return watchlist.containsKey(name);
    }

    public static Collection<WatchlistEntry> getAll() {
        return watchlist.values();
    }

    public static void clear() {
        watchlist.clear();
    }

    public static class WatchlistEntry {
        public final PlayerData data;
        public final long addedAt;

        public WatchlistEntry(PlayerData data) {
            this.data = data;
            this.addedAt = System.currentTimeMillis();
        }

        public WatchlistEntry(PlayerData data, long addedAt) {
            this.data = data;
            this.addedAt = addedAt;
        }

        public String getTimeAgo() {
            long seconds = (System.currentTimeMillis() - this.addedAt) / 1000L;
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

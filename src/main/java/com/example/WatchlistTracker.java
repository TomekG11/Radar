package com.example;

import java.util.*;

public class WatchlistTracker {
    private static final Map<String, WatchlistEntry> watchlist = new LinkedHashMap<>();

    public static void add(PlayerData pd) {
        if (!watchlist.containsKey(pd.name)) {
            watchlist.put(pd.name, new WatchlistEntry(pd));
        }
    }

    public static void remove(String name) { watchlist.remove(name); }
    public static boolean isWatched(String name) { return watchlist.containsKey(name); }
    public static Collection<WatchlistEntry> getAll() { return watchlist.values(); }
    public static void clear() { watchlist.clear(); }

    public static class WatchlistEntry {
        public final PlayerData data;
        public final long addedAt;

        public WatchlistEntry(PlayerData data) {
            this.data = data;
            this.addedAt = System.currentTimeMillis();
        }

        public String getTimeAgo() {
            long s = (System.currentTimeMillis() - addedAt) / 1000L;
            if (s < 60L) return s + "s temu";
            long m = s / 60L;
            if (m < 60L) return m + "min temu";
            long h = m / 60L;
            if (h < 24L) return h + "h temu";
            return (h / 24L) + "d temu";
        }
    }
}

package com.example;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
            long seconds = (System.currentTimeMillis() - addedAt) / 1000;
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

package com.example;

import java.util.*;

public class RtpTracker {
    private static final LinkedList<RtpEntry> rtpList = new LinkedList<>();

    public static void markRtp(PlayerData pd) {
        // Usuń jeśli już istnieje
        rtpList.removeIf(e -> e.data.name.equals(pd.name));
        // Dodaj na początek (najnowsi na górze)
        rtpList.addFirst(new RtpEntry(pd));
    }

    public static List<RtpEntry> getAll() {
        return new ArrayList<>(rtpList);
    }

    public static void remove(String name) {
        rtpList.removeIf(e -> e.data.name.equals(name));
    }

    public static void clearAll() {
        rtpList.clear();
        PlayerTracker.reset();
    }
    
    public static int getCount() {
        return rtpList.size();
    }

    public static class RtpEntry {
        public final PlayerData data;
        public final long rtpAt;

        public RtpEntry(PlayerData data) {
            this.data = data;
            this.rtpAt = System.currentTimeMillis();
        }

        public String getTimeAgo() {
            long s = (System.currentTimeMillis() - rtpAt) / 1000L;
            if (s < 60L) return s + "s temu";
            long m = s / 60L;
            if (m < 60L) return m + "min temu";
            long h = m / 60L;
            if (h < 24L) return h + "h temu";
            return (h / 24L) + "d temu";
        }
    }
}

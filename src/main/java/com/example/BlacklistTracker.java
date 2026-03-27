package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BlacklistTracker {
    private static final Set<String> blacklisted = new LinkedHashSet<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path savePath;

    public static void init() {
        savePath = FabricLoader.getInstance().getGameDir().resolve("ukrainskireader_blacklist.json");
        load();
    }

    public static void add(String name) {
        blacklisted.add(name.toLowerCase());
        save();
    }

    public static void remove(String name) {
        blacklisted.remove(name.toLowerCase());
        save();
    }

    public static boolean isBlacklisted(String name) {
        return blacklisted.contains(name.toLowerCase());
    }

    public static Set<String> getAll() {
        return Collections.unmodifiableSet(blacklisted);
    }

    private static void save() {
        try {
            JsonArray arr = new JsonArray();
            for (String name : blacklisted) {
                arr.add(name);
            }
            Files.writeString(savePath, GSON.toJson(arr));
        } catch (Exception e) {
            System.err.println("[UkrainskiReader] Błąd zapisu blacklisty: " + e.getMessage());
        }
    }

    private static void load() {
        if (Files.exists(savePath)) {
            try {
                String json = Files.readString(savePath);
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement el : arr) {
                    blacklisted.add(el.getAsString());
                }
                System.out.println("[UkrainskiReader] Załadowano " + blacklisted.size() + " zablokowanych graczy.");
            } catch (Exception e) {
                System.err.println("[UkrainskiReader] Błąd odczytu blacklisty: " + e.getMessage());
            }
        }
    }
}

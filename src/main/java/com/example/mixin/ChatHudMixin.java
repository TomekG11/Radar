package com.example.mixin;

import com.example.WatchlistTracker;
import com.example.WatchlistTracker.WatchlistEntry;
import com.example.GearReaderScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mixin nasłuchujący nowych wiadomości w chat HUD.
 * - Powiadamia (actionbar + dźwięk) jeśli w treści pojawi się nick z watchlisty (cooldown 5s dla każdego nicku).
 * - Zamyka GearReaderScreen, jeśli wykryje wiadomość typu "Zostaniesz przeteleportowany za 5s" (kolory ignorowane).
 */
@Mixin(ChatHud.class)
public class ChatHudMixin {
    // cooldown (ms) między powiadomieniami dla tego samego nicku
    private static final long COOLDOWN_MS = 5_000L;
    private static final Map<String, Long> lastNotified = new HashMap<>();

    // Wzorzec wykrywający komunikat o teleportowaniu (ignorujemy kolory i wielkość liter)
    private static final Pattern TELEPORT_PATTERN = Pattern.compile("zostaniesz\\s+przeteleportowany\\s+za\\s*\\d+\\s*s", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/text/Text;)V")
    private void onAddMessage(Text message, CallbackInfo ci) {
        try {
            if (message == null) return;
            String raw = message.getString();
            if (raw == null || raw.isEmpty()) return;

            // Usuń kody kolorów zarówno '§x' jak i '&x' dla bezpieczeństwa/powszechności
            String cleaned = raw.replaceAll("(?i)§.", "").replaceAll("(?i)&.", "");
            String lower = cleaned.toLowerCase(Locale.ROOT);

            // 1) Sprawdź komunikat teleportu i zamknij GUI jeśli pasuje
            try {
                Matcher m = TELEPORT_PATTERN.matcher(lower);
                if (m.find()) {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        mc.execute(() -> {
                            if (mc.currentScreen instanceof GearReaderScreen) {
                                mc.setScreen(null);
                            }
                        });
                    }
                }
            } catch (Throwable ignored) {}

            // 2) Sprawdź nicki z watchlisty (dopasowanie gdziekolwiek w wiadomości)
            Collection<WatchlistEntry> watch = WatchlistTracker.getAll();
            if (watch == null || watch.isEmpty()) return;

            long now = System.currentTimeMillis();

            for (WatchlistEntry e : watch) {
                if (e == null || e.data == null || e.data.name == null) continue;
                String nick = e.data.name;
                if (nick.isEmpty()) continue;

                String nickLower = nick.toLowerCase(Locale.ROOT);

                if (lower.contains(nickLower)) {
                    Long last = lastNotified.get(nickLower);
                    if (last != null && (now - last) < COOLDOWN_MS) {
                        // w cooldownie -> pomijamy
                        continue;
                    }

                    lastNotified.put(nickLower, now);

                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        try {
                            // actionbar (nad hotbarem)
                            if (mc.inGameHud != null) {
                                mc.inGameHud.setOverlayMessage(Text.literal("Gracz " + nick + " napisał na chat"), false);
                            }
                            // dźwięk powiadomienia (możesz zmienić na inny SoundEvents)
                            if (mc.getSoundManager() != null) {
                                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
                            }
                        } catch (Throwable t) {
                            // Nie przerywamy działania moda przy błędzie
                        }
                    }
                    // nie robimy break; żeby wykryć kilka nicków w jednej wiadomości
                }
            }
        } catch (Throwable ignored) {
            // bezpieczeństwo - mixin nie powinien wyrzucać wyjątku
        }
    }
}

package com.example.mixin;

import com.example.WatchlistTracker;
import com.example.WatchlistTracker.WatchlistEntry;
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

/**
 * Mixin nasłuchujący nowych wiadomości w chat HUD.
 * Jeśli w tekście pojawi się nick z watchlisty — powiadomienie (actionbar + dźwięk).
 */
@Mixin(ChatHud.class)
public class ChatHudMixin {
    // cooldown (ms) między powiadomieniami dla tego samego nicku
    private static final long COOLDOWN_MS = 5_000L;
    private static final Map<String, Long> lastNotified = new HashMap<>();

    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/text/Text;)V")
    private void onAddMessage(Text message, CallbackInfo ci) {
        try {
            if (message == null) return;
            String raw = message.getString();
            if (raw == null || raw.isEmpty()) return;

            String lower = raw.toLowerCase(Locale.ROOT);

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
                            // actionbar
                            if (mc.inGameHud != null) {
                                mc.inGameHud.setOverlayMessage(Text.literal("Gracz " + nick + " napisał na chat"), false);
                            }
                            // dźwięk powiadomienia
                            if (mc.getSoundManager() != null) {
                                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
                            }
                        } catch (Throwable t) {
                            // nie przerywamy działania moda
                        }
                    }
                    // jeśli chcesz powiadamiać o każdym nicku w wiadomości, usuń break;
                    // zostawiam bez break aby powiadomić dla wszystkich znalezionych nicków
                }
            }
        } catch (Throwable ignored) {
            // bezpieczeństwo - mixin nie powinien wyrzucać wyjątku
        }
    }
}

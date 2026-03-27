package com.example.mixin;

import com.example.WatchlistTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/text/Text;)V")
    private void onAddMessage(Text message, CallbackInfo ci) {
        try {
            String raw = message.getString();
            if (raw == null || raw.isEmpty()) return;

            // Oczekiwany format: "{nick} » {wiad}"
            int sep = raw.indexOf(" » ");
            if (sep <= 0) return;
            String nick = raw.substring(0, sep).trim();
            if (nick.isEmpty()) return;

            if (WatchlistTracker.isWatched(nick)) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null) {
                    // actionbar (nad hotbarem)
                    if (mc.inGameHud != null) {
                        mc.inGameHud.setOverlayMessage(Text.literal("Gracz " + nick + " napisał na chat"), false);
                    }
                    // dźwięk powiadomienia
                    if (mc.getSoundManager() != null) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
                    }
                }
            }
        } catch (Throwable t) {
            // Nie przerywamy działania moda przy błędzie w mixinie
        }
    }
}

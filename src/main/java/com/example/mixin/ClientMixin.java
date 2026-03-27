package com.example.mixin;

import com.example.GearReaderScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ClientMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo info) {
        // Placeholder for tick injection if needed
    }
}

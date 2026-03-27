package com.example.mixin;

import com.example.GearReaderScreen;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_310.class)
public class ClientMixin {
    
    @Inject(method = "method_1488", at = @At("HEAD"))
    private void onChatMessage(class_2561 message, CallbackInfo ci) {
        GearReaderScreen.onChatMessage(message.getString());
    }
}

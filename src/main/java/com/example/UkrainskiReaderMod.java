package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_304;
import net.minecraft.class_3675.class_307;

public class UkrainskiReaderMod implements ClientModInitializer {
    public static class_304 openGuiKey;

    @Override
    public void onInitializeClient() {
        BlacklistTracker.init();
        
        openGuiKey = KeyBindingHelper.registerKeyBinding(new class_304(
            "key.ukrainskireader.open",
            class_307.field_1668,
            71, // G key
            "category.ukrainskireader"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerTracker.tick();
            
            while (openGuiKey.method_1436()) {
                if (client.field_1724 != null) {
                    client.method_1507(new GearReaderScreen());
                }
            }
        });
    }
}

package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class UkrainskiReaderMod implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        BlacklistTracker.init();
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ukrainskireader.open",
            InputUtil.Type.KEYSYM,
            71,
            "category.ukrainskireader"
        ));

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            PlayerTracker.tick();
            while (openGuiKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new GearReaderScreen());
                }
            }
        });
    }
}

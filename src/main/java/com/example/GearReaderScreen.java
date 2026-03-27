package com.example;

import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_640;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GearReaderScreen extends class_437 {
    private static final int COLOR_BG = 0xF0101014;
    private static final int COLOR_PANEL_DARK = 0xFF0A0A0F;
    private static final int COLOR_HEADER = 0xFF121822;
    private static final int COLOR_BORDER = 0xFF262A3A;
    private static final int COLOR_TEXT_DIM = 0xFF666A77;
    private static final int COLOR_NETHERITE_BG = 0x34000000;
    private static final int COLOR_ENTRY_BG = 0xFF0E0E12;
    private static final int COLOR_ENTRY_HOVER = 0xFF121218;
    private static final int COLOR_TIME = 0xFFFF6694;
    private static final int COLOR_STAR = 0xFFFFDD00;
    private static final int COLOR_STAR_ACTIVE = 0xFFFFAA00;
    private static final int COLOR_HP_HIGH = 0xFF00FF44;
    private static final int COLOR_HP_MID = 0xFFFFCC00;
    private static final int COLOR_HP_LOW = 0xFFFF2222;

    private static final int GUI_W = 860;
    private static final int GUI_H = 480;
    private static final int EH = 70;
    private static final int EGAP = 4;
    private static final int PAD = 6;
    private static final int HEAD = 26;
    private static final int TX = 38;

    private Tab currentTab = Tab.PLAYERS;
    private float animProgress = 0.0F;
    private long openTime;
    private int leftScroll = 0;
    private int rightScroll = 0;
    private int blacklistScroll = 0;
    private int watchlistScroll = 0;
    private int guiX;
    private int guiY;
    private int panelH;
    private int lastMX;
    private int lastMY;

    public GearReaderScreen() {
        super(class_2561.method_43470("UkrainskiReader"));
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void method_25426() {
        super.method_25426();
        this.guiX = (this.field_22789 - GUI_W) / 2;
        this.guiY = (this.field_22790 - GUI_H) / 2;
        this.panelH = 408;
    }

    public static void onChatMessage(String message) {
        if (ModSettings.closeOnTpa) {
            if (message.contains("zaakceptował") || message.contains("accepted")) {
                class_310 client = class_310.method_1551();
                if (client.field_1755 instanceof GearReaderScreen) {
                    client.execute(() -> {
                        client.method_1507(null);
                    });
                }
            }
        }
    }

    @Override
    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        this.lastMX = mouseX;
        this.lastMY = mouseY;

        int gY;
        if (!ModSettings.performanceMode) {
            float elapsed = (float)(System.currentTimeMillis() - this.openTime) / 300.0F;
            this.animProgress = class_3532.method_15363(elapsed, 0.0F, 1.0F);
            float ease = 1.0F - (1.0F - this.animProgress) * (1.0F - this.animProgress);
            gY = this.guiY + (int)((1.0F - ease) * 30.0F);
        } else {
            this.animProgress = 1.0F;
            gY = this.guiY;
        }

        this.renderMain(ctx, mouseX, mouseY, this.guiX, gY);
        super.method_25394(ctx, mouseX, mouseY, delta);
    }

    private void renderMain(class_332 ctx, int mx, int my, int gX, int gY) {
        if (!ModSettings.performanceMode) {
            this.method_25420(ctx);
            this.drawBorderGlow(ctx, gX - 2, gY - 2, 864, 484);
        } else {
            ctx.method_25294(0, 0, this.field_22789, this.field_22790, 0xAA000000);
        }

        ctx.method_25294(gX, gY, gX + 860, gY + 480, COLOR_BG);
        this.fillGradientH(ctx, gX, gY, 860, 48, 0xFF0D1520, 0xFF0E1928);
        ctx.method_25294(gX, gY + 46, gX + 860, gY + 47, ModSettings.accentColor);

        int titleW = this.field_22793.method_1727("UKRAINSKI READER");
        ctx.method_27535(this.field_22793, class_2561.method_43470("§b§lUKRAINSKI READER"), 
            gX + 430 - titleW / 2, gY + 17, ModSettings.accentColor2);
        

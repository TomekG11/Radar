package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SettingsScreen extends Screen {
    private static final int GUI_W = 400;
    private static final int GUI_H = 320;
    private final Screen parent;
    private long openTime;
    private int guiX, guiY;

    public SettingsScreen(Screen parent) {
        super(Text.literal("Ustawienia"));
        this.parent = parent;
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        guiX = (width - GUI_W) / 2;
        guiY = (height - GUI_H) / 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        float ease = MathHelper.clamp((float)(System.currentTimeMillis() - openTime) / 250.0F, 0.0F, 1.0F);
        ease = 1.0F - (1.0F - ease) * (1.0F - ease);
        int gX = guiX, gY = guiY + (int)((1.0F - ease) * 20.0F);
        renderBackground(ctx);
        ctx.fill(gX - 1, gY - 1, gX + GUI_W + 1, gY + GUI_H + 1, ModSettings.accentColor);
        ctx.fill(gX, gY, gX + GUI_W, gY + GUI_H, -267909104);
        ctx.fill(gX, gY, gX + GUI_W, gY + 48, -15921878);
        ctx.fill(gX, gY + 46, gX + GUI_W, gY + 47, ModSettings.accentColor);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§c§l⚙ USTAWIENIA"), gX + 200, gY + 17, ModSettings.accentColor2);

        int y = gY + 60, btnW = 100, btnH = 20, gap = 6;

        // Kolory
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f▌ §6§lKOLOR INTERFEJSU"), gX + 14, y, ModSettings.accentColor2);
        y += 20;
        ModSettings.ColorPreset[] presets = ModSettings.ColorPreset.values();
        for (int i = 0; i < presets.length; i++) {
            ModSettings.ColorPreset p = presets[i];
            boolean active = ModSettings.accentColor == p.accent;
            int bX = gX + 20 + (i % 3) * (btnW + gap), bY = y + (i / 3) * (btnH + gap);
            renderBtn(ctx, (active ? "§l✔ " : "") + p.label, active ? GuiButton.Style.ORANGE : GuiButton.Style.WHITE, bX, bY, btnW, btnH, hitTest(mouseX, mouseY, bX, bY, btnW, btnH));
        }
        y += (int) Math.ceil((double) presets.length / 3.0D) * (btnH + gap) + 20;

        // Opcje
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f▌ §6§lOPCJE"), gX + 14, y, ModSettings.accentColor2);
        y += 20;
        int optBtnW = 170;
        renderBtn(ctx, ModSettings.performanceMode ? "§l✔ Performance: WŁ" : "Performance: WYŁ", ModSettings.performanceMode ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20, y, optBtnW, btnH, hitTest(mouseX, mouseY, gX + 20, y, optBtnW, btnH));
        renderBtn(ctx, ModSettings.closeOnTpa ? "§l✔ Zamknij po TPA" : "Zamknij po TPA: WYŁ", ModSettings.closeOnTpa ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20 + optBtnW + 10, y, optBtnW, btnH, hitTest(mouseX, mouseY, gX + 20 + optBtnW + 10, y, optBtnW, btnH));
        y += btnH + 20;

        // Blacklist
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f▌ §c§lBLACKLIST §7(" + BlacklistTracker.getAll().size() + ")"), gX + 14, y, ModSettings.accentColor2);
        y += 20;
        int blBtnW = 180;
        renderBtn(ctx, "⛔ Zarządzaj Blacklistą", GuiButton.Style.RED, gX + GUI_W / 2 - blBtnW / 2, y, blBtnW, btnH, hitTest(mouseX, mouseY, gX + GUI_W / 2 - blBtnW / 2, y, blBtnW, btnH));

        // Powrót
        int backX = gX + GUI_W / 2 - 60, backY = gY + GUI_H - 35;
        renderBtn(ctx, "← Powrót", GuiButton.Style.WHITE, backX, backY, 120, 22, hitTest(mouseX, mouseY, backX, backY, 120, 22));
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX, my = (int) mouseY;
        float ease = MathHelper.clamp((float)(System.currentTimeMillis() - openTime) / 250.0F, 0.0F, 1.0F);
        ease = 1.0F - (1.0F - ease) * (1.0F - ease);
        int gX = guiX, gY = guiY + (int)((1.0F - ease) * 20.0F);
        int y = gY + 80, btnW = 100, btnH = 20, gap = 6;

        ModSettings.ColorPreset[] presets = ModSettings.ColorPreset.values();
        for (int i = 0; i < presets.length; i++) {
            int bX = gX + 20 + (i % 3) * (btnW + gap), bY = y + (i / 3) * (btnH + gap);
            if (hitTest(mx, my, bX, bY, btnW, btnH)) { ModSettings.setColor(presets[i]); return true; }
        }
        y += (int) Math.ceil((double) presets.length / 3.0D) * (btnH + gap) + 40;

        int optBtnW = 170;
        if (hitTest(mx, my, gX + 20, y, optBtnW, btnH)) { ModSettings.performanceMode = !ModSettings.performanceMode; return true; }
        if (hitTest(mx, my, gX + 20 + optBtnW + 10, y, optBtnW, btnH)) { ModSettings.closeOnTpa = !ModSettings.closeOnTpa; return true; }
        y += btnH + 40;

        int blBtnW = 180;
        if (hitTest(mx, my, gX + GUI_W / 2 - blBtnW / 2, y, blBtnW, btnH)) { MinecraftClient.getInstance().setScreen(new BlacklistScreen(this)); return true; }

        int backX = gX + GUI_W / 2 - 60, backY = gY + GUI_H - 35;
        if (hitTest(mx, my, backX, backY, 120, 22)) { MinecraftClient.getInstance().setScreen(parent); return true; }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderBtn(DrawContext ctx, String label, GuiButton.Style style, int x, int y, int w, int h, boolean hovered) {
        int bg = hovered ? blendColor(style.bg, style.border, 0.35F) : style.bg;
        ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
        ctx.fill(x, y, x + w, y + 1, style.border);
        ctx.fill(x, y + h - 1, x + w, y + h, style.border);
        ctx.fill(x, y, x + 1, y + h, style.border);
        ctx.fill(x + w - 1, y, x + w, y + h, style.border);
        if (hovered) ctx.fill(x + 1, y + 1, x + w - 1, y + 2, addAlpha(style.border, 102));
        int tw = textRenderer.getWidth(label), tx = x + (w - tw) / 2, ty = y + (h - 8) / 2;
        ctx.drawText(textRenderer, label, tx + 1, ty + 1, 1426063360, false);
        ctx.drawText(textRenderer, label, tx, ty, hovered ? -1 : style.text, false);
    }

    private boolean hitTest(int mx, int my, int x, int y, int w, int h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
    private int blendColor(int c1, int c2, float t) { return -16777216 | ((int)((c1>>16&255)+((c2>>16&255)-(c1>>16&255))*t))<<16 | ((int)((c1>>8&255)+((c2>>8&255)-(c1>>8&255))*t))<<8 | (int)((c1&255)+((c2&255)-(c1&255))*t); }
    private int addAlpha(int color, int alpha) { return alpha << 24 | (color & 16777215); }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { MinecraftClient.getInstance().setScreen(parent); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}

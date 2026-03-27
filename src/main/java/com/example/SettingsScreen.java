package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SettingsScreen extends Screen {
    private static final int GUI_W = 540;
    private static final int GUI_H = 560;
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
        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(gX - 1, gY - 1, gX + GUI_W + 1, gY + GUI_H + 1, ModSettings.accentColor);
        ctx.fill(gX, gY, gX + GUI_W, gY + GUI_H, -267909104);
        ctx.fill(gX, gY, gX + GUI_W, gY + 48, -15921878);
        ctx.fill(gX, gY + 46, gX + GUI_W, gY + 47, ModSettings.accentColor);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§b§l⚙ USTAWIENIA"), gX + 270, gY + 17, ModSettings.accentColor2);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§8UkrainskiReader v1.0"), gX + 8, gY + 33, -12303258);
        int y = gY + 54, btnW = 120, btnH = 20, gap = 6;

        y = renderSection(ctx, gX, y, "§f▌ §b§lKOLOR INTERFEJSU");
        ctx.fill(gX + GUI_W - 54, y - 14, gX + GUI_W - 12, y - 1, ModSettings.accentColor);
        ModSettings.ColorPreset[] presets = ModSettings.ColorPreset.values();
        for (int i = 0; i < presets.length; i++) {
            ModSettings.ColorPreset p = presets[i];
            boolean active = ModSettings.accentColor == p.accent;
            int bX = gX + 20 + (i % 4) * (btnW + gap), bY = y + (i / 4) * (btnH + gap);
            renderBtn(ctx, (active ? "§l✔ " : "") + p.label, active ? GuiButton.Style.BLUE : GuiButton.Style.WHITE, bX, bY, btnW, btnH, hitTest(mouseX, mouseY, bX, bY, btnW, btnH));
        }
        y += (int) Math.ceil((double) presets.length / 4.0D) * (btnH + gap) + 12;

        y = renderSection(ctx, gX, y, "§f▌ §b§lTRYB FILTROWANIA");
        ctx.drawTextWithShadow(textRenderer, Text.literal(ModSettings.fullSetOnly ? "§aPełny set: helm+napierśnik+spodnie+buty+broń" : "§7Dowolny item: chociaż 1 netherytowy"), gX + 14, y, -7829351);
        y += 12;
        ctx.drawTextWithShadow(textRenderer, Text.literal(ModSettings.showElytra ? "§aWykrywaj elytra: WŁ" : "§7Wykrywaj elytra: WYŁ"), gX + 14, y, -7829351);
        y += 14;
        int fBtnW = 245;
        renderBtn(ctx, ModSettings.fullSetOnly ? "§l✔ Pełny set: WŁ" : "Pełny set: WYŁ", ModSettings.fullSetOnly ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20, y, fBtnW, btnH, hitTest(mouseX, mouseY, gX + 20, y, fBtnW, btnH));
        renderBtn(ctx, ModSettings.showElytra ? "§l✔ Elytra: WŁ" : "Elytra: WYŁ", ModSettings.showElytra ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20 + fBtnW + 10, y, fBtnW, btnH, hitTest(mouseX, mouseY, gX + 20 + fBtnW + 10, y, fBtnW, btnH));
        y += btnH + 14;

        y = renderSection(ctx, gX, y, "§f▌ §e§lOPCJE GUI");
        ctx.drawTextWithShadow(textRenderer, Text.literal(ModSettings.performanceMode ? "§aPerformance: max FPS" : "§7Performance: wyłączony"), gX + 14, y, -7829351);
        y += 12;
        ctx.drawTextWithShadow(textRenderer, Text.literal(ModSettings.closeOnTpa ? "§aZamknij po TPA: WŁ" : "§7Zamknij po TPA: WYŁ"), gX + 14, y, -7829351);
        y += 14;
        int gBtnW = 245;
        renderBtn(ctx, ModSettings.performanceMode ? "§l✔ Performance: WŁ" : "Performance: WYŁ", ModSettings.performanceMode ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 20, y, gBtnW, btnH, hitTest(mouseX, mouseY, gX + 20, y, gBtnW, btnH));
        renderBtn(ctx, ModSettings.closeOnTpa ? "§l✔ Zamknij po TPA: WŁ" : "Zamknij po TPA: WYŁ", ModSettings.closeOnTpa ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20 + gBtnW + 10, y, gBtnW, btnH, hitTest(mouseX, mouseY, gX + 20 + gBtnW + 10, y, gBtnW, btnH));
        y += btnH + 14;

        y = renderSection(ctx, gX, y, "§f▌ §c§lBLACKLIST");
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7Zablokowanych: §f" + BlacklistTracker.getAll().size()), gX + 14, y, -7829351);
        y += 14;
        int blBtnW = 220, blBtnX = gX + 270 - blBtnW / 2;
        renderBtn(ctx, "⛔ Zarządzaj Blacklistą", GuiButton.Style.RED, blBtnX, y, blBtnW, btnH, hitTest(mouseX, mouseY, blBtnX, y, blBtnW, btnH));

        ctx.fill(gX + 10, gY + GUI_H - 50, gX + GUI_W - 10, gY + GUI_H - 49, -14540220);
        int backX = gX + 270 - 60, backY = gY + GUI_H - 40;
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
        int y = gY + 54, btnW = 120, btnH = 20, gap = 6;
        y += 18; // sectionSkip
        ModSettings.ColorPreset[] presets = ModSettings.ColorPreset.values();
        for (int i = 0; i < presets.length; i++) {
            int bX = gX + 20 + (i % 4) * (btnW + gap), bY = y + (i / 4) * (btnH + gap);
            if (hitTest(mx, my, bX, bY, btnW, btnH)) { ModSettings.setColor(presets[i]); return true; }
        }
        y += (int) Math.ceil((double) presets.length / 4.0D) * (btnH + gap) + 12;
        y += 18; y += 26; // sectionSkip + desc
        int fBtnW = 245;
        if (hitTest(mx, my, gX + 20, y, fBtnW, btnH)) { ModSettings.fullSetOnly = !ModSettings.fullSetOnly; return true; }
        if (hitTest(mx, my, gX + 20 + fBtnW + 10, y, fBtnW, btnH)) { ModSettings.showElytra = !ModSettings.showElytra; return true; }
        y += btnH + 14; y += 18; y += 26; // sectionSkip + desc
        int gBtnW = 245;
        if (hitTest(mx, my, gX + 20, y, gBtnW, btnH)) { ModSettings.performanceMode = !ModSettings.performanceMode; return true; }
        if (hitTest(mx, my, gX + 20 + gBtnW + 10, y, gBtnW, btnH)) { ModSettings.closeOnTpa = !ModSettings.closeOnTpa; return true; }
        y += btnH + 14; y += 18; y += 14; // sectionSkip + desc
        int blBtnW = 220, blBtnX = gX + 270 - blBtnW / 2;
        if (hitTest(mx, my, blBtnX, y, blBtnW, btnH)) { MinecraftClient.getInstance().setScreen(new BlacklistScreen(this)); return true; }
        int backX = gX + 270 - 60, backY = gY + GUI_H - 40;
        if (hitTest(mx, my, backX, backY, 120, 22)) { MinecraftClient.getInstance().setScreen(parent); return true; }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int renderSection(DrawContext ctx, int gX, int y, String label) {
        ctx.fill(gX + 10, y, gX + GUI_W - 10, y + 1, ModSettings.accentColor);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), gX + 14, y + 4, ModSettings.accentColor2);
        return y + 18;
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

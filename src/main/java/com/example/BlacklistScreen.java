package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class BlacklistScreen extends Screen {
    private static final int GUI_W = 500;
    private static final int GUI_H = 420;
    private final Screen parent;
    private long openTime;
    private int guiX;
    private int guiY;
    private TextFieldWidget searchField;
    private String searchQuery = "";
    private int scroll = 0;
    private static final int ENTRY_H = 28;
    private static final int ENTRY_GAP = 3;
    private static final int LIST_TOP_OFFSET = 90;
    private final List<ButtonWidget> unlockButtons = new ArrayList<>();

    public BlacklistScreen(Screen parent) {
        super(Text.literal("Blacklist"));
        this.parent = parent;
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        this.guiX = (this.width - GUI_W) / 2;
        this.guiY = (this.height - GUI_H) / 2;

        this.searchField = new TextFieldWidget(this.textRenderer, guiX + 14, guiY + 55, 472, 22, Text.literal("Szukaj..."));
        this.searchField.setMaxLength(64);
        this.searchField.setChangedListener(s -> {
            this.searchQuery = s;
            this.scroll = 0;
            this.rebuildButtons();
        });
        this.addDrawableChild(this.searchField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("§f← Powrót"), b -> {
            MinecraftClient.getInstance().setScreen(this.parent);
        }).dimensions(guiX + 250 - 60, guiY + GUI_H - 35, 120, 22).build());

        this.rebuildButtons();
    }

    private void rebuildButtons() {
        for (ButtonWidget btn : unlockButtons) {
            this.remove(btn);
        }
        unlockButtons.clear();

        List<String> filtered = getFiltered();
        int listTop = guiY + LIST_TOP_OFFSET;
        int listH = 280;
        int y = listTop + 4 - scroll;

        for (String name : filtered) {
            if (y + ENTRY_H > listTop && y < listTop + listH) {
                ButtonWidget btn = ButtonWidget.builder(Text.literal("§aOdblokuj"), b -> {
                    BlacklistTracker.remove(name);
                    this.scroll = Math.max(0, this.scroll - 31);
                    this.rebuildButtons();
                }).dimensions(guiX + GUI_W - 110, y + 4, 90, 20).build();
                unlockButtons.add(btn);
                this.addDrawableChild(btn);
            }
            y += 31;
        }
    }

    private List<String> getFiltered() {
        List<String> result = new ArrayList<>();
        String q = searchQuery.toLowerCase().trim();
        for (String name : BlacklistTracker.getAll()) {
            if (q.isEmpty() || name.toLowerCase().contains(q)) {
                result.add(name);
            }
        }
        return result;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        float ease = MathHelper.clamp((float)(System.currentTimeMillis() - openTime) / 250.0F, 0.0F, 1.0F);
        ease = 1.0F - (1.0F - ease) * (1.0F - ease);
        int offY = (int)((1.0F - ease) * 20.0F);
        int gX = guiX;
        int gY = guiY + offY;

        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(gX - 1, gY - 1, gX + GUI_W + 1, gY + GUI_H + 1, ModSettings.accentColor);
        ctx.fill(gX, gY, gX + GUI_W, gY + GUI_H, -267909104);
        ctx.fill(gX, gY, gX + GUI_W, gY + 48, -15921878);
        ctx.fill(gX, gY + 46, gX + GUI_W, gY + 47, ModSettings.accentColor);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§c§l⛔ BLACKLIST"), gX + 250, gY + 17, -48060);

        List<String> filtered = getFiltered();
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7Zablokowanych: §f" + BlacklistTracker.getAll().size() +
            (searchQuery.isEmpty() ? "" : " §8(filtr: " + filtered.size() + ")")), gX + 14, gY + 33, -7829351);

        searchField.setY(gY + 55);
        int listTop = gY + LIST_TOP_OFFSET;
        int listH = 280;
        ctx.fill(gX + 10, listTop, gX + GUI_W - 10, listTop + listH, -16316657);
        ctx.fill(gX + 10, listTop, gX + GUI_W - 10, listTop + 1, -14013846);
        ctx.enableScissor(gX + 10, listTop, gX + GUI_W - 10, listTop + listH);

        if (filtered.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(
                searchQuery.isEmpty() ? "§7Brak zablokowanych graczy" : "§7Brak wyników dla: §f" + searchQuery),
                gX + 250, listTop + listH / 2 - 4, -10066313);
        }

        int y = listTop + 4 - scroll;
        for (String name : filtered) {
            if (y + ENTRY_H > listTop && y < listTop + listH) {
                ctx.fill(gX + 12, y, gX + GUI_W - 12, y + ENTRY_H, -16119270);
                ctx.fill(gX + 12, y, gX + 14, y + ENTRY_H, -3399134);
                ctx.drawTextWithShadow(textRenderer, Text.literal("§c⛔ §f" + name), gX + 20, y + 14 - 4, -3355427);
            }
            y += 31;
        }
        ctx.disableScissor();

        ctx.fill(gX + 10, gY + GUI_H - 45, gX + GUI_W - 10, gY + GUI_H - 44, -14540220);
        super.render(ctx, mouseX, mouseY, delta);
        this.rebuildButtons();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        this.scroll = Math.max(0, this.scroll - (int)(amount * 15.0D));
        this.rebuildButtons();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            MinecraftClient.getInstance().setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

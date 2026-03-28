package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class GearReaderScreen extends Screen {
    private static final int GUI_W = 860, GUI_H = 480;
    private Tab currentTab = Tab.PLAYERS;
    private long openTime;
    private int leftScroll = 0, rightScroll = 0, watchScroll = 0, blackScroll = 0;
    private int guiX, guiY, panelH, lastMX, lastMY;

    private TextFieldWidget watchlistInput;
    private TextFieldWidget blacklistInput;

    public GearReaderScreen() {
        super(Text.literal("Gear Reader"));
        openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        guiX = (width - GUI_W) / 2;
        guiY = (height - GUI_H) / 2;
        panelH = 408;

        watchlistInput = new TextFieldWidget(textRenderer, guiX + 14, guiY + 84, 200, 18, Text.literal("Nick..."));
        watchlistInput.setMaxLength(32);
        watchlistInput.setVisible(false);
        addDrawableChild(watchlistInput);

        blacklistInput = new TextFieldWidget(textRenderer, guiX + 14, guiY + 84, 200, 18, Text.literal("Nick..."));
        blacklistInput.setMaxLength(32);
        blacklistInput.setVisible(false);
        addDrawableChild(blacklistInput);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        lastMX = mouseX;
        lastMY = mouseY;
        float ease = MathHelper.clamp((float)(System.currentTimeMillis() - openTime) / 300.0F, 0.0F, 1.0F);
        ease = 1.0F - (1.0F - ease) * (1.0F - ease);
        int gY = guiY + (int)((1.0F - ease) * 30.0F);

        renderBackground(ctx);
        drawBorderGlow(ctx, guiX - 2, gY - 2, GUI_W + 4, GUI_H + 4);

        ctx.fill(guiX, gY, guiX + GUI_W, gY + GUI_H, -267909104);
        fillGradientH(ctx, guiX, gY, GUI_W, 48, -15921878, -15066550);
        ctx.fill(guiX, gY + 46, guiX + GUI_W, gY + 47, ModSettings.accentColor);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§b§lGEAR READER"), guiX + GUI_W / 2, gY + 17, ModSettings.accentColor2);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7RTP Detector"), guiX + 8, gY + 33, -10066313);

        renderTabBar(ctx, mouseX, mouseY, guiX, gY);
        int pY = gY + 52;

        // Zarządzaj widocznością pól
        watchlistInput.setVisible(currentTab == Tab.WATCHLIST);
        blacklistInput.setVisible(currentTab == Tab.BLACKLIST);
        if (currentTab != Tab.WATCHLIST) watchlistInput.setFocused(false);
        if (currentTab != Tab.BLACKLIST) blacklistInput.setFocused(false);

        switch (currentTab) {
            case PLAYERS -> {
                renderLeftPanel(ctx, mouseX, mouseY, guiX, pY);
                renderRightPanel(ctx, mouseX, mouseY, guiX, pY);
                ctx.fill(guiX + 429, gY + 48, guiX + 431, gY + GUI_H - 20, -14013846);
            }
            case WATCHLIST -> renderWatchlistPanel(ctx, mouseX, mouseY, guiX, pY);
            case BLACKLIST -> renderBlacklistPanel(ctx, mouseX, mouseY, guiX, pY);
        }

        ctx.fill(guiX, gY + GUI_H - 20, guiX + GUI_W, gY + GUI_H, -15592918);
        ctx.fill(guiX, gY + GUI_H - 21, guiX + GUI_W, gY + GUI_H - 20, -14013846);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7[ §fESC §7- Zamknij ]"), guiX + GUI_W / 2, gY + GUI_H - 13, -10066313);
        renderInlineBtn(ctx, "⚙", GuiButton.Style.WHITE, guiX + GUI_W - 26, gY + 2, 22, 18, hitTest(mouseX, mouseY, guiX + GUI_W - 26, gY + 2, 22, 18));

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderTabBar(DrawContext ctx, int mx, int my, int gX, int gY) {
        int ty = gY + 2;
        int rtpCount = DisappearedTracker.getCount();
        String playersLabel = rtpCount > 0 ? "GRACZE §c(" + rtpCount + " RTP)" : "GRACZE";
        renderInlineBtn(ctx, playersLabel, currentTab == Tab.PLAYERS ? GuiButton.Style.BLUE : GuiButton.Style.WHITE, gX + 4, ty, 160, 18, hitTest(mx, my, gX + 4, ty, 160, 18));
        renderInlineBtn(ctx, "★ WATCHLIST", currentTab == Tab.WATCHLIST ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 168, ty, 110, 18, hitTest(mx, my, gX + 168, ty, 110, 18));
        renderInlineBtn(ctx, "⛔ BLACKLIST", currentTab == Tab.BLACKLIST ? GuiButton.Style.RED : GuiButton.Style.WHITE, gX + 282, ty, 110, 18, hitTest(mx, my, gX + 282, ty, 110, 18));
    }

    // ===== LEWY PANEL: GRACZE W ZASIĘGU =====
    private void renderLeftPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<PlayerData> players = PlayerTracker.getCurrentList();
        int pX = gX + 6, pW = 418, cTop = pY + 28, cBot = pY + panelH;
        fillGradientH(ctx, pX, pY, pW, 28, -15921878, -15198144);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -14013846);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f§l👥 GRACZE W ZASIĘGU"), pX + 8, pY + 8, ModSettings.accentColor2);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + players.size() + ")"), pX + pW - textRenderer.getWidth("(" + players.size() + ")") - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        if (players.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Brak graczy w zasięgu"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }
        int y = cTop + 4 - leftScroll;
        for (PlayerData pd : players) {
            if (y + 70 > cTop && y < cBot) renderEntry(ctx, pd, pX + 4, y, pW - 8, null, mx >= pX && mx <= pX + pW && my >= y && my <= y + 70, false, pX, pW, cTop, cBot);
            y += 74;
        }
        ctx.disableScissor();
    }

    // ===== PRAWY PANEL: RTP WYKRYCI =====
    private void renderRightPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<DisappearedTracker.DisappearedEntry> list = DisappearedTracker.getDisappeared();
        int pX = gX + 436, pW = 418, cTop = pY + 28, cBot = pY + panelH - 24;
        fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c§l🚀 RTP WYKRYCI"), pX + 8, pY + 8, -48060);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + list.size() + ")"), pX + pW - textRenderer.getWidth("(" + list.size() + ")") - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        if (list.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Brak wykrytych RTP"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 10, -10066313);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§8(gracz musi zniknąć przy buttonie)"), pX + pW / 2, cTop + (cBot - cTop) / 2 + 4, -12303258);
        }
        int y = cTop + 4 - rightScroll;
        for (DisappearedTracker.DisappearedEntry entry : list) {
            if (y + 70 > cTop && y < cBot) renderEntry(ctx, entry.data, pX + 4, y, pW - 8, entry.getTimeAgo(), mx >= pX && mx <= pX + pW && my >= y && my <= y + 70, true, pX, pW, cTop, cBot);
            y += 74;
        }
        ctx.disableScissor();
        ctx.fill(pX, cBot, pX + pW, pY + panelH, -16119270);
        ctx.fill(pX, cBot - 1, pX + pW, cBot, -14013846);
        int clearX = pX + pW / 2 - 45, clearY = cBot + 4;
        renderInlineBtn(ctx, "✖ WYCZYŚĆ", GuiButton.Style.RED, clearX, clearY, 90, 16, hitTest(mx, my, clearX, clearY, 90, 16));
    }

    // ===== WATCHLIST =====
    private void renderWatchlistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
        int pX = gX + 6, pW = GUI_W - 12, cTop = pY + 56, cBot = pY + panelH;

        fillGradientH(ctx, pX, pY, pW, 28, -14015232, -12568576);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7838208);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l★ WATCHLIST"), pX + 8, pY + 8, -8960);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + all.size() + ")"), pX + pW - textRenderer.getWidth("(" + all.size() + ")") - 6, pY + 8, -10066313);

        // Pole do wpisywania
        watchlistInput.setX(pX + 8);
        watchlistInput.setY(pY + 32);

        // Przycisk Dodaj
        int addX = pX + 8 + 206, addY = pY + 32;
        renderInlineBtn(ctx, "§a+ Dodaj", GuiButton.Style.GREEN, addX, addY, 60, 18, hitTest(mx, my, addX, addY, 60, 18));

        // Podpowiedź
        ctx.drawTextWithShadow(textRenderer, Text.literal("§8Wpisz nick i kliknij Dodaj lub Enter"), pX + 280, pY + 36, -12303258);

        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Watchlist pusta - dodaj nick powyżej lub kliknij §e★"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }
        int y = cTop + 4 - watchScroll;
        for (WatchlistTracker.WatchlistEntry entry : all) {
            if (y + 70 > cTop && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
                ctx.fill(pX + 4, y, pX + pW - 4, y + 70, hov ? -15658712 : -16119270);
                ctx.fill(pX + 4, y, pX + 6, y + 70, -22016);
                ctx.fill(pX + 4, y, pX + pW - 4, y + 1, -10075136);
                renderHead(ctx, entry.data, pX + 10, y + 3, 26);
                String playerType = ItemChecker.getPlayerType(entry.data);
                ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l" + entry.data.name + " " + playerType), pX + 44, y + 4, -8960);
                ctx.drawTextWithShadow(textRenderer, Text.literal("§7Dodano: " + entry.getTimeAgo()), pX + 44, y + 15, -10066313);
                float hp = entry.data.health / 2.0F, maxHp = entry.data.maxHealth / 2.0F;
                ctx.drawTextWithShadow(textRenderer, Text.literal("§c❤ §f" + (int) hp + "/" + (int) maxHp), pX + 44, y + 26, -3355427);
                int iY = y + 40, iX = pX + 44;
                renderItemSlot(ctx, entry.data.helmet, iX, iY);
                renderItemSlot(ctx, entry.data.chestplate, iX + 20, iY);
                renderItemSlot(ctx, entry.data.leggings, iX + 40, iY);
                renderItemSlot(ctx, entry.data.boots, iX + 60, iY);
                ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
                renderItemSlot(ctx, entry.data.mainHand, iX + 88, iY);
                renderItemSlot(ctx, entry.data.offHand, iX + 108, iY);
                int btnY = y + 48, btnX = pX + pW - 160;
                renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, btnX, btnY, 40, 18, hitTest(mx, my, btnX, btnY, 40, 18));
                renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, btnX + 44, btnY, 50, 18, hitTest(mx, my, btnX + 44, btnY, 50, 18));
                renderInlineBtn(ctx, "✖ USUŃ", GuiButton.Style.ORANGE, btnX + 98, btnY, 55, 18, hitTest(mx, my, btnX + 98, btnY, 55, 18));
            }
            y += 74;
        }
        ctx.disableScissor();
    }

    // ===== BLACKLIST =====
    private void renderBlacklistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<String> all = new ArrayList<>(BlacklistTracker.getAll());
        int pX = gX + 6, pW = GUI_W - 12, entH = 32, cTop = pY + 56, cBot = pY + panelH;

        fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c§l⛔ BLACKLIST"), pX + 8, pY + 8, -48060);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + all.size() + ")"), pX + pW - textRenderer.getWidth("(" + all.size() + ")") - 6, pY + 8, -10066313);

        // Pole do wpisywania
        blacklistInput.setX(pX + 8);
        blacklistInput.setY(pY + 32);

        // Przycisk Dodaj
        int addX = pX + 8 + 206, addY = pY + 32;
        renderInlineBtn(ctx, "§c+ Dodaj", GuiButton.Style.RED, addX, addY, 60, 18, hitTest(mx, my, addX, addY, 60, 18));

        // Podpowiedź
        ctx.drawTextWithShadow(textRenderer, Text.literal("§8Wpisz nick i kliknij Dodaj lub Enter"), pX + 280, pY + 36, -12303258);

        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Blacklista jest pusta - dodaj nick powyżej"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }
        int y = cTop + 4 - blackScroll;
        for (String name : all) {
            if (y + entH > cTop && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + entH;
                ctx.fill(pX + 4, y, pX + pW - 4, y + entH, hov ? -15658718 : -16119270);
                ctx.fill(pX + 4, y, pX + 6, y + entH, -3399134);
                ctx.fill(pX + 4, y, pX + pW - 4, y + 1, -14013846);
                ctx.drawTextWithShadow(textRenderer, Text.literal("§c⛔ §f" + name), pX + 12, y + entH / 2 - 4, -3355427);
                renderInlineBtn(ctx, "Odblokuj", GuiButton.Style.GREEN, pX + pW - 100, y + 6, 90, 20, hitTest(mx, my, pX + pW - 100, y + 6, 90, 20));
            }
            y += entH + 4;
        }
        ctx.disableScissor();
    }

    // ===== ENTRY RENDER =====
    private void renderEntry(DrawContext ctx, PlayerData pd, int x, int y, int w, String timeAgo, boolean hover, boolean isRtp, int pX, int pW, int cTop, int cBot) {
        int bgColor = isRtp ? (hover ? -13369395 : -14408612) : (hover ? -15658712 : -16119270);
        int accent = isRtp ? -48060 : ModSettings.accentColor;
        ctx.fill(x, y, x + w, y + 70, bgColor);
        ctx.fill(x, y, x + 2, y + 70, accent);
        ctx.fill(x, y, x + w, y + 1, -14013846);
        renderHead(ctx, pd, x + 4, y + 3, 26);
        String playerType = ItemChecker.getPlayerType(pd);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l" + pd.name + " " + playerType), x + 38, y + 4, -22016);
        if (timeAgo != null) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("§c§l🚀 RTP! §7" + timeAgo), x + 38, y + 15, -39356);
        } else {
            ctx.drawTextWithShadow(textRenderer, Text.literal("§7W zasięgu"), x + 38, y + 15, -10066313);
        }
        float hp = pd.health / 2.0F, maxHp = pd.maxHealth / 2.0F, abs = pd.absorption / 2.0F;
        float ratio = maxHp > 0 ? hp / maxHp : 0;
        int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c❤ §f" + (int) hp + "/" + (int) maxHp + (abs > 0 ? " §e(+" + (int) abs + ")" : "")), x + 38, y + 26, hpCol);
        int iY = y + 40, iX = x + 38;
        renderItemSlot(ctx, pd.helmet, iX, iY);
        renderItemSlot(ctx, pd.chestplate, iX + 20, iY);
        renderItemSlot(ctx, pd.leggings, iX + 40, iY);
        renderItemSlot(ctx, pd.boots, iX + 60, iY);
        ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
        renderItemSlot(ctx, pd.mainHand, iX + 88, iY);
        renderItemSlot(ctx, pd.offHand, iX + 108, iY);
        if (isRtp) {
            int bX = x + w - 180, btnY = y + 48;
            boolean watched = WatchlistTracker.isWatched(pd.name);
            renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, bX, btnY, 40, 18, hitTest(lastMX, lastMY, bX, btnY, 40, 18));
            renderInlineBtn(ctx, "⛔", GuiButton.Style.RED, bX + 44, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 44, btnY, 24, 18));
            renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, bX + 72, btnY, 50, 18, hitTest(lastMX, lastMY, bX + 72, btnY, 50, 18));
            renderInlineBtn(ctx, watched ? "★" : "☆", watched ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, bX + 126, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 126, btnY, 24, 18));
        }
    }

    // ===== MOUSE CLICK =====
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX, my = (int) mouseY, gX = guiX, gY = guiY, pY = gY + 52;

        // Watchlist - przycisk Dodaj
        if (currentTab == Tab.WATCHLIST) {
            int pX = gX + 6, addX = pX + 8 + 206, addY = pY + 32;
            if (hitTest(mx, my, addX, addY, 60, 18)) {
                addWatchlistFromInput();
                return true;
            }
        }

        // Blacklist - przycisk Dodaj
        if (currentTab == Tab.BLACKLIST) {
            int pX = gX + 6, addX = pX + 8 + 206, addY = pY + 32;
            if (hitTest(mx, my, addX, addY, 60, 18)) {
                addBlacklistFromInput();
                return true;
            }
        }

        // Settings
        if (hitTest(mx, my, gX + GUI_W - 26, gY + 2, 22, 18)) {
            MinecraftClient.getInstance().setScreen(new SettingsScreen(this));
            return true;
        }

        // Tabs
        if (hitTest(mx, my, gX + 4, gY + 2, 160, 18)) { currentTab = Tab.PLAYERS; return true; }
        if (hitTest(mx, my, gX + 168, gY + 2, 110, 18)) { currentTab = Tab.WATCHLIST; return true; }
        if (hitTest(mx, my, gX + 282, gY + 2, 110, 18)) { currentTab = Tab.BLACKLIST; return true; }

        if (currentTab == Tab.PLAYERS) {
            // Przycisk WYCZYŚĆ
            int rpX = gX + 436, rpW = 418;
            int cBot = pY + panelH - 24;
            int clearX = rpX + rpW / 2 - 45, clearY = cBot + 4;
            if (hitTest(mx, my, clearX, clearY, 90, 16)) {
                DisappearedTracker.clearAll();
                rightScroll = 0;
                return true;
            }

            // RTP przyciski (prawy panel)
            int cTop = pY + 28;
            List<DisappearedTracker.DisappearedEntry> list = DisappearedTracker.getDisappeared();
            int y = cTop + 4 - rightScroll;
            for (DisappearedTracker.DisappearedEntry entry : list) {
                if (y + 70 > cTop && y < cBot) {
                    int bX = rpX + 4 + (rpW - 8) - 180, btnY = y + 48;
                    if (hitTest(mx, my, bX, btnY, 40, 18)) { sendCommand("tpa " + entry.data.name); return true; }
                    if (hitTest(mx, my, bX + 44, btnY, 24, 18)) { BlacklistTracker.add(entry.data.name); DisappearedTracker.remove(entry.data.name); return true; }
                    if (hitTest(mx, my, bX + 72, btnY, 50, 18)) { sendCommand("stats " + entry.data.name); close(); return true; }
                    if (hitTest(mx, my, bX + 126, btnY, 24, 18)) { toggleWatchlist(entry.data); return true; }
                }
                y += 74;
            }
        } else if (currentTab == Tab.WATCHLIST) {
            int pX = gX + 6, pW = GUI_W - 12, cTop = pY + 56, cBot = pY + panelH;
            List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
            int y = cTop + 4 - watchScroll;
            for (WatchlistTracker.WatchlistEntry entry : all) {
                if (y + 70 > cTop && y < cBot) {
                    int btnX = pX + pW - 160, btnY = y + 48;
                    if (hitTest(mx, my, btnX, btnY, 40, 18)) { sendCommand("tpa " + entry.data.name); return true; }
                    if (hitTest(mx, my, btnX + 44, btnY, 50, 18)) { sendCommand("stats " + entry.data.name); close(); return true; }
                    if (hitTest(mx, my, btnX + 98, btnY, 55, 18)) { WatchlistTracker.remove(entry.data.name); return true; }
                }
                y += 74;
            }
        } else if (currentTab == Tab.BLACKLIST) {
            int pX = gX + 6, pW = GUI_W - 12, entH = 32, cTop = pY + 56, cBot = pY + panelH;
            List<String> all = new ArrayList<>(BlacklistTracker.getAll());
            int y = cTop + 4 - blackScroll;
            for (String name : all) {
                if (y + entH > cTop && y < cBot && hitTest(mx, my, pX + pW - 100, y + 6, 90, 20)) {
                    BlacklistTracker.remove(name);
                    return true;
                }
                y += entH + 4;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ===== KEYBOARD =====
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter dodaje nick
        if (keyCode == 257) {
            if (currentTab == Tab.WATCHLIST && watchlistInput.isFocused()) {
                addWatchlistFromInput();
                return true;
            }
            if (currentTab == Tab.BLACKLIST && blacklistInput.isFocused()) {
                addBlacklistFromInput();
                return true;
            }
        }

        // ESC
        if (keyCode == 256) {
            if (watchlistInput.isFocused()) { watchlistInput.setFocused(false); return true; }
            if (blacklistInput.isFocused()) { blacklistInput.setFocused(false); return true; }
            close();
            return true;
        }

        // Forward do aktywnego pola
        if (currentTab == Tab.WATCHLIST && watchlistInput.isFocused()) {
            return watchlistInput.keyPressed(keyCode, scanCode, modifiers);
        }
        if (currentTab == Tab.BLACKLIST && blacklistInput.isFocused()) {
            return blacklistInput.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ===== SCROLL =====
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (currentTab == Tab.PLAYERS) {
            if (mouseX < guiX + 430) leftScroll = Math.max(0, leftScroll - (int)(amount * 20));
            else rightScroll = Math.max(0, rightScroll - (int)(amount * 20));
        } else if (currentTab == Tab.WATCHLIST) {
            watchScroll = Math.max(0, watchScroll - (int)(amount * 20));
        } else {
            blackScroll = Math.max(0, blackScroll - (int)(amount * 20));
        }
        return true;
    }

    // ===== HELPERS =====
    private void addWatchlistFromInput() {
        String name = watchlistInput.getText().trim();
        if (!name.isEmpty()) {
            PlayerData pd = new PlayerData(null);
            pd.name = name;
            WatchlistTracker.add(pd);
            watchlistInput.setText("");
        }
    }

    private void addBlacklistFromInput() {
        String name = blacklistInput.getText().trim();
        if (!name.isEmpty()) {
            BlacklistTracker.add(name);
            blacklistInput.setText("");
        }
    }

    private void sendCommand(String cmd) {
        if (MinecraftClient.getInstance().player != null)
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(cmd);
    }

    private void toggleWatchlist(PlayerData pd) {
        if (WatchlistTracker.isWatched(pd.name)) WatchlistTracker.remove(pd.name);
        else WatchlistTracker.add(pd);
    }

    private void renderHead(DrawContext ctx, PlayerData pd, int x, int y, int size) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Identifier skin = null;
            if (client.getNetworkHandler() != null)
                for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList())
                    if (entry.getProfile().getName().equals(pd.name)) { skin = entry.getSkinTexture(); break; }
            if (skin == null) skin = new Identifier("textures/entity/player/wide/steve.png");
            ctx.fill(x - 2, y - 2, x + size + 2, y + size + 2, ModSettings.accentColor);
            ctx.fill(x - 1, y - 1, x + size + 1, y + size + 1, -16448240);
            ctx.drawTexture(skin, x, y, size, size, 8.0F, 8.0F, 8, 8, 64, 64);
            ctx.drawTexture(skin, x, y, size, size, 40.0F, 8.0F, 8, 8, 64, 64);
        } catch (Exception e) {
            ctx.fill(x, y, x + size, y + size, -13417370);
        }
    }

    private void renderItemSlot(DrawContext ctx, ItemStack stack, int x, int y) {
        if (stack != null && !stack.isEmpty()) {
            if (ItemChecker.isNetherite(stack)) ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x55AA00AA);
            else if (ItemChecker.isLeatherArmor(stack)) ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x55FFAA00);
            else if (ItemChecker.isTrident(stack)) ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x5500AAFF);
            else if (ItemChecker.hasCustomModelData(stack)) ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x55FF00FF);
            ctx.drawItem(stack, x, y);
        } else {
            ctx.fill(x, y, x + 16, y + 16, -15921894);
        }
    }

    private void renderInlineBtn(DrawContext ctx, String label, GuiButton.Style style, int x, int y, int w, int h, boolean hovered) {
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

    private void fillGradientH(DrawContext ctx, int x, int y, int w, int h, int cL, int cR) {
        for (int i = 0; i < w; i++) {
            float t = (float) i / w;
            ctx.fill(x + i, y, x + i + 1, y + h,
                lerp(cL >> 24 & 255, cR >> 24 & 255, t) << 24 |
                lerp(cL >> 16 & 255, cR >> 16 & 255, t) << 16 |
                lerp(cL >> 8 & 255, cR >> 8 & 255, t) << 8 |
                lerp(cL & 255, cR & 255, t));
        }
    }

    private void drawBorderGlow(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + 1, 1152035583);
        ctx.fill(x, y + h, x + w, y + h + 1, 1152035583);
        ctx.fill(x, y, x + 1, y + h, 1152035583);
        ctx.fill(x + w, y, x + w + 1, y + h, 1152035583);
        ctx.fill(x + 1, y + 1, x + w - 1, y + 2, ModSettings.accentColor);
        ctx.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, ModSettings.accentColor);
        ctx.fill(x + 1, y + 1, x + 2, y + h - 1, ModSettings.accentColor);
        ctx.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, ModSettings.accentColor);
    }

    private int lerp(int a, int b, float t) { return (int)(a + (b - a) * t); }
    private int blendColor(int c1, int c2, float t) { return -16777216 | lerp(c1 >> 16 & 255, c2 >> 16 & 255, t) << 16 | lerp(c1 >> 8 & 255, c2 >> 8 & 255, t) << 8 | lerp(c1 & 255, c2 & 255, t); }
    private int addAlpha(int color, int alpha) { return alpha << 24 | (color & 16777215); }
    private boolean hitTest(int mx, int my, int x, int y, int w, int h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }

    @Override
    public boolean shouldPause() { return false; }

    private enum Tab { PLAYERS, WATCHLIST, BLACKLIST }
}

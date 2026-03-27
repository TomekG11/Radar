package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class GearReaderScreen extends Screen {
    private static final int GUI_W = 860;
    private static final int GUI_H = 480;

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
        super(Text.literal("UkrainskiReader"));
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        this.guiX = (this.width - GUI_W) / 2;
        this.guiY = (this.height - GUI_H) / 2;
        this.panelH = 408;
    }

    public static void onChatMessage(String message) {
        if (ModSettings.closeOnTpa) {
            if (message.contains("zaakceptował") || message.contains("accepted")) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.currentScreen instanceof GearReaderScreen) {
                    client.execute(() -> client.setScreen(null));
                }
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.lastMX = mouseX;
        this.lastMY = mouseY;
        int gY;
        if (!ModSettings.performanceMode) {
            float elapsed = (float)(System.currentTimeMillis() - openTime) / 300.0F;
            animProgress = MathHelper.clamp(elapsed, 0.0F, 1.0F);
            float ease = 1.0F - (1.0F - animProgress) * (1.0F - animProgress);
            gY = guiY + (int)((1.0F - ease) * 30.0F);
        } else {
            animProgress = 1.0F;
            gY = guiY;
        }
        renderMain(ctx, mouseX, mouseY, guiX, gY);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderMain(DrawContext ctx, int mx, int my, int gX, int gY) {
        if (!ModSettings.performanceMode) {
            this.renderBackground(ctx, mx, my, 0);
            drawBorderGlow(ctx, gX - 2, gY - 2, GUI_W + 4, GUI_H + 4);
        } else {
            ctx.fill(0, 0, this.width, this.height, -1442840576);
        }

        ctx.fill(gX, gY, gX + GUI_W, gY + GUI_H, -267909104);
        fillGradientH(ctx, gX, gY, GUI_W, 48, -15921878, -15066550);
        ctx.fill(gX, gY + 46, gX + GUI_W, gY + 47, ModSettings.accentColor);

        int titleW = textRenderer.getWidth("UKRAINSKI READER");
        ctx.drawTextWithShadow(textRenderer, Text.literal("§b§lUKRAINSKI READER"), gX + 430 - titleW / 2, gY + 17, ModSettings.accentColor2);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7By smiciak"), gX + 8, gY + 33, -10066313);
        String modeStr = ModSettings.fullSetOnly ? "§aPełny set" : "§7Dowolny item";
        ctx.drawTextWithShadow(textRenderer, Text.literal("§8Tryb: " + modeStr), gX + GUI_W - 150, gY + 33, -10066313);

        renderTabBar(ctx, mx, my, gX, gY);
        int pY = gY + 52;

        if (currentTab == Tab.PLAYERS) {
            renderLeftPanel(ctx, mx, my, gX, pY);
            renderRightPanel(ctx, mx, my, gX, pY);
            ctx.fill(gX + 430 - 1, gY + 48, gX + 430, gY + GUI_H - 20, -14013846);
            ctx.fill(gX + 430, gY + 48, gX + 430 + 1, gY + GUI_H - 20, -15066566);
            int sX = gX + 430 + 6;
            int sY = gY + GUI_H - 40;
            renderInlineBtn(ctx, "✖ WYCZYŚĆ", GuiButton.Style.RED, sX, sY, 90, 16, hitTest(mx, my, sX, sY, 90, 16));
        } else if (currentTab == Tab.WATCHLIST) {
            renderWatchlistPanel(ctx, mx, my, gX, pY);
        } else {
            renderBlacklistPanel(ctx, mx, my, gX, pY);
        }

        ctx.fill(gX, gY + GUI_H - 20, gX + GUI_W, gY + GUI_H, -15592918);
        ctx.fill(gX, gY + GUI_H - 21, gX + GUI_W, gY + GUI_H - 20, -14013846);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7[ §fESC §7- Zamknij ]"), gX + 430, gY + GUI_H - 13, -10066313);

        int sX = gX + GUI_W - 26;
        int sY = gY + 2;
        renderInlineBtn(ctx, "⚙", GuiButton.Style.WHITE, sX, sY, 22, 18, hitTest(mx, my, sX, sY, 22, 18));
    }

    private void renderTabBar(DrawContext ctx, int mx, int my, int gX, int gY) {
        int ty = gY + 2;
        int th = 18;
        renderInlineBtn(ctx, "GRACZE", currentTab == Tab.PLAYERS ? GuiButton.Style.BLUE : GuiButton.Style.WHITE, gX + 4, ty, 90, th, hitTest(mx, my, gX + 4, ty, 90, th));
        renderInlineBtn(ctx, "★ WATCHLIST", currentTab == Tab.WATCHLIST ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 98, ty, 110, th, hitTest(mx, my, gX + 98, ty, 110, th));
        renderInlineBtn(ctx, "⛔ BLACKLIST", currentTab == Tab.BLACKLIST ? GuiButton.Style.RED : GuiButton.Style.WHITE, gX + 212, ty, 110, th, hitTest(mx, my, gX + 212, ty, 110, th));
    }

    private void renderLeftPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<PlayerData> players = PlayerTracker.getCurrentList();
        int pX = gX + 6;
        int pW = 418;
        int cBot = pY + panelH;
        fillGradientH(ctx, pX, pY, pW, 28, -15921878, -15198144);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -14013846);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f§lGRACZE W ZASIĘGU"), pX + 8, pY + 8, ModSettings.accentColor2);
        String cnt = "(" + players.size() + ")";
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7" + cnt), pX + pW - textRenderer.getWidth(cnt) - 6, pY + 8, -10066313);
        ctx.fill(pX, pY + 28, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, pY + 28, pX + pW, cBot);

        if (players.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Brak graczy w zasięgu"), pX + pW / 2, pY + 28 + panelH / 2 - 4, -10066313);
        }

        int y = pY + 30 - leftScroll;
        for (PlayerData pd : players) {
            if (y + 70 > pY + 28 && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
                renderEntry(ctx, pd, pX + 4, y, pW - 8, null, hov, false, 0, 0, pY + 28, cBot);
            }
            y += 74;
        }
        ctx.disableScissor();
    }

    private void renderRightPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<DisappearedTracker.DisappearedEntry> list = new ArrayList<>(DisappearedTracker.getDisappeared());
        int pX = gX + 430 + 6;
        int pW = 418;
        int cTop = pY + 28;
        int cBot = pY + panelH - 24;
        fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c§lZNIKNIĘCI §7(RTP?)"), pX + 8, pY + 8, -48060);
        String cnt = "(" + list.size() + ")";
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7" + cnt), pX + pW - textRenderer.getWidth(cnt) - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);

        if (list.isEmpty()) {
            ctx.enableScissor(pX, cTop, pX + pW, cBot);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Brak znikniętych graczy"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
            ctx.disableScissor();
        }

        int y = pY + 30 - rightScroll;
        for (DisappearedTracker.DisappearedEntry entry : list) {
            if (y + 70 > cTop && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
                renderEntry(ctx, entry.data, pX + 4, y, pW - 8, entry.getTimeAgo(), hov, true, pX, pW, cTop, cBot);
            }
            y += 74;
        }

        ctx.fill(pX, cBot, pX + pW, pY + panelH, -16119270);
        ctx.fill(pX, cBot - 1, pX + pW, cBot, -14013846);
    }

    private void renderWatchlistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
        int pX = gX + 6;
        int pW = 848;
        int cTop = pY + 28;
        int cBot = pY + panelH;
        fillGradientH(ctx, pX, pY, pW, 28, -14015232, -12568576);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7838208);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l★ WATCHLIST"), pX + 8, pY + 8, -8960);
        String cnt = "(" + all.size() + ")";
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7" + cnt), pX + pW - textRenderer.getWidth(cnt) - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);

        if (all.isEmpty()) {
            ctx.enableScissor(pX, cTop, pX + pW, cBot);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Watchlist pusta - kliknij §e★§7 przy graczu"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
            ctx.disableScissor();
        }

        int y = pY + 30 - watchlistScroll;
        for (WatchlistTracker.WatchlistEntry entry : all) {
            if (y + 70 > cTop && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
                renderWatchlistEntry(ctx, mx, my, entry, pX + 4, y, pW - 8, hov, cTop, cBot);
            }
            y += 74;
        }
    }

    private void renderBlacklistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<String> all = new ArrayList<>(BlacklistTracker.getAll());
        int pX = gX + 6;
        int pW = 848;
        int entH = 32;
        int cTop = pY + 28;
        int cBot = pY + panelH;
        fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c§l⛔ BLACKLIST"), pX + 8, pY + 8, -48060);
        String cnt = "(" + all.size() + ")";
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7" + cnt), pX + pW - textRenderer.getWidth(cnt) - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);

        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Blacklista jest pusta"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }

        int y = pY + 30 - blacklistScroll;
        for (String name : all) {
            if (y + entH > cTop && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + entH;
                ctx.fill(pX + 4, y, pX + pW - 4, y + entH, hov ? -15658718 : -16119270);
                ctx.fill(pX + 4, y, pX + 6, y + entH, -3399134);
                ctx.fill(pX + 4, y, pX + pW - 4, y + 1, -14013846);
                ctx.drawTextWithShadow(textRenderer, Text.literal("§c⛔ §f" + name), pX + 12, y + entH / 2 - 4, -3355427);
                int bX = pX + pW - 100;
                int bY2 = y + 6;
                renderInlineBtn(ctx, "Odblokuj", GuiButton.Style.GREEN, bX, bY2, 90, 20, hitTest(mx, my, bX, bY2, 90, 20));
            }
            y += entH + 4;
        }
        ctx.disableScissor();
    }

    private void renderEntry(DrawContext ctx, PlayerData pd, int x, int y, int w, String timeAgo, boolean hover, boolean showButtons, int pX, int pW, int cTop, int cBot) {
        ctx.enableScissor(x, cTop, x + w, cBot);
        ctx.fill(x, y, x + w, y + 70, hover ? -15658712 : -16119270);
        ctx.fill(x, y, x + 2, y + 70, ModSettings.accentColor);
        ctx.fill(x, y, x + w, y + 1, -14013846);

        renderHead(ctx, pd, x + 4, y + 3, 26);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l" + pd.name), x + 38, y + 4, -22016);

        if (timeAgo != null) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("§c⏱" + timeAgo), x + 38, y + 15, -39356);
        }

        if (NetheriteChecker.hasElytra(pd)) {
            int ex = x + 38 + (timeAgo != null ? textRenderer.getWidth("⏱ " + timeAgo) + 4 : 0);
            ctx.drawTextWithShadow(textRenderer, Text.literal("§b\ud83e\ude82"), ex, y + 15, -16724737);
        }

        float hp = pd.health / 2.0F;
        float maxHp = pd.maxHealth / 2.0F;
        float abs = pd.absorption / 2.0F;
        float ratio = maxHp > 0.0F ? hp / maxHp : 0.0F;
        int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
        String hpTxt = (int) hp + "/" + (int) maxHp + (abs > 0.0F ? " (+" + (int) abs + ")" : "");
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c❤"), x + 38, y + 26, hpCol);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f " + hpTxt), x + 38 + 10, y + 26, -3355427);

        int iY = y + 37;
        int iX = x + 38;
        renderItemSlot(ctx, pd.helmet, iX, iY);
        renderItemSlot(ctx, pd.chestplate, iX + 20, iY);
        renderItemSlot(ctx, pd.leggings, iX + 40, iY);
        renderItemSlot(ctx, pd.boots, iX + 60, iY);
        ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
        renderItemSlot(ctx, pd.mainHand, iX + 88, iY);
        renderItemSlot(ctx, pd.offHand, iX + 108, iY);
        ctx.disableScissor();

        if (showButtons) {
            int btnY = y + 54;
            if (btnY + 18 > cTop && btnY < cBot) {
                int bX = pX + pW - 222;
                boolean watched = WatchlistTracker.isWatched(pd.name);
                renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, bX, btnY, 50, 18, hitTest(lastMX, lastMY, bX, btnY, 50, 18));
                renderInlineBtn(ctx, "BLOK", GuiButton.Style.RED, bX + 54, btnY, 50, 18, hitTest(lastMX, lastMY, bX + 54, btnY, 50, 18));
                renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, bX + 108, btnY, 50, 18, hitTest(lastMX, lastMY, bX + 108, btnY, 50, 18));
                renderInlineBtn(ctx, watched ? "★" : "☆", watched ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, bX + 162, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 162, btnY, 24, 18));
            }
        }
    }

    private void renderWatchlistEntry(DrawContext ctx, int mx, int my, WatchlistTracker.WatchlistEntry entry, int x, int y, int w, boolean hover, int cTop, int cBot) {
        ctx.enableScissor(x, cTop, x + w, cBot);
        ctx.fill(x, y, x + w, y + 70, hover ? -15658712 : -16119270);
        ctx.fill(x, y, x + 2, y + 70, -22016);
        ctx.fill(x, y, x + w, y + 1, -10075136);

        renderHead(ctx, entry.data, x + 4, y + 3, 26);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l" + entry.data.name), x + 38, y + 4, -8960);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7" + entry.getTimeAgo()), x + 38, y + 15, -10066313);

        float hp = entry.data.health / 2.0F;
        float maxHp = entry.data.maxHealth / 2.0F;
        float abs = entry.data.absorption / 2.0F;
        float ratio = maxHp > 0.0F ? hp / maxHp : 0.0F;
        int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
        String hpTxt = (int) hp + "/" + (int) maxHp + (abs > 0.0F ? " (+" + (int) abs + ")" : "");
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c❤"), x + 38, y + 26, hpCol);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f " + hpTxt), x + 38 + 10, y + 26, -3355427);

        int iY = y + 37;
        int iX = x + 38;
        renderItemSlot(ctx, entry.data.helmet, iX, iY);
        renderItemSlot(ctx, entry.data.chestplate, iX + 20, iY);
        renderItemSlot(ctx, entry.data.leggings, iX + 40, iY);
        renderItemSlot(ctx, entry.data.boots, iX + 60, iY);
        ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
        renderItemSlot(ctx, entry.data.mainHand, iX + 88, iY);
        renderItemSlot(ctx, entry.data.offHand, iX + 108, iY);
        ctx.disableScissor();

        int btnY = y + 54;
        if (btnY + 18 > cTop && btnY < cBot) {
            int btnX = x + w - 186;
            renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, btnX, btnY, 50, 18, hitTest(mx, my, btnX, btnY, 50, 18));
            renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, btnX + 54, btnY, 50, 18, hitTest(mx, my, btnX + 54, btnY, 50, 18));
            renderInlineBtn(ctx, "★ USUŃ", GuiButton.Style.ORANGE, btnX + 108, btnY, 70, 18, hitTest(mx, my, btnX + 108, btnY, 70, 18));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX;
        int my = (int) mouseY;
        int gX = guiX;
        int gY = guiY;
        int pY = gY + 52;

        // Settings button
        if (hitTest(mx, my, gX + GUI_W - 26, gY + 2, 22, 18)) {
            MinecraftClient.getInstance().setScreen(new SettingsScreen(this));
            return true;
        }
        // Tab buttons
        if (hitTest(mx, my, gX + 4, gY + 2, 90, 18)) { currentTab = Tab.PLAYERS; return true; }
        if (hitTest(mx, my, gX + 98, gY + 2, 110, 18)) { currentTab = Tab.WATCHLIST; return true; }
        if (hitTest(mx, my, gX + 212, gY + 2, 110, 18)) { currentTab = Tab.BLACKLIST; return true; }

        if (currentTab == Tab.PLAYERS) {
            // Clear button
            int sX = gX + 430 + 6;
            int cY = gY + GUI_H - 40;
            if (hitTest(mx, my, sX, cY, 90, 16)) {
                DisappearedTracker.clearAll();
                return true;
            }

            // Right panel buttons
            int rpX = gX + 430 + 6;
            int rpW = 418;
            int cTop = pY + 28;
            int cBot = pY + panelH - 24;
            List<DisappearedTracker.DisappearedEntry> list = new ArrayList<>(DisappearedTracker.getDisappeared());
            int y = pY + 30 - rightScroll;
            for (DisappearedTracker.DisappearedEntry entry : list) {
                int btnY = y + 54;
                if (btnY + 18 > cTop && btnY < cBot) {
                    int bX = rpX + rpW - 222;
                    if (hitTest(mx, my, bX, btnY, 50, 18)) {
                        if (MinecraftClient.getInstance().player != null) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpa " + entry.data.name);
                        }
                        return true;
                    }
                    if (hitTest(mx, my, bX + 54, btnY, 50, 18)) {
                        BlacklistTracker.add(entry.data.name);
                        DisappearedTracker.remove(entry.data.name);
                        return true;
                    }
                    if (hitTest(mx, my, bX + 108, btnY, 50, 18)) {
                        if (MinecraftClient.getInstance().player != null) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("stats " + entry.data.name);
                            MinecraftClient.getInstance().setScreen(null);
                        }
                        return true;
                    }
                    if (hitTest(mx, my, bX + 162, btnY, 24, 18)) {
                        if (WatchlistTracker.isWatched(entry.data.name)) {
                            WatchlistTracker.remove(entry.data.name);
                        } else {
                            WatchlistTracker.add(entry.data);
                        }
                        return true;
                    }
                }
                y += 74;
            }
        } else if (currentTab == Tab.WATCHLIST) {
            int wpX = gX + 6;
            int wpW = 848;
            int wCTop = pY + 28;
            int wCBot = pY + panelH;
            List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
            int wy = pY + 30 - watchlistScroll;
            for (WatchlistTracker.WatchlistEntry entry : all) {
                int btnY = wy + 54;
                if (btnY + 18 > wCTop && btnY < wCBot) {
                    int btnX = wpX + 4 + (wpW - 8) - 186;
                    if (hitTest(mx, my, btnX, btnY, 50, 18)) {
                        if (MinecraftClient.getInstance().player != null) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpa " + entry.data.name);
                        }
                        return true;
                    }
                    if (hitTest(mx, my, btnX + 54, btnY, 50, 18)) {
                        if (MinecraftClient.getInstance().player != null) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("stats " + entry.data.name);
                            MinecraftClient.getInstance().setScreen(null);
                        }
                        return true;
                    }
                    if (hitTest(mx, my, btnX + 108, btnY, 70, 18)) {
                        WatchlistTracker.remove(entry.data.name);
                        return true;
                    }
                }
                wy += 74;
            }
        } else if (currentTab == Tab.BLACKLIST) {
            int bpX = gX + 6;
            int bpW = 848;
            int entH = 32;
            int bCTop = pY + 28;
            int bCBot = pY + panelH;
            List<String> all = new ArrayList<>(BlacklistTracker.getAll());
            int by = pY + 30 - blacklistScroll;
            for (String name : all) {
                if (by + entH > bCTop && by < bCBot && hitTest(mx, my, bpX + bpW - 100, by + 6, 90, 20)) {
                    BlacklistTracker.remove(name);
                    return true;
                }
                by += entH + 4;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderHead(DrawContext ctx, PlayerData pd, int x, int y, int size) {
        if (ModSettings.performanceMode) {
            ctx.fill(x, y, x + size, y + size, -13417370);
            if (pd.name != null && !pd.name.isEmpty()) {
                ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(String.valueOf(pd.name.charAt(0)).toUpperCase()), x + size / 2, y + size / 2 - 4, ModSettings.accentColor2);
            }
        } else {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                Identifier skin = null;
                if (client.getNetworkHandler() != null) {
                    for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
                        if (entry.getProfile().getName().equals(pd.name)) {
                            skin = entry.getSkinTextures().texture();
                            break;
                        }
                    }
                }
                if (skin == null) {
                    skin = new Identifier("textures/entity/player/wide/steve.png");
                }
                ctx.fill(x - 2, y - 2, x + size + 2, y + size + 2, ModSettings.accentColor);
                ctx.fill(x - 1, y - 1, x + size + 1, y + size + 1, -16448240);
                ctx.drawTexture(skin, x, y, size, size, 8.0F, 8.0F, 8, 8, 64, 64);
                ctx.drawTexture(skin, x, y, size, size, 40.0F, 8.0F, 8, 8, 64, 64);
            } catch (Exception e) {
                ctx.fill(x, y, x + size, y + size, -13417370);
                if (pd.name != null && !pd.name.isEmpty()) {
                    ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(String.valueOf(pd.name.charAt(0)).toUpperCase()), x + size / 2, y + size / 2 - 4, -3355393);
                }
            }
        }
    }

    private void renderItemSlot(DrawContext ctx, ItemStack stack, int x, int y) {
        if (stack != null && !stack.isEmpty()) {
            if (!ModSettings.performanceMode) {
                if (NetheriteChecker.isNetherite(stack)) {
                    ctx.fill(x - 2, y - 2, x + 18, y + 18, 872393216);
                    ctx.fill(x - 1, y - 1, x + 17, y + 17, 1442818560);
                }
                if (NetheriteChecker.isElytra(stack)) {
                    ctx.fill(x - 2, y - 2, x + 18, y + 18, 855690495);
                    ctx.fill(x - 1, y - 1, x + 17, y + 17, 1426115839);
                }
            }
            ctx.drawItem(stack, x, y);
        } else {
            ctx.fill(x, y, x + 16, y + 16, -15921894);
            if (!ModSettings.performanceMode) {
                ctx.fill(x, y, x + 16, y + 1, -14013846);
                ctx.fill(x, y, x + 1, y + 16, -14013846);
            }
        }
    }

    private void renderInlineBtn(DrawContext ctx, String label, GuiButton.Style style, int x, int y, int w, int h, boolean hovered) {
        int bg = hovered ? blendColor(style.bg, style.border, 0.35F) : style.bg;
        ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
        ctx.fill(x, y, x + w, y + 1, style.border);
        ctx.fill(x, y + h - 1, x + w, y + h, style.border);
        ctx.fill(x, y, x + 1, y + h, style.border);
        ctx.fill(x + w - 1, y, x + w, y + h, style.border);
        if (hovered) {
            ctx.fill(x + 1, y + 1, x + w - 1, y + 2, addAlpha(style.border, 102));
        }
        int tw = textRenderer.getWidth(label);
        int tx = x + (w - tw) / 2;
        int ty = y + (h - 8) / 2;
        ctx.drawText(textRenderer, label, tx + 1, ty + 1, 1426063360, false);
        ctx.drawText(textRenderer, label, tx, ty, hovered ? -1 : style.text, false);
    }

    private void fillGradientH(DrawContext ctx, int x, int y, int w, int h, int cL, int cR) {
        if (ModSettings.performanceMode) {
            ctx.fill(x, y, x + w, y + h, cL);
        } else {
            for (int i = 0; i < w; i++) {
                float t = (float) i / (float) w;
                int r = lerp(cL >> 16 & 255, cR >> 16 & 255, t);
                int g = lerp(cL >> 8 & 255, cR >> 8 & 255, t);
                int b = lerp(cL & 255, cR & 255, t);
                int a = lerp(cL >> 24 & 255, cR >> 24 & 255, t);
                ctx.fill(x + i, y, x + i + 1, y + h, a << 24 | r << 16 | g << 8 | b);
            }
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

    private int lerp(int a, int b, float t) {
        return (int)((float) a + (float)(b - a) * t);
    }

    private int blendColor(int c1, int c2, float t) {
        return -16777216 | lerp(c1 >> 16 & 255, c2 >> 16 & 255, t) << 16 | lerp(c1 >> 8 & 255, c2 >> 8 & 255, t) << 8 | lerp(c1 & 255, c2 & 255, t);
    }

    private int addAlpha(int color, int alpha) {
        return alpha << 24 | (color & 16777215);
    }

    private boolean hitTest(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        if (currentTab == Tab.BLACKLIST) {
            blacklistScroll = Math.max(0, blacklistScroll - (int)(amount * 15.0D));
        } else if (currentTab == Tab.WATCHLIST) {
            watchlistScroll = Math.max(0, watchlistScroll - (int)(amount * 15.0D));
        } else if (mouseX < (double)(guiX + 430)) {
            leftScroll = Math.max(0, leftScroll - (int)(amount * 18.0D));
        } else {
            rightScroll = Math.max(0, rightScroll - (int)(amount * 18.0D));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private enum Tab {
        PLAYERS, WATCHLIST, BLACKLIST
    }
}

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
    private static final int GUI_W = 600, GUI_H = 480;
    private Tab currentTab = Tab.PLAYERS;
    private float animProgress = 0.0F;
    private long openTime;
    private int scroll = 0;
    private int guiX, guiY, panelH, lastMX, lastMY;

    public GearReaderScreen() {
        super(Text.literal("Gear Reader"));
        openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        guiX = (width - GUI_W) / 2;
        guiY = (height - GUI_H) / 2;
        panelH = 390;
    }

    public static void onChatMessage(String message) {
        if (ModSettings.closeOnTpa && (message.contains("zaakceptował") || message.contains("accepted"))) {
            MinecraftClient c = MinecraftClient.getInstance();
            if (c.currentScreen instanceof GearReaderScreen) c.execute(() -> c.setScreen(null));
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        lastMX = mouseX; lastMY = mouseY;
        int gY;
        if (!ModSettings.performanceMode) {
            float elapsed = (float)(System.currentTimeMillis() - openTime) / 300.0F;
            animProgress = MathHelper.clamp(elapsed, 0.0F, 1.0F);
            float ease = 1.0F - (1.0F - animProgress) * (1.0F - animProgress);
            gY = guiY + (int)((1.0F - ease) * 30.0F);
        } else { animProgress = 1.0F; gY = guiY; }
        renderMain(ctx, mouseX, mouseY, guiX, gY);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderMain(DrawContext ctx, int mx, int my, int gX, int gY) {
        if (!ModSettings.performanceMode) { renderBackground(ctx); drawBorderGlow(ctx, gX - 2, gY - 2, GUI_W + 4, GUI_H + 4); }
        else ctx.fill(0, 0, width, height, -1442840576);

        ctx.fill(gX, gY, gX + GUI_W, gY + GUI_H, -267909104);
        fillGradientH(ctx, gX, gY, GUI_W, 48, -15921878, -15066550);
        ctx.fill(gX, gY + 46, gX + GUI_W, gY + 47, ModSettings.accentColor);
        
        String title = currentTab == Tab.RTP ? "RTP DETECTOR" : "GEAR READER";
        int tw = textRenderer.getWidth(title);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§b§l" + title), gX + GUI_W/2 - tw/2, gY + 17, ModSettings.accentColor2);
        
        // Info
        int rtpCount = RtpTracker.getCount();
        String info = "§8Gracze: §f" + PlayerTracker.getCurrentList().size() + "  §8RTP: §c" + rtpCount;
        ctx.drawTextWithShadow(textRenderer, Text.literal(info), gX + 8, gY + 33, -10066313);
        
        renderTabBar(ctx, mx, my, gX, gY);
        int pY = gY + 52;

        switch (currentTab) {
            case PLAYERS -> renderPlayersPanel(ctx, mx, my, gX, pY);
            case RTP -> renderRtpPanel(ctx, mx, my, gX, pY);
            case WATCHLIST -> renderWatchlistPanel(ctx, mx, my, gX, pY);
            case BLACKLIST -> renderBlacklistPanel(ctx, mx, my, gX, pY);
        }

        ctx.fill(gX, gY + GUI_H - 20, gX + GUI_W, gY + GUI_H, -15592918);
        ctx.fill(gX, gY + GUI_H - 21, gX + GUI_W, gY + GUI_H - 20, -14013846);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7[ §fESC §7- Zamknij ]"), gX + GUI_W/2, gY + GUI_H - 13, -10066313);
        renderInlineBtn(ctx, "⚙", GuiButton.Style.WHITE, gX + GUI_W - 26, gY + 2, 22, 18, hitTest(mx, my, gX + GUI_W - 26, gY + 2, 22, 18));
    }

    private void renderTabBar(DrawContext ctx, int mx, int my, int gX, int gY) {
        int ty = gY + 2;
        int rtpCount = RtpTracker.getCount();
        String rtpLabel = rtpCount > 0 ? "🚀 RTP (" + rtpCount + ")" : "🚀 RTP";
        
        renderInlineBtn(ctx, "👥 GRACZE", currentTab == Tab.PLAYERS ? GuiButton.Style.BLUE : GuiButton.Style.WHITE, gX + 4, ty, 90, 18, hitTest(mx, my, gX + 4, ty, 90, 18));
        renderInlineBtn(ctx, rtpLabel, currentTab == Tab.RTP ? GuiButton.Style.RED : GuiButton.Style.WHITE, gX + 98, ty, 90, 18, hitTest(mx, my, gX + 98, ty, 90, 18));
        renderInlineBtn(ctx, "★ WATCH", currentTab == Tab.WATCHLIST ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 192, ty, 80, 18, hitTest(mx, my, gX + 192, ty, 80, 18));
        renderInlineBtn(ctx, "⛔ BLOCK", currentTab == Tab.BLACKLIST ? GuiButton.Style.RED : GuiButton.Style.WHITE, gX + 276, ty, 80, 18, hitTest(mx, my, gX + 276, ty, 80, 18));
    }

    private void renderPlayersPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<PlayerData> players = PlayerTracker.getCurrentList();
        int pX = gX + 6, pW = GUI_W - 12, cTop = pY + 28, cBot = pY + panelH;
        
        fillGradientH(ctx, pX, pY, pW, 28, -15921878, -15198144);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -14013846);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§f§l👥 GRACZE W ZASIĘGU"), pX + 8, pY + 8, ModSettings.accentColor2);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + players.size() + ")"), pX + pW - textRenderer.getWidth("(" + players.size() + ")") - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        
        if (players.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Brak graczy w zasięgu"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 12, -10066313);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§8(leather/netherite/trident/custom)"), pX + pW / 2, cTop + (cBot - cTop) / 2 + 2, -12303258);
        }
        
        int y = cTop + 4 - scroll;
        for (PlayerData pd : players) {
            if (y + 70 > cTop && y < cBot) {
                renderPlayerEntry(ctx, pd, pX + 4, y, pW - 8, null, mx >= pX && mx <= pX + pW && my >= y && my <= y + 70, true, pX, pW, cTop, cBot);
            }
            y += 74;
        }
        ctx.disableScissor();
    }

    private void renderRtpPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<RtpTracker.RtpEntry> rtpList = RtpTracker.getAll();
        int pX = gX + 6, pW = GUI_W - 12, cTop = pY + 28, cBot = pY + panelH;
        
        fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c§l🚀 RTP WYKRYCI"), pX + 8, pY + 8, -48060);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + rtpList.size() + ")"), pX + pW - textRenderer.getWidth("(" + rtpList.size() + ")") - 6, pY + 8, -10066313);
        
        // Przycisk wyczyść
        int clearX = pX + pW - 100, clearY = pY + 4;
        renderInlineBtn(ctx, "✖ WYCZYŚĆ", GuiButton.Style.RED, clearX, clearY, 90, 18, hitTest(mx, my, clearX, clearY, 90, 18));
        
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        
        if (rtpList.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Brak wykrytych RTP"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 12, -10066313);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§8(gracz musi kliknąć button i zniknąć)"), pX + pW / 2, cTop + (cBot - cTop) / 2 + 2, -12303258);
        }
        
        int y = cTop + 4 - scroll;
        for (RtpTracker.RtpEntry entry : rtpList) {
            if (y + 70 > cTop && y < cBot) {
                renderPlayerEntry(ctx, entry.data, pX + 4, y, pW - 8, entry.getTimeAgo(), mx >= pX && mx <= pX + pW && my >= y && my <= y + 70, true, pX, pW, cTop, cBot);
            }
            y += 74;
        }
        ctx.disableScissor();
    }

    private void renderWatchlistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
        int pX = gX + 6, pW = GUI_W - 12, cTop = pY + 28, cBot = pY + panelH;
        
        fillGradientH(ctx, pX, pY, pW, 28, -14015232, -12568576);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7838208);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l★ WATCHLIST"), pX + 8, pY + 8, -8960);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + all.size() + ")"), pX + pW - textRenderer.getWidth("(" + all.size() + ")") - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        
        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Watchlist pusta - kliknij §e★§7 przy graczu"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }
        
        int y = cTop + 4 - scroll;
        for (WatchlistTracker.WatchlistEntry entry : all) {
            if (y + 70 > cTop && y < cBot) {
                renderWatchlistEntry(ctx, mx, my, entry, pX + 4, y, pW - 8, mx >= pX && mx <= pX + pW && my >= y && my <= y + 70, cTop, cBot);
            }
            y += 74;
        }
        ctx.disableScissor();
    }

    private void renderBlacklistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<String> all = new ArrayList<>(BlacklistTracker.getAll());
        int pX = gX + 6, pW = GUI_W - 12, entH = 32, cTop = pY + 28, cBot = pY + panelH;
        
        fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c§l⛔ BLACKLIST"), pX + 8, pY + 8, -48060);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7(" + all.size() + ")"), pX + pW - textRenderer.getWidth("(" + all.size() + ")") - 6, pY + 8, -10066313);
        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);
        
        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Blacklista jest pusta"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }
        
        int y = cTop + 4 - scroll;
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

    private void renderPlayerEntry(DrawContext ctx, PlayerData pd, int x, int y, int w, String timeAgo, boolean hover, boolean showButtons, int pX, int pW, int cTop, int cBot) {
        ctx.fill(x, y, x + w, y + 70, hover ? -15658712 : -16119270);
        ctx.fill(x, y, x + 2, y + 70, ModSettings.accentColor);
        ctx.fill(x, y, x + w, y + 1, -14013846);
        renderHead(ctx, pd, x + 4, y + 3, 26);
        
        String playerType = ItemChecker.getPlayerType(pd);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l" + pd.name + " " + playerType), x + 38, y + 4, -22016);
        
        if (timeAgo != null) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("§c🚀 RTP " + timeAgo), x + 38, y + 15, -39356);
        } else if (pd.nearButton) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("§6🔘 Przy buttonie..."), x + 38, y + 15, -22016);
        } else {
            ctx.drawTextWithShadow(textRenderer, Text.literal("§7W zasięgu"), x + 38, y + 15, -10066313);
        }
        
        float hp = pd.health / 2.0F, maxHp = pd.maxHealth / 2.0F, abs = pd.absorption / 2.0F;
        float ratio = maxHp > 0 ? hp / maxHp : 0;
        int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c❤ §f" + (int)hp + "/" + (int)maxHp + (abs > 0 ? " §e(+" + (int)abs + ")" : "")), x + 38, y + 26, hpCol);
        
        int iY = y + 40, iX = x + 38;
        renderItemSlot(ctx, pd.helmet, iX, iY); 
        renderItemSlot(ctx, pd.chestplate, iX + 20, iY);
        renderItemSlot(ctx, pd.leggings, iX + 40, iY); 
        renderItemSlot(ctx, pd.boots, iX + 60, iY);
        ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
        renderItemSlot(ctx, pd.mainHand, iX + 88, iY); 
        renderItemSlot(ctx, pd.offHand, iX + 108, iY);
        
        if (showButtons && y + 70 > cTop && y < cBot) {
            int bX = x + w - 180;
            int btnY = y + 48;
            boolean watched = WatchlistTracker.isWatched(pd.name);
            renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, bX, btnY, 40, 18, hitTest(lastMX, lastMY, bX, btnY, 40, 18));
            renderInlineBtn(ctx, "⛔", GuiButton.Style.RED, bX + 44, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 44, btnY, 24, 18));
            renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, bX + 72, btnY, 50, 18, hitTest(lastMX, lastMY, bX + 72, btnY, 50, 18));
            renderInlineBtn(ctx, watched ? "★" : "☆", watched ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, bX + 126, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 126, btnY, 24, 18));
        }
    }

    private void renderWatchlistEntry(DrawContext ctx, int mx, int my, WatchlistTracker.WatchlistEntry entry, int x, int y, int w, boolean hover, int cTop, int cBot) {
        ctx.fill(x, y, x + w, y + 70, hover ? -15658712 : -16119270);
        ctx.fill(x, y, x + 2, y + 70, -22016);
        ctx.fill(x, y, x + w, y + 1, -10075136);
        renderHead(ctx, entry.data, x + 4, y + 3, 26);
        
        String playerType = ItemChecker.getPlayerType(entry.data);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§e§l" + entry.data.name + " " + playerType), x + 38, y + 4, -8960);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§7Dodano: " + entry.getTimeAgo()), x + 38, y + 15, -10066313);
        
        float hp = entry.data.health / 2.0F, maxHp = entry.data.maxHealth / 2.0F, abs = entry.data.absorption / 2.0F;
        float ratio = maxHp > 0 ? hp / maxHp : 0;
        int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
        ctx.drawTextWithShadow(textRenderer, Text.literal("§c❤ §f" + (int)hp + "/" + (int)maxHp + (abs > 0 ? " §e(+" + (int)abs + ")" : "")), x + 38, y + 26, hpCol);
        
        int iY = y + 40, iX = x + 38;
        renderItemSlot(ctx, entry.data.helmet, iX, iY); 
        renderItemSlot(ctx, entry.data.chestplate, iX + 20, iY);
        renderItemSlot(ctx, entry.data.leggings, iX + 40, iY); 
        renderItemSlot(ctx, entry.data.boots, iX + 60, iY);
        ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
        renderItemSlot(ctx, entry.data.mainHand, iX + 88, iY); 
        renderItemSlot(ctx, entry.data.offHand, iX + 108, iY);
        
        int btnY = y + 48;
        int btnX = x + w - 150;
        renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, btnX, btnY, 40, 18, hitTest(mx, my, btnX, btnY, 40, 18));
        renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, btnX + 44, btnY, 50, 18, hitTest(mx, my, btnX + 44, btnY, 50, 18));
        renderInlineBtn(ctx, "✖ USUŃ", GuiButton.Style.ORANGE, btnX + 98, btnY, 50, 18, hitTest(mx, my, btnX + 98, btnY, 50, 18));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX, my = (int) mouseY, gX = guiX, gY = guiY, pY = gY + 52;
        
        // Settings
        if (hitTest(mx, my, gX + GUI_W - 26, gY + 2, 22, 18)) { 
            MinecraftClient.getInstance().setScreen(new SettingsScreen(this)); 
            return true; 
        }
        
        // Tabs
        if (hitTest(mx, my, gX + 4, gY + 2, 90, 18)) { currentTab = Tab.PLAYERS; scroll = 0; return true; }
        if (hitTest(mx, my, gX + 98, gY + 2, 90, 18)) { currentTab = Tab.RTP; scroll = 0; return true; }
        if (hitTest(mx, my, gX + 192, gY + 2, 80, 18)) { currentTab = Tab.WATCHLIST; scroll = 0; return true; }
        if (hitTest(mx, my, gX + 276, gY + 2, 80, 18)) { currentTab = Tab.BLACKLIST; scroll = 0; return true; }

        int pX = gX + 6, pW = GUI_W - 12, cTop = pY + 28, cBot = pY + panelH;

        if (currentTab == Tab.PLAYERS) {
            List<PlayerData> players = PlayerTracker.getCurrentList();
            int y = cTop + 4 - scroll;
            for (PlayerData pd : players) {
                if (y + 70 > cTop && y < cBot) {
                    int bX = pX + 4 + (pW - 8) - 180;
                    int btnY = y + 48;
                    if (hitTest(mx, my, bX, btnY, 40, 18)) { sendCommand("tpa " + pd.name); return true; }
                    if (hitTest(mx, my, bX + 44, btnY, 24, 18)) { BlacklistTracker.add(pd.name); return true; }
                    if (hitTest(mx, my, bX + 72, btnY, 50, 18)) { sendCommand("stats " + pd.name); close(); return true; }
                    if (hitTest(mx, my, bX + 126, btnY, 24, 18)) { toggleWatchlist(pd); return true; }
                }
                y += 74;
            }
        } else if (currentTab == Tab.RTP) {
            // Przycisk wyczyść
            int clearX = pX + pW - 100, clearY = pY + 4;
            if (hitTest(mx, my, clearX, clearY, 90, 18)) { 
                RtpTracker.clearAll(); 
                scroll = 0;
                return true; 
            }
            
            List<RtpTracker.RtpEntry> rtpList = RtpTracker.getAll();
            int y = cTop + 4 - scroll;
            for (RtpTracker.RtpEntry entry : rtpList) {
                if (y + 70 > cTop && y < cBot) {
                    int bX = pX + 4 + (pW - 8) - 180;
                    int btnY = y + 48;
                    if (hitTest(mx, my, bX, btnY, 40, 18)) { sendCommand("tpa " + entry.data.name); return true; }
                    if (hitTest(mx, my, bX + 44, btnY, 24, 18)) { BlacklistTracker.add(entry.data.name); RtpTracker.remove(entry.data.name); return true; }
                    if (hitTest(mx, my, bX + 72, btnY, 50, 18)) { sendCommand("stats " + entry.data.name); close(); return true; }
                    if (hitTest(mx, my, bX + 126, btnY, 24, 18)) { toggleWatchlist(entry.data); return true; }
                }
                y += 74;
            }
        } else if (currentTab == Tab.WATCHLIST) {
            List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
            int y = cTop + 4 - scroll;
            for (WatchlistTracker.WatchlistEntry entry : all) {
                if (y + 70 > cTop && y < cBot) {
                    int btnX = pX + 4 + (pW - 8) - 150;
                    int btnY = y + 48;
                    if (hitTest(mx, my, btnX, btnY, 40, 18)) { sendCommand("tpa " + entry.data.name); return true; }
                    if (hitTest(mx, my, btnX + 44, btnY, 50, 18)) { sendCommand("stats " + entry.data.name); close(); return true; }
                    if (hitTest(mx, my, btnX + 98, btnY, 50, 18)) { WatchlistTracker.remove(entry.data.name); return true; }
                }
                y += 74;
            }
        } else if (currentTab == Tab.BLACKLIST) {
            List<String> all = new ArrayList<>(BlacklistTracker.getAll());
            int entH = 32;
            int y = cTop + 4 - scroll;
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
    
    private void sendCommand(String cmd) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(cmd);
        }
    }
    
    private void toggleWatchlist(PlayerData pd) {
        if (WatchlistTracker.isWatched(pd.name)) {
            WatchlistTracker.remove(pd.name);
        } else {
            WatchlistTracker.add(pd);
        }
    }

    private void renderHead(DrawContext ctx, PlayerData pd, int x, int y, int size) {
        if (ModSettings.performanceMode) {
            ctx.fill(x, y, x + size, y + size, -13417370);
            if (pd.name != null && !pd.name.isEmpty()) ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(String.valueOf(pd.name.charAt(0)).toUpperCase()), x + size / 2, y + size / 2 - 4, ModSettings.accentColor2);
        } else {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                Identifier skin = null;
                if (client.getNetworkHandler() != null) for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) if (entry.getProfile().getName().equals(pd.name)) { skin = entry.getSkinTexture(); break; }
                if (skin == null) skin = new Identifier("textures/entity/player/wide/steve.png");
                ctx.fill(x - 2, y - 2, x + size + 2, y + size + 2, ModSettings.accentColor);
                ctx.fill(x - 1, y - 1, x + size + 1, y + size + 1, -16448240);
                ctx.drawTexture(skin, x, y, size, size, 8.0F, 8.0F, 8, 8, 64, 64);
                ctx.drawTexture(skin, x, y, size, size, 40.0F, 8.0F, 8, 8, 64, 64);
            } catch (Exception e) {
                ctx.fill(x, y, x + size, y + size, -13417370);
                if (pd.name != null && !pd.name.isEmpty()) ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(String.valueOf(pd.name.charAt(0)).toUpperCase()), x + size / 2, y + size / 2 - 4, -3355393);
            }
        }
    }

    private void renderItemSlot(DrawContext ctx, ItemStack stack, int x, int y) {
        if (stack != null && !stack.isEmpty()) {
            if (ItemChecker.isNetherite(stack)) {
                ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x55AA00AA);
            } else if (ItemChecker.isLeatherArmor(stack)) {
                ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x55FFAA00);
            } else if (ItemChecker.isTrident(stack)) {
                ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x5500AAFF);
            } else if (ItemChecker.hasCustomModelData(stack)) {
                ctx.fill(x - 1, y - 1, x + 17, y + 17, 0x55FF00FF);
            }
            ctx.drawItem(stack, x, y);
        } else {
            ctx.fill(x, y, x + 16, y + 16, -15921894);
        }
    }

    private void renderInlineBtn(DrawContext ctx, String label, GuiButton.Style style, int x, int y, int w, int h, boolean hovered) {
        int bg = hovered ? blendColor(style.bg, style.border, 0.35F) : style.bg;
        ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
        ctx.fill(x, y, x + w, y + 1, style.border); ctx.fill(x, y + h - 1, x + w, y + h, style.border);
        ctx.fill(x, y, x + 1, y + h, style.border); ctx.fill(x + w - 1, y, x + w, y + h, style.border);
        if (hovered) ctx.fill(x + 1, y + 1, x + w - 1, y + 2, addAlpha(style.border, 102));
        int tw2 = textRenderer.getWidth(label), tx = x + (w - tw2) / 2, ty = y + (h - 8) / 2;
        ctx.drawText(textRenderer, label, tx + 1, ty + 1, 1426063360, false);
        ctx.drawText(textRenderer, label, tx, ty, hovered ? -1 : style.text, false);
    }

    private void fillGradientH(DrawContext ctx, int x, int y, int w, int h, int cL, int cR) {
        if (ModSettings.performanceMode) { ctx.fill(x, y, x + w, y + h, cL); return; }
        for (int i = 0; i < w; i++) { float t = (float) i / w; ctx.fill(x + i, y, x + i + 1, y + h, lerp(cL >> 24 & 255, cR >> 24 & 255, t) << 24 | lerp(cL >> 16 & 255, cR >> 16 & 255, t) << 16 | lerp(cL >> 8 & 255, cR >> 8 & 255, t) << 8 | lerp(cL & 255, cR & 255, t)); }
    }

    private void drawBorderGlow(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + 1, 1152035583); ctx.fill(x, y + h, x + w, y + h + 1, 1152035583);
        ctx.fill(x, y, x + 1, y + h, 1152035583); ctx.fill(x + w, y, x + w + 1, y + h, 1152035583);
        ctx.fill(x + 1, y + 1, x + w - 1, y + 2, ModSettings.accentColor); ctx.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, ModSettings.accentColor);
        ctx.fill(x + 1, y + 1, x + 2, y + h - 1, ModSettings.accentColor); ctx.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, ModSettings.accentColor);
    }

    private int lerp(int a, int b, float t) { return (int)(a + (b - a) * t); }
    private int blendColor(int c1, int c2, float t) { return -16777216 | lerp(c1 >> 16 & 255, c2 >> 16 & 255, t) << 16 | lerp(c1 >> 8 & 255, c2 >> 8 & 255, t) << 8 | lerp(c1 & 255, c2 & 255, t); }
    private int addAlpha(int color, int alpha) { return alpha << 24 | (color & 16777215); }
    private boolean hitTest(int mx, int my, int x, int y, int w, int h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll = Math.max(0, scroll - (int)(amount * 20));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
    
    private enum Tab { PLAYERS, RTP, WATCHLIST, BLACKLIST }
}

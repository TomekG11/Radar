package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * GearReaderScreen - wersja zmodyfikowana:
 * - Zakładki: PLAYERS (gracze w zasięgu), WATCHLIST (z polem wpisu na górze), BLACKLIST (z polem wpisu),
 *   RTP (prawa kolumna została jak wcześniej - wykryci zniknięci przy buttonie).
 * - Dodano: TextFieldWidget i ButtonWidget "Dodaj" dla Watchlist i Blacklist (ręczne dodawanie nicku).
 * - Pola i przyciski są renderowane tylko gdy odpowiednia zakładka aktywna, a wejścia są do nich
 *   forwardowane (kliknięcia/klawiatura).
 */
public class GearReaderScreen extends Screen {
    private static final int GUI_W = 860, GUI_H = 480;

    private enum Tab { PLAYERS, WATCHLIST, BLACKLIST }

    private Tab currentTab = Tab.PLAYERS;
    private long openTime;
    private int guiX, guiY, panelH;
    private int lastMX, lastMY;

    // Pola dla watchlist/blacklist (nie dodajemy jako drawable children globalnie - render i input forwardujemy ręcznie)
    private TextFieldWidget watchlistInput;
    private ButtonWidget watchlistAddBtn;
    private TextFieldWidget blacklistInput;
    private ButtonWidget blacklistAddBtn;

    public GearReaderScreen() {
        super(Text.literal("Gear Reader"));
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        this.guiX = (this.width - GUI_W) / 2;
        this.guiY = (this.height - GUI_H) / 2;
        this.panelH = 408;

        // Inicjalizacja pól (pozycje relatywne do guiX/guiY)
        int inputW = 240;
        int inputH = 20;
        int inputX = this.guiX + 14;
        int inputY = this.guiY + 55;

        this.watchlistInput = new TextFieldWidget(this.textRenderer, inputX, inputY, inputW, inputH, Text.literal("Dodaj nick..."));
        this.watchlistInput.setMaxLength(32);

        this.watchlistAddBtn = ButtonWidget.builder(Text.literal("Dodaj"), b -> {
            String name = this.watchlistInput.getText().trim();
            if (!name.isEmpty()) {
                PlayerData pd = new PlayerData(null);
                pd.name = name;
                WatchlistTracker.add(pd);
                this.watchlistInput.setText("");
            }
        }).dimensions(inputX + inputW + 6, inputY, 60, inputH).build();

        this.blacklistInput = new TextFieldWidget(this.textRenderer, inputX, inputY, inputW, inputH, Text.literal("Dodaj nick..."));
        this.blacklistInput.setMaxLength(32);

        this.blacklistAddBtn = ButtonWidget.builder(Text.literal("Dodaj"), b -> {
            String name = this.blacklistInput.getText().trim();
            if (!name.isEmpty()) {
                BlacklistTracker.add(name);
                this.blacklistInput.setText("");
            }
        }).dimensions(inputX + inputW + 6, inputY, 60, inputH).build();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.lastMX = mouseX;
        this.lastMY = mouseY;

        float ease = MathHelper.clamp((float)(System.currentTimeMillis() - this.openTime) / 300.0F, 0.0F, 1.0F);
        ease = 1.0F - (1.0F - ease) * (1.0F - ease);
        int gY = this.guiY + (int)((1.0F - ease) * 30.0F);

        this.renderBackground(ctx);
        this.drawBorderGlow(ctx, this.guiX - 2, gY - 2, GUI_W + 4, GUI_H + 4);

        // Tło i header
        ctx.fill(this.guiX, gY, this.guiX + GUI_W, gY + GUI_H, -267909104);
        this.fillGradientH(ctx, this.guiX, gY, GUI_W, 48, -15921878, -15066550);
        ctx.fill(this.guiX, gY + 46, this.guiX + GUI_W, gY + 47, ModSettings.accentColor);

        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§b§lGEAR READER"), this.guiX + GUI_W / 2, gY + 17, ModSettings.accentColor2);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§7By smiciak"), this.guiX + 8, gY + 33, -10066313);

        // Tab bar
        this.renderTabBar(ctx, mouseX, mouseY, this.guiX, gY);

        int pY = gY + 52;

        // Renderuje odpowiednią zawartość
        if (this.currentTab == Tab.PLAYERS) {
            this.renderPlayersPanel(ctx, mouseX, mouseY, this.guiX, pY);
        } else if (this.currentTab == Tab.WATCHLIST) {
            this.renderWatchlistPanel(ctx, mouseX, mouseY, this.guiX, pY);
        } else if (this.currentTab == Tab.BLACKLIST) {
            this.renderBlacklistPanel(ctx, mouseX, mouseY, this.guiX, pY);
        }

        // Footer
        ctx.fill(this.guiX, gY + GUI_H - 20, this.guiX + GUI_W, gY + GUI_H, -15592918);
        ctx.fill(this.guiX, gY + GUI_H - 21, this.guiX + GUI_W, gY + GUI_H - 20, -14013846);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7[ §fESC §7- Zamknij ]"), this.guiX + GUI_W / 2, gY + GUI_H - 13, -10066313);

        // Renderuj przycisk ustawień
        this.renderInlineBtn(ctx, "⚙", GuiButton.Style.WHITE, this.guiX + GUI_W - 26, gY + 2, 22, 18, hitTest(mouseX, mouseY, this.guiX + GUI_W - 26, gY + 2, 22, 18));
    }

    private void renderTabBar(DrawContext ctx, int mx, int my, int gX, int gY) {
        int ty = gY + 2;
        int rtpCount = DisappearedTracker.getCount();
        String playersLabel = rtpCount > 0 ? "GRACZE §c(" + rtpCount + " RTP)" : "GRACZE";

        this.renderInlineBtn(ctx, playersLabel, this.currentTab == Tab.PLAYERS ? GuiButton.Style.BLUE : GuiButton.Style.WHITE, gX + 4, ty, 180, 18, hitTest(mx, my, gX + 4, ty, 180, 18));
        this.renderInlineBtn(ctx, "★ WATCHLIST", this.currentTab == Tab.WATCHLIST ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 188, ty, 140, 18, hitTest(mx, my, gX + 188, ty, 140, 18));
        this.renderInlineBtn(ctx, "⛔ BLACKLIST", this.currentTab == Tab.BLACKLIST ? GuiButton.Style.RED : GuiButton.Style.WHITE, gX + 332, ty, 140, 18, hitTest(mx, my, gX + 332, ty, 140, 18));
    }

    private void renderPlayersPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<PlayerData> players = PlayerTracker.getCurrentList();
        int pX = gX + 6;
        int pW = 418;
        int cTop = pY + 28;
        int cBot = pY + this.panelH;

        this.fillGradientH(ctx, pX, pY, pW, 28, -15921878, -15198144);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -14013846);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§f§lGRACZE W ZASIĘGU"), pX + 8, pY + 8, ModSettings.accentColor2);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§7(" + players.size() + ")"), pX + pW - this.textRenderer.getWidth("(" + players.size() + ")") - 6, pY + 8, -10066313);

        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);

        if (players.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Brak graczy w zasięgu"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }

        int y = cTop + 4;
        for (PlayerData pd : players) {
            if (y + 70 > cTop && y < cBot) {
                this.renderEntry(ctx, pd, pX + 4, y, pW - 8, null, false, false, pX, pW, cTop, cBot);
            }
            y += 74;
        }

        ctx.disableScissor();
    }

    private void renderWatchlistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<WatchlistTracker.WatchlistEntry> all = new ArrayList<>(WatchlistTracker.getAll());
        int pX = gX + 6;
        int pW = GUI_W - 12;
        int cTop = pY + 28;
        int cBot = pY + this.panelH;

        this.fillGradientH(ctx, pX, pY, pW, 28, -14015232, -12568576);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7838208);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§e§l★ WATCHLIST"), pX + 8, pY + 8, -8960);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§7(" + all.size() + ")"), pX + pW - this.textRenderer.getWidth("(" + all.size() + ")") - 6, pY + 8, -10066313);

        // Render input field i przycisk (tylko w tej zakładce)
        if (this.currentTab == Tab.WATCHLIST) {
            this.watchlistInput.setX(this.guiX + 14);
            this.watchlistInput.setY(this.guiY + 55);
            this.watchlistInput.tick();
            this.watchlistInput.render(ctx, mx, my, 0f);
            this.watchlistAddBtn.setX(this.guiX + 14 + this.watchlistInput.getWidth() + 6);
            this.watchlistAddBtn.setY(this.guiY + 55);
            this.watchlistAddBtn.render(ctx, mx, my, 0f);
        }

        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);

        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Watchlist pusta - dodaj nick powyżej"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }

        int y = cTop + 4;
        for (WatchlistTracker.WatchlistEntry entry : all) {
            if (y + 70 > cTop && y < cBot) {
                this.renderWatchEntry(ctx, entry, pX + 4, y, pW - 8, false, pX, pW, cTop, cBot);
            }
            y += 74;
        }

        ctx.disableScissor();
    }

    private void renderBlacklistPanel(DrawContext ctx, int mx, int my, int gX, int pY) {
        List<String> all = new ArrayList<>(BlacklistTracker.getAll());
        int pX = gX + 6;
        int pW = GUI_W - 12;
        int cTop = pY + 28;
        int cBot = pY + this.panelH;

        this.fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
        ctx.fill(pX, pY + 27, pX + pW, pY + 28, -7851213);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§c§l⛔ BLACKLIST"), pX + 8, pY + 8, -48060);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§7(" + all.size() + ")"), pX + pW - this.textRenderer.getWidth("(" + all.size() + ")") - 6, pY + 8, -10066313);

        // Render input field i przycisk (tylko w tej zakładce)
        if (this.currentTab == Tab.BLACKLIST) {
            this.blacklistInput.setX(this.guiX + 14);
            this.blacklistInput.setY(this.guiY + 55);
            this.blacklistInput.tick();
            this.blacklistInput.render(ctx, mx, my, 0f);
            this.blacklistAddBtn.setX(this.guiX + 14 + this.blacklistInput.getWidth() + 6);
            this.blacklistAddBtn.setY(this.guiY + 55);
            this.blacklistAddBtn.render(ctx, mx, my, 0f);
        }

        ctx.fill(pX, cTop, pX + pW, cBot, -16316657);
        ctx.enableScissor(pX, cTop, pX + pW, cBot);

        if (all.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Blacklista jest pusta"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
        }

        int y = cTop + 4;
        for (String name : all) {
            if (y + 32 > cTop && y < cBot) {
                boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 32;
                ctx.fill(pX + 4, y, pX + pW - 4, y + 32, hov ? -15658718 : -16119270);
                ctx.fill(pX + 4, y, pX + 6, y + 32, -3399134);
                ctx.drawTextWithShadow(this.textRenderer, Text.literal("§c⛔ §f" + name), pX + 12, y + 16 - 4, -3355427);
                this.renderInlineBtn(ctx, "Odblokuj", GuiButton.Style.GREEN, pX + pW - 100, y + 6, 90, 20, hitTest(mx, my, pX + pW - 100, y + 6, 90, 20));
            }
            y += 36;
        }

        ctx.disableScissor();
    }

    private void renderEntry(DrawContext ctx, PlayerData pd, int x, int y, int w, String timeAgo, boolean hover, boolean isRtp, int pX, int pW, int cTop, int cBot) {
        int bgColor = hover ? -15658712 : -16119270;
        ctx.fill(x, y, x + w, y + 70, bgColor);
        ctx.fill(x, y, x + 2, y + 70, ModSettings.accentColor);
        ctx.fill(x, y, x + w, y + 1, -14013846);

        this.renderHead(ctx, pd, x + 4, y + 3, 26);

        String playerType = ItemChecker.getPlayerType(pd);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§e§l" + pd.name + " " + playerType), x + 38, y + 4, -22016);

        if (timeAgo != null) {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("§c🚀 RTP " + timeAgo), x + 38, y + 15, -39356);
        } else {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("§7W zasięgu"), x + 38, y + 15, -10066313);
        }

        float hp = pd.health / 2.0F, maxHp = pd.maxHealth / 2.0F, abs = pd.absorption / 2.0F;
        float ratio = maxHp > 0 ? hp / maxHp : 0;
        int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§c❤ §f" + (int)hp + "/" + (int)maxHp + (abs > 0 ? " §e(+" + (int)abs + ")" : "")), x + 38, y + 26, hpCol);

        int iY = y + 40, iX = x + 38;
        this.renderItemSlot(ctx, pd.helmet, iX, iY);
        this.renderItemSlot(ctx, pd.chestplate, iX + 20, iY);
        this.renderItemSlot(ctx, pd.leggings, iX + 40, iY);
        this.renderItemSlot(ctx, pd.boots, iX + 60, iY);
        ctx.fill(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
        this.renderItemSlot(ctx, pd.mainHand, iX + 88, iY);
        this.renderItemSlot(ctx, pd.offHand, iX + 108, iY);

        if (isRtp) {
            int bX = x + w - 180;
            int btnY = y + 48;
            boolean watched = WatchlistTracker.isWatched(pd.name);
            this.renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, bX, btnY, 40, 18, hitTest(lastMX, lastMY, bX, btnY, 40, 18));
            this.renderInlineBtn(ctx, "⛔", GuiButton.Style.RED, bX + 44, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 44, btnY, 24, 18));
            this.renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, bX + 72, btnY, 50, 18, hitTest(lastMX, lastMY, bX + 72, btnY, 50, 18));
            this.renderInlineBtn(ctx, watched ? "★" : "☆", watched ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, bX + 126, btnY, 24, 18, hitTest(lastMX, lastMY, bX + 126, btnY, 24, 18));
        }
    }

    private void renderWatchEntry(DrawContext ctx, WatchlistTracker.WatchlistEntry entry, int x, int y, int w, boolean hover, int pX, int pW, int cTop, int cBot) {
        this.renderEntry(ctx, entry.data, x, y, w, entry.getTimeAgo(), hover, false, pX, pW, cTop, cBot);
    }

    private void renderWatchEntry(DrawContext ctx, int mx, int my, WatchlistTracker.WatchlistEntry entry, int x, int y, int w, boolean hover, int cTop, int cBot) {
        renderWatchEntry(ctx, entry, x, y, w, hover, cTop, w, cTop, cBot);
    }

    private void renderHead(DrawContext ctx, PlayerData pd, int x, int y, int size) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Identifier skin = null;
            if (client.getNetworkHandler() != null) {
                for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
                    if (entry.getProfile().getName().equals(pd.name)) {
                        skin = entry.getSkinTexture();
                        break;
                    }
                }
            }
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
        int tw = this.textRenderer.getWidth(label);
        int tx = x + (w - tw) / 2;
        int ty = y + (h - 8) / 2;
        ctx.drawText(this.textRenderer, label, tx + 1, ty + 1, 1426063360, false);
        ctx.drawText(this.textRenderer, label, tx, ty, hovered ? -1 : style.text, false);
    }

    private void fillGradientH(DrawContext ctx, int x, int y, int w, int h, int cL, int cR) {
        if (ModSettings.performanceMode) { ctx.fill(x, y, x + w, y + h, cL); return; }
        for (int i = 0; i < w; i++) {
            float t = (float) i / (float) w;
            int r = lerp(cL >> 16 & 255, cR >> 16 & 255, t);
            int g = lerp(cL >> 8 & 255, cR >> 8 & 255, t);
            int b = lerp(cL & 255, cR & 255, t);
            int a = lerp(cL >> 24 & 255, cR >> 24 & 255, t);
            ctx.fill(x + i, y, x + i + 1, y + h, a << 24 | r << 16 | g << 8 | b);
        }
    }

    private void drawBorderGlow(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + 1, 1152035583);
        ctx.fill(x, y + h, x + w, y + h + 1, 1152035583);
        ctx.fill(x, y, x + 1, y + h, 1152035583);
        ctx.fill(x + w, y, x + w + 1, y + h, 1152035583);
        ctx.fill(x + 1, y + 1, x + w - 1, y + 2, ModSettings.accentColor);
        ctx.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, ModSettings.accentColor);
    }

    private int lerp(int a, int b, float t) { return (int)((float)a + (float)(b - a) * t); }
    private int blendColor(int c1, int c2, float t) { return -16777216 | lerp(c1 >> 16 & 255, c2 >> 16 & 255, t) << 16 | lerp(c1 >> 8 & 255, c2 >> 8 & 255, t) << 8 | lerp(c1 & 255, c2 & 255, t); }
    private int addAlpha(int color, int alpha) { return alpha << 24 | color & 16777215; }
    private boolean hitTest(int mx, int my, int x, int y, int w, int h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }

    // Input forwarding: mouseClicked -> mogą obsłużyć pola i przyciski (tylko dla aktywnej zakładki)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Najpierw forward do inputów jeśli aktywne
        if (this.currentTab == Tab.WATCHLIST) {
            if (this.watchlistInput.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.watchlistAddBtn.mouseClicked(mouseX, mouseY, button)) return true;
        }
        if (this.currentTab == Tab.BLACKLIST) {
            if (this.blacklistInput.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.blacklistAddBtn.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // Klawiatura: forward dla aktywnego pola
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.currentTab == Tab.WATCHLIST) {
            if (this.watchlistInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        if (this.currentTab == Tab.BLACKLIST) {
            if (this.blacklistInput.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.currentTab == Tab.WATCHLIST) {
            if (this.watchlistInput.charTyped(chr, modifiers)) return true;
        }
        if (this.currentTab == Tab.BLACKLIST) {
            if (this.blacklistInput.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }
}

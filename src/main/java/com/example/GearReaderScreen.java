package com.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_640;

public class GearReaderScreen extends class_437 {
   private static final int COLOR_BG = -267909104;
   private static final int COLOR_PANEL_DARK = -16316657;
   private static final int COLOR_HEADER = -15592918;
   private static final int COLOR_BORDER = -14013846;
   private static final int COLOR_TEXT_DIM = -10066313;
   private static final int COLOR_NETHERITE_BG = 872393216;
   private static final int COLOR_ENTRY_BG = -16119270;
   private static final int COLOR_ENTRY_HOVER = -15658712;
   private static final int COLOR_TIME = -39356;
   private static final int COLOR_STAR = -8960;
   private static final int COLOR_STAR_ACTIVE = -22016;
   private static final int COLOR_HP_HIGH = -16711868;
   private static final int COLOR_HP_MID = -13312;
   private static final int COLOR_HP_LOW = -56798;
   private static final int GUI_W = 860;
   private static final int GUI_H = 480;
   private static final int EH = 70;
   private static final int EGAP = 4;
   private static final int PAD = 6;
   private static final int HEAD = 26;
   private static final int TX = 38;
   private GearReaderScreen.Tab currentTab;
   private float animProgress;
   private long openTime;
   private int leftScroll;
   private int rightScroll;
   private int blacklistScroll;
   private int watchlistScroll;
   private int guiX;
   private int guiY;
   private int panelH;
   private int lastMX;
   private int lastMY;

   public GearReaderScreen() {
      super(class_2561.method_43470("UkrainskiReader"));
      this.currentTab = GearReaderScreen.Tab.PLAYERS;
      this.animProgress = 0.0F;
      this.leftScroll = 0;
      this.rightScroll = 0;
      this.blacklistScroll = 0;
      this.watchlistScroll = 0;
      this.openTime = System.currentTimeMillis();
   }

   protected void method_25426() {
      super.method_25426();
      this.guiX = (this.field_22789 - 860) / 2;
      this.guiY = (this.field_22790 - 480) / 2;
      this.panelH = 408;
   }

   public static void onChatMessage(String message) {
      if (ModSettings.closeOnTpa) {
         if (message.contains("zaakceptował") || message.contains("accepted")) {
            class_310 client = class_310.method_1551();
            if (client.field_1755 instanceof GearReaderScreen) {
               client.execute(() -> {
                  client.method_1507((class_437)null);
               });
            }
         }

      }
   }

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
         ctx.method_25294(0, 0, this.field_22789, this.field_22790, -1442840576);
      }

      ctx.method_25294(gX, gY, gX + 860, gY + 480, -267909104);
      this.fillGradientH(ctx, gX, gY, 860, 48, -15921878, -15066550);
      ctx.method_25294(gX, gY + 46, gX + 860, gY + 47, ModSettings.accentColor);
      int titleW = this.field_22793.method_1727("UKRAINSKI READER");
      ctx.method_27535(this.field_22793, class_2561.method_43470("§b§lUKRAINSKI READER"), gX + 430 - titleW / 2, gY + 17, ModSettings.accentColor2);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7By smiciak"), gX + 8, gY + 33, -10066313);
      String modeStr = ModSettings.fullSetOnly ? "§aPełny set" : "§7Dowolny item";
      ctx.method_27535(this.field_22793, class_2561.method_43470("§8Tryb: " + modeStr), gX + 860 - 150, gY + 33, -10066313);
      this.renderTabBar(ctx, mx, my, gX, gY);
      int pY = gY + 52;
      int sX;
      int sY;
      if (this.currentTab == GearReaderScreen.Tab.PLAYERS) {
         this.renderLeftPanel(ctx, mx, my, gX, pY);
         this.renderRightPanel(ctx, mx, my, gX, pY);
         ctx.method_25294(gX + 430 - 1, gY + 48, gX + 430, gY + 480 - 20, -14013846);
         ctx.method_25294(gX + 430, gY + 48, gX + 430 + 1, gY + 480 - 20, -15066566);
         sX = gX + 430 + 6;
         sY = gY + 480 - 40;
         this.renderInlineBtn(ctx, "✖ WYCZYŚĆ", GuiButton.Style.RED, sX, sY, 90, 16, this.hitTest(mx, my, sX, sY, 90, 16));
      } else if (this.currentTab == GearReaderScreen.Tab.WATCHLIST) {
         this.renderWatchlistPanel(ctx, mx, my, gX, pY);
      } else {
         this.renderBlacklistPanel(ctx, mx, my, gX, pY);
      }

      ctx.method_25294(gX, gY + 480 - 20, gX + 860, gY + 480, -15592918);
      ctx.method_25294(gX, gY + 480 - 21, gX + 860, gY + 480 - 20, -14013846);
      ctx.method_27534(this.field_22793, class_2561.method_43470("§7[ §fESC §7- Zamknij ]"), gX + 430, gY + 480 - 13, -10066313);
      sX = gX + 860 - 26;
      sY = gY + 2;
      this.renderInlineBtn(ctx, "⚙", GuiButton.Style.WHITE, sX, sY, 22, 18, this.hitTest(mx, my, sX, sY, 22, 18));
   }

   private void renderTabBar(class_332 ctx, int mx, int my, int gX, int gY) {
      int ty = gY + 2;
      int th = 18;
      boolean hP = this.hitTest(mx, my, gX + 4, ty, 90, th);
      boolean hW = this.hitTest(mx, my, gX + 98, ty, 110, th);
      boolean hB = this.hitTest(mx, my, gX + 212, ty, 110, th);
      this.renderInlineBtn(ctx, "GRACZE", this.currentTab == GearReaderScreen.Tab.PLAYERS ? GuiButton.Style.BLUE : GuiButton.Style.WHITE, gX + 4, ty, 90, th, hP);
      this.renderInlineBtn(ctx, "★ WATCHLIST", this.currentTab == GearReaderScreen.Tab.WATCHLIST ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 98, ty, 110, th, hW);
      this.renderInlineBtn(ctx, "⛔ BLACKLIST", this.currentTab == GearReaderScreen.Tab.BLACKLIST ? GuiButton.Style.RED : GuiButton.Style.WHITE, gX + 212, ty, 110, th, hB);
   }

   private void renderLeftPanel(class_332 ctx, int mx, int my, int gX, int pY) {
      List<PlayerData> players = PlayerTracker.getCurrentList();
      int pX = gX + 6;
      int pW = 418;
      int cBot = pY + this.panelH;
      this.fillGradientH(ctx, pX, pY, pW, 28, -15921878, -15198144);
      ctx.method_25294(pX, pY + 27, pX + pW, pY + 28, -14013846);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§f§lGRACZE W ZASIĘGU"), pX + 8, pY + 8, ModSettings.accentColor2);
      String cnt = "(" + players.size() + ")";
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7" + cnt), pX + pW - this.field_22793.method_1727(cnt) - 6, pY + 8, -10066313);
      ctx.method_25294(pX, pY + 28, pX + pW, cBot, -16316657);
      ctx.method_44379(pX, pY + 28, pX + pW, cBot);
      if (players.isEmpty()) {
         ctx.method_27534(this.field_22793, class_2561.method_43470("§7Brak graczy w zasięgu"), pX + pW / 2, pY + 28 + this.panelH / 2 - 4, -10066313);
      }

      int y = pY + 30 - this.leftScroll;

      for(Iterator var12 = players.iterator(); var12.hasNext(); y += 74) {
         PlayerData pd = (PlayerData)var12.next();
         if (y + 70 > pY + 28 && y < cBot) {
            boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
            this.renderEntry(ctx, pd, pX + 4, y, pW - 8, (String)null, hov, false, 0, 0, pY + 28, cBot);
         }
      }

      ctx.method_44380();
   }

   private void renderRightPanel(class_332 ctx, int mx, int my, int gX, int pY) {
      List<DisappearedTracker.DisappearedEntry> list = new ArrayList(DisappearedTracker.getDisappeared());
      int pX = gX + 430 + 6;
      int pW = 418;
      int cTop = pY + 28;
      int cBot = pY + this.panelH - 24;
      this.fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
      ctx.method_25294(pX, pY + 27, pX + pW, pY + 28, -7851213);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§c§lZNIKNIĘCI §7(RTP?)"), pX + 8, pY + 8, -48060);
      String cnt = "(" + list.size() + ")";
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7" + cnt), pX + pW - this.field_22793.method_1727(cnt) - 6, pY + 8, -10066313);
      ctx.method_25294(pX, cTop, pX + pW, cBot, -16316657);
      if (list.isEmpty()) {
         ctx.method_44379(pX, cTop, pX + pW, cBot);
         ctx.method_27534(this.field_22793, class_2561.method_43470("§7Brak znikniętych graczy"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
         ctx.method_44380();
      }

      int y = pY + 30 - this.rightScroll;

      for(Iterator var13 = list.iterator(); var13.hasNext(); y += 74) {
         DisappearedTracker.DisappearedEntry entry = (DisappearedTracker.DisappearedEntry)var13.next();
         if (y + 70 > cTop && y < cBot) {
            boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
            this.renderEntry(ctx, entry.data, pX + 4, y, pW - 8, entry.getTimeAgo(), hov, true, pX, pW, cTop, cBot);
         }
      }

      ctx.method_25294(pX, cBot, pX + pW, pY + this.panelH, -16119270);
      ctx.method_25294(pX, cBot - 1, pX + pW, cBot, -14013846);
   }

   private void renderWatchlistPanel(class_332 ctx, int mx, int my, int gX, int pY) {
      List<WatchlistTracker.WatchlistEntry> all = new ArrayList(WatchlistTracker.getAll());
      int pX = gX + 6;
      int pW = 848;
      int cTop = pY + 28;
      int cBot = pY + this.panelH;
      this.fillGradientH(ctx, pX, pY, pW, 28, -14015232, -12568576);
      ctx.method_25294(pX, pY + 27, pX + pW, pY + 28, -7838208);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§e§l★ WATCHLIST"), pX + 8, pY + 8, -8960);
      String cnt = "(" + all.size() + ")";
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7" + cnt), pX + pW - this.field_22793.method_1727(cnt) - 6, pY + 8, -10066313);
      ctx.method_25294(pX, cTop, pX + pW, cBot, -16316657);
      if (all.isEmpty()) {
         ctx.method_44379(pX, cTop, pX + pW, cBot);
         ctx.method_27534(this.field_22793, class_2561.method_43470("§7Watchlist pusta - kliknij §e★§7 przy graczu"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
         ctx.method_44380();
      }

      int y = pY + 30 - this.watchlistScroll;

      for(Iterator var13 = all.iterator(); var13.hasNext(); y += 74) {
         WatchlistTracker.WatchlistEntry entry = (WatchlistTracker.WatchlistEntry)var13.next();
         if (y + 70 > cTop && y < cBot) {
            boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + 70;
            this.renderWatchlistEntry(ctx, mx, my, entry, pX + 4, y, pW - 8, hov, cTop, cBot);
         }
      }

   }

   private void renderBlacklistPanel(class_332 ctx, int mx, int my, int gX, int pY) {
      List<String> all = new ArrayList(BlacklistTracker.getAll());
      int pX = gX + 6;
      int pW = 848;
      int entH = 32;
      int cTop = pY + 28;
      int cBot = pY + this.panelH;
      this.fillGradientH(ctx, pX, pY, pW, 28, -14021363, -12576744);
      ctx.method_25294(pX, pY + 27, pX + pW, pY + 28, -7851213);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§c§l⛔ BLACKLIST"), pX + 8, pY + 8, -48060);
      String cnt = "(" + all.size() + ")";
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7" + cnt), pX + pW - this.field_22793.method_1727(cnt) - 6, pY + 8, -10066313);
      ctx.method_25294(pX, cTop, pX + pW, cBot, -16316657);
      ctx.method_44379(pX, cTop, pX + pW, cBot);
      if (all.isEmpty()) {
         ctx.method_27534(this.field_22793, class_2561.method_43470("§7Blacklista jest pusta"), pX + pW / 2, cTop + (cBot - cTop) / 2 - 4, -10066313);
      }

      int y = pY + 30 - this.blacklistScroll;

      for(Iterator var14 = all.iterator(); var14.hasNext(); y += entH + 4) {
         String name = (String)var14.next();
         if (y + entH > cTop && y < cBot) {
            boolean hov = mx >= pX && mx <= pX + pW && my >= y && my <= y + entH;
            ctx.method_25294(pX + 4, y, pX + pW - 4, y + entH, hov ? -15658718 : -16119270);
            ctx.method_25294(pX + 4, y, pX + 6, y + entH, -3399134);
            ctx.method_25294(pX + 4, y, pX + pW - 4, y + 1, -14013846);
            ctx.method_27535(this.field_22793, class_2561.method_43470("§c⛔ §f" + name), pX + 12, y + entH / 2 - 4, -3355427);
            int bX = pX + pW - 100;
            int bY = y + 6;
            this.renderInlineBtn(ctx, "Odblokuj", GuiButton.Style.GREEN, bX, bY, 90, 20, this.hitTest(mx, my, bX, bY, 90, 20));
         }
      }

      ctx.method_44380();
   }

   private void renderEntry(class_332 ctx, PlayerData pd, int x, int y, int w, String timeAgo, boolean hover, boolean showButtons, int pX, int pW, int cTop, int cBot) {
      ctx.method_44379(x, cTop, x + w, cBot);
      ctx.method_25294(x, y, x + w, y + 70, hover ? -15658712 : -16119270);
      ctx.method_25294(x, y, x + 2, y + 70, ModSettings.accentColor);
      ctx.method_25294(x, y, x + w, y + 1, -14013846);
      this.renderHead(ctx, pd, x + 4, y + 3, 26);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§e§l" + pd.name), x + 38, y + 4, -22016);
      if (timeAgo != null) {
         ctx.method_27535(this.field_22793, class_2561.method_43470("§c⏱ " + timeAgo), x + 38, y + 15, -39356);
      }

      if (NetheriteChecker.hasElytra(pd)) {
         int ex = x + 38 + (timeAgo != null ? this.field_22793.method_1727("⏱ " + timeAgo) + 4 : 0);
         ctx.method_27535(this.field_22793, class_2561.method_43470("§b\ud83e\ude82"), ex, y + 15, -16724737);
      }

      float hp = pd.health / 2.0F;
      float maxHp = pd.maxHealth / 2.0F;
      float abs = pd.absorption / 2.0F;
      float ratio = maxHp > 0.0F ? hp / maxHp : 0.0F;
      int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
      String hpTxt = (int)hp + "/" + (int)maxHp + (abs > 0.0F ? " (+" + (int)abs + ")" : "");
      ctx.method_27535(this.field_22793, class_2561.method_43470("§c❤"), x + 38, y + 26, hpCol);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§f " + hpTxt), x + 38 + 10, y + 26, -3355427);
      int iY = y + 37;
      int iX = x + 38;
      this.renderItemSlot(ctx, pd.helmet, iX, iY);
      this.renderItemSlot(ctx, pd.chestplate, iX + 20, iY);
      this.renderItemSlot(ctx, pd.leggings, iX + 40, iY);
      this.renderItemSlot(ctx, pd.boots, iX + 60, iY);
      ctx.method_25294(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
      this.renderItemSlot(ctx, pd.mainHand, iX + 88, iY);
      this.renderItemSlot(ctx, pd.offHand, iX + 108, iY);
      ctx.method_44380();
      if (showButtons) {
         int btnY = y + 54;
         if (btnY + 18 > cTop && btnY < cBot) {
            int bX = pX + pW - 222;
            boolean watched = WatchlistTracker.isWatched(pd.name);
            this.renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, bX, btnY, 50, 18, this.hitTest(this.lastMX, this.lastMY, bX, btnY, 50, 18));
            this.renderInlineBtn(ctx, "BLOK", GuiButton.Style.RED, bX + 54, btnY, 50, 18, this.hitTest(this.lastMX, this.lastMY, bX + 54, btnY, 50, 18));
            this.renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, bX + 108, btnY, 50, 18, this.hitTest(this.lastMX, this.lastMY, bX + 108, btnY, 50, 18));
            this.renderInlineBtn(ctx, watched ? "★" : "☆", watched ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, bX + 162, btnY, 24, 18, this.hitTest(this.lastMX, this.lastMY, bX + 162, btnY, 24, 18));
         }
      }

   }

   private void renderWatchlistEntry(class_332 ctx, int mx, int my, WatchlistTracker.WatchlistEntry entry, int x, int y, int w, boolean hover, int cTop, int cBot) {
      ctx.method_44379(x, cTop, x + w, cBot);
      ctx.method_25294(x, y, x + w, y + 70, hover ? -15658712 : -16119270);
      ctx.method_25294(x, y, x + 2, y + 70, -22016);
      ctx.method_25294(x, y, x + w, y + 1, -10075136);
      this.renderHead(ctx, entry.data, x + 4, y + 3, 26);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§e§l" + entry.data.name), x + 38, y + 4, -8960);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7" + entry.getTimeAgo()), x + 38, y + 15, -10066313);
      float hp = entry.data.health / 2.0F;
      float maxHp = entry.data.maxHealth / 2.0F;
      float abs = entry.data.absorption / 2.0F;
      float ratio = maxHp > 0.0F ? hp / maxHp : 0.0F;
      int hpCol = ratio > 0.6F ? -16711868 : (ratio > 0.3F ? -13312 : -56798);
      String hpTxt = (int)hp + "/" + (int)maxHp + (abs > 0.0F ? " (+" + (int)abs + ")" : "");
      ctx.method_27535(this.field_22793, class_2561.method_43470("§c❤"), x + 38, y + 26, hpCol);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§f " + hpTxt), x + 38 + 10, y + 26, -3355427);
      int iY = y + 37;
      int iX = x + 38;
      this.renderItemSlot(ctx, entry.data.helmet, iX, iY);
      this.renderItemSlot(ctx, entry.data.chestplate, iX + 20, iY);
      this.renderItemSlot(ctx, entry.data.leggings, iX + 40, iY);
      this.renderItemSlot(ctx, entry.data.boots, iX + 60, iY);
      ctx.method_25294(iX + 84, iY - 2, iX + 85, iY + 18, -14013846);
      this.renderItemSlot(ctx, entry.data.mainHand, iX + 88, iY);
      this.renderItemSlot(ctx, entry.data.offHand, iX + 108, iY);
      ctx.method_44380();
      int btnY = y + 54;
      if (btnY + 18 > cTop && btnY < cBot) {
         int btnX = x + w - 186;
         this.renderInlineBtn(ctx, "TPA", GuiButton.Style.GREEN, btnX, btnY, 50, 18, this.hitTest(mx, my, btnX, btnY, 50, 18));
         this.renderInlineBtn(ctx, "STATS", GuiButton.Style.WHITE, btnX + 54, btnY, 50, 18, this.hitTest(mx, my, btnX + 54, btnY, 50, 18));
         this.renderInlineBtn(ctx, "★ USUŃ", GuiButton.Style.ORANGE, btnX + 108, btnY, 70, 18, this.hitTest(mx, my, btnX + 108, btnY, 70, 18));
      }

   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return super.method_25402(mouseX, mouseY, button);
      } else {
         int mx = (int)mouseX;
         int my = (int)mouseY;
         int gX = this.guiX;
         int gY = this.guiY;
         int pY = gY + 52;
         if (this.hitTest(mx, my, gX + 860 - 26, gY + 2, 22, 18)) {
            class_310.method_1551().method_1507(new SettingsScreen(this));
            return true;
         } else if (this.hitTest(mx, my, gX + 4, gY + 2, 90, 18)) {
            this.currentTab = GearReaderScreen.Tab.PLAYERS;
            return true;
         } else if (this.hitTest(mx, my, gX + 98, gY + 2, 110, 18)) {
            this.currentTab = GearReaderScreen.Tab.WATCHLIST;
            return true;
         } else if (this.hitTest(mx, my, gX + 212, gY + 2, 110, 18)) {
            this.currentTab = GearReaderScreen.Tab.BLACKLIST;
            return true;
         } else {
            int pX;
            int pX;
            int cBot;
            int cBot;
            if (this.currentTab == GearReaderScreen.Tab.PLAYERS) {
               pX = gX + 430 + 6;
               int cY = gY + 480 - 40;
               if (this.hitTest(mx, my, pX, cY, 90, 16)) {
                  DisappearedTracker.clearAll();
                  return true;
               }

               pX = gX + 430 + 6;
               int pW = 418;
               cBot = pY + 28;
               cBot = pY + this.panelH - 24;
               List<DisappearedTracker.DisappearedEntry> list = new ArrayList(DisappearedTracker.getDisappeared());
               int y = pY + 30 - this.rightScroll;

               for(Iterator var19 = list.iterator(); var19.hasNext(); y += 74) {
                  DisappearedTracker.DisappearedEntry entry = (DisappearedTracker.DisappearedEntry)var19.next();
                  int btnY = y + 54;
                  if (btnY + 18 > cBot && btnY < cBot) {
                     int bX = pX + pW - 222;
                     if (this.hitTest(mx, my, bX, btnY, 50, 18)) {
                        if (class_310.method_1551().field_1724 != null) {
                           class_310.method_1551().field_1724.field_3944.method_45730("tpa " + entry.data.name);
                        }

                        return true;
                     }

                     if (this.hitTest(mx, my, bX + 54, btnY, 50, 18)) {
                        BlacklistTracker.add(entry.data.name);
                        DisappearedTracker.remove(entry.data.name);
                        return true;
                     }

                     if (this.hitTest(mx, my, bX + 108, btnY, 50, 18)) {
                        if (class_310.method_1551().field_1724 != null) {
                           class_310.method_1551().field_1724.field_3944.method_45730("stats " + entry.data.name);
                           class_310.method_1551().method_1507((class_437)null);
                        }

                        return true;
                     }

                     if (this.hitTest(mx, my, bX + 162, btnY, 24, 18)) {
                        if (WatchlistTracker.isWatched(entry.data.name)) {
                           WatchlistTracker.remove(entry.data.name);
                        } else {
                           WatchlistTracker.add(entry.data);
                        }

                        return true;
                     }
                  }
               }
            } else {
               short pW;
               int cTop;
               if (this.currentTab == GearReaderScreen.Tab.WATCHLIST) {
                  pX = gX + 6;
                  pW = 848;
                  pX = pY + 28;
                  cTop = pY + this.panelH;
                  List<WatchlistTracker.WatchlistEntry> all = new ArrayList(WatchlistTracker.getAll());
                  cBot = pY + 30 - this.watchlistScroll;

                  for(Iterator var28 = all.iterator(); var28.hasNext(); cBot += 74) {
                     WatchlistTracker.WatchlistEntry entry = (WatchlistTracker.WatchlistEntry)var28.next();
                     int btnY = cBot + 54;
                     if (btnY + 18 > pX && btnY < cTop) {
                        int btnX = pX + 4 + (pW - 8) - 186;
                        if (this.hitTest(mx, my, btnX, btnY, 50, 18)) {
                           if (class_310.method_1551().field_1724 != null) {
                              class_310.method_1551().field_1724.field_3944.method_45730("tpa " + entry.data.name);
                           }

                           return true;
                        }

                        if (this.hitTest(mx, my, btnX + 54, btnY, 50, 18)) {
                           if (class_310.method_1551().field_1724 != null) {
                              class_310.method_1551().field_1724.field_3944.method_45730("stats " + entry.data.name);
                              class_310.method_1551().method_1507((class_437)null);
                           }

                           return true;
                        }

                        if (this.hitTest(mx, my, btnX + 108, btnY, 70, 18)) {
                           WatchlistTracker.remove(entry.data.name);
                           return true;
                        }
                     }
                  }
               } else if (this.currentTab == GearReaderScreen.Tab.BLACKLIST) {
                  pX = gX + 6;
                  pW = 848;
                  int entH = 32;
                  cTop = pY + 28;
                  cBot = pY + this.panelH;
                  List<String> all = new ArrayList(BlacklistTracker.getAll());
                  int y = pY + 30 - this.blacklistScroll;

                  for(Iterator var31 = all.iterator(); var31.hasNext(); y += entH + 4) {
                     String name = (String)var31.next();
                     if (y + entH > cTop && y < cBot && this.hitTest(mx, my, pX + pW - 100, y + 6, 90, 20)) {
                        BlacklistTracker.remove(name);
                        return true;
                     }
                  }
               }
            }

            return super.method_25402(mouseX, mouseY, button);
         }
      }
   }

   private void renderHead(class_332 ctx, PlayerData pd, int x, int y, int size) {
      if (ModSettings.performanceMode) {
         ctx.method_25294(x, y, x + size, y + size, -13417370);
         if (pd.name != null && !pd.name.isEmpty()) {
            ctx.method_27534(this.field_22793, class_2561.method_43470(String.valueOf(pd.name.charAt(0)).toUpperCase()), x + size / 2, y + size / 2 - 4, ModSettings.accentColor2);
         }

      } else {
         try {
            class_310 client = class_310.method_1551();
            class_2960 skin = null;
            if (client.method_1562() != null) {
               Iterator var8 = client.method_1562().method_2880().iterator();

               while(var8.hasNext()) {
                  class_640 entry = (class_640)var8.next();
                  if (entry.method_2966().getName().equals(pd.name)) {
                     skin = entry.method_2968();
                     break;
                  }
               }
            }

            if (skin == null) {
               skin = new class_2960("textures/entity/player/wide/steve.png");
            }

            ctx.method_25294(x - 2, y - 2, x + size + 2, y + size + 2, ModSettings.accentColor);
            ctx.method_25294(x - 1, y - 1, x + size + 1, y + size + 1, -16448240);
            ctx.method_25293(skin, x, y, size, size, 8.0F, 8.0F, 8, 8, 64, 64);
            ctx.method_25293(skin, x, y, size, size, 40.0F, 8.0F, 8, 8, 64, 64);
         } catch (Exception var10) {
            ctx.method_25294(x, y, x + size, y + size, -13417370);
            if (pd.name != null && !pd.name.isEmpty()) {
               ctx.method_27534(this.field_22793, class_2561.method_43470(String.valueOf(pd.name.charAt(0)).toUpperCase()), x + size / 2, y + size / 2 - 4, -3355393);
            }
         }

      }
   }

   private void renderItemSlot(class_332 ctx, class_1799 stack, int x, int y) {
      if (stack != null && !stack.method_7960()) {
         if (!ModSettings.performanceMode) {
            if (NetheriteChecker.isNetherite(stack)) {
               ctx.method_25294(x - 2, y - 2, x + 18, y + 18, 872393216);
               ctx.method_25294(x - 1, y - 1, x + 17, y + 17, 1442818560);
            }

            if (NetheriteChecker.isElytra(stack)) {
               ctx.method_25294(x - 2, y - 2, x + 18, y + 18, 855690495);
               ctx.method_25294(x - 1, y - 1, x + 17, y + 17, 1426115839);
            }
         }

         ctx.method_51427(stack, x, y);
      } else {
         ctx.method_25294(x, y, x + 16, y + 16, -15921894);
         if (!ModSettings.performanceMode) {
            ctx.method_25294(x, y, x + 16, y + 1, -14013846);
            ctx.method_25294(x, y, x + 1, y + 16, -14013846);
         }

      }
   }

   private void renderInlineBtn(class_332 ctx, String label, GuiButton.Style style, int x, int y, int w, int h, boolean hovered) {
      int bg = hovered ? this.blendColor(style.bg, style.border, 0.35F) : style.bg;
      ctx.method_25294(x + 1, y + 1, x + w - 1, y + h - 1, bg);
      ctx.method_25294(x, y, x + w, y + 1, style.border);
      ctx.method_25294(x, y + h - 1, x + w, y + h, style.border);
      ctx.method_25294(x, y, x + 1, y + h, style.border);
      ctx.method_25294(x + w - 1, y, x + w, y + h, style.border);
      if (hovered) {
         ctx.method_25294(x + 1, y + 1, x + w - 1, y + 2, this.addAlpha(style.border, 102));
      }

      int tw = this.field_22793.method_1727(label);
      int tx = x + (w - tw) / 2;
      int ty = y + (h - 8) / 2;
      ctx.method_51433(this.field_22793, label, tx + 1, ty + 1, 1426063360, false);
      ctx.method_51433(this.field_22793, label, tx, ty, hovered ? -1 : style.text, false);
   }

   private void fillGradientH(class_332 ctx, int x, int y, int w, int h, int cL, int cR) {
      if (ModSettings.performanceMode) {
         ctx.method_25294(x, y, x + w, y + h, cL);
      } else {
         for(int i = 0; i < w; ++i) {
            float t = (float)i / (float)w;
            int r = this.lerp(cL >> 16 & 255, cR >> 16 & 255, t);
            int g = this.lerp(cL >> 8 & 255, cR >> 8 & 255, t);
            int b = this.lerp(cL & 255, cR & 255, t);
            int a = this.lerp(cL >> 24 & 255, cR >> 24 & 255, t);
            ctx.method_25294(x + i, y, x + i + 1, y + h, a << 24 | r << 16 | g << 8 | b);
         }

      }
   }

   private void drawBorderGlow(class_332 ctx, int x, int y, int w, int h) {
      ctx.method_25294(x, y, x + w, y + 1, 1152035583);
      ctx.method_25294(x, y + h, x + w, y + h + 1, 1152035583);
      ctx.method_25294(x, y, x + 1, y + h, 1152035583);
      ctx.method_25294(x + w, y, x + w + 1, y + h, 1152035583);
      ctx.method_25294(x + 1, y + 1, x + w - 1, y + 2, ModSettings.accentColor);
      ctx.method_25294(x + 1, y + h - 2, x + w - 1, y + h - 1, ModSettings.accentColor);
      ctx.method_25294(x + 1, y + 1, x + 2, y + h - 1, ModSettings.accentColor);
      ctx.method_25294(x + w - 2, y + 1, x + w - 1, y + h - 1, ModSettings.accentColor);
   }

   private int lerp(int a, int b, float t) {
      return (int)((float)a + (float)(b - a) * t);
   }

   private int blendColor(int c1, int c2, float t) {
      return -16777216 | this.lerp(c1 >> 16 & 255, c2 >> 16 & 255, t) << 16 | this.lerp(c1 >> 8 & 255, c2 >> 8 & 255, t) << 8 | this.lerp(c1 & 255, c2 & 255, t);
   }

   private int addAlpha(int color, int alpha) {
      return alpha << 24 | color & 16777215;
   }

   private boolean hitTest(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   public boolean method_25401(double mouseX, double mouseY, double amount) {
      if (this.currentTab == GearReaderScreen.Tab.BLACKLIST) {
         this.blacklistScroll = Math.max(0, this.blacklistScroll - (int)(amount * 15.0D));
      } else if (this.currentTab == GearReaderScreen.Tab.WATCHLIST) {
         this.watchlistScroll = Math.max(0, this.watchlistScroll - (int)(amount * 15.0D));
      } else if (mouseX < (double)(this.guiX + 430)) {
         this.leftScroll = Math.max(0, this.leftScroll - (int)(amount * 18.0D));
      } else {
         this.rightScroll = Math.max(0, this.rightScroll - (int)(amount * 18.0D));
      }

      return true;
   }

   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.method_25419();
         return true;
      } else {
         return super.method_25404(keyCode, scanCode, modifiers);
      }
   }

   public boolean method_25421() {
      return false;
   }

   private static enum Tab {
      PLAYERS,
      WATCHLIST,
      BLACKLIST;

      // $FF: synthetic method
      private static GearReaderScreen.Tab[] $values() {
         return new GearReaderScreen.Tab[]{PLAYERS, WATCHLIST, BLACKLIST};
      }
   }
}

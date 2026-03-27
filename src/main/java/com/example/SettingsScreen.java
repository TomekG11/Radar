package com.example;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;

public class SettingsScreen extends class_437 {
   private static final int GUI_W = 540;
   private static final int GUI_H = 560;
   private final class_437 parent;
   private long openTime;
   private int guiX;
   private int guiY;
   private int lastMX;
   private int lastMY;

   public SettingsScreen(class_437 parent) {
      super(class_2561.method_43470("Ustawienia"));
      this.parent = parent;
      this.openTime = System.currentTimeMillis();
   }

   protected void method_25426() {
      super.method_25426();
      this.guiX = (this.field_22789 - 540) / 2;
      this.guiY = (this.field_22790 - 560) / 2;
   }

   public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
      this.lastMX = mouseX;
      this.lastMY = mouseY;
      float ease = class_3532.method_15363((float)(System.currentTimeMillis() - this.openTime) / 250.0F, 0.0F, 1.0F);
      ease = 1.0F - (1.0F - ease) * (1.0F - ease);
      int gX = this.guiX;
      int gY = this.guiY + (int)((1.0F - ease) * 20.0F);
      this.method_25420(ctx);
      ctx.method_25294(gX - 1, gY - 1, gX + 540 + 1, gY + 560 + 1, ModSettings.accentColor);
      ctx.method_25294(gX, gY, gX + 540, gY + 560, -267909104);
      ctx.method_25294(gX, gY, gX + 540, gY + 48, -15921878);
      ctx.method_25294(gX, gY + 46, gX + 540, gY + 47, ModSettings.accentColor);
      ctx.method_27534(this.field_22793, class_2561.method_43470("§b§l⚙ USTAWIENIA"), gX + 270, gY + 17, ModSettings.accentColor2);
      ctx.method_27535(this.field_22793, class_2561.method_43470("§8UkrainskiReader v1.0"), gX + 8, gY + 33, -12303258);
      int y = gY + 54;
      y = this.renderSection(ctx, gX, y, "§f▌ §b§lKOLOR INTERFEJSU");
      ctx.method_25294(gX + 540 - 54, y - 14, gX + 540 - 12, y - 1, ModSettings.accentColor);
      ModSettings.ColorPreset[] presets = ModSettings.ColorPreset.values();
      int btnW = 120;
      int btnH = 20;
      int gap = 6;

      int rowCount;
      for(rowCount = 0; rowCount < presets.length; ++rowCount) {
         ModSettings.ColorPreset preset = presets[rowCount];
         boolean active = ModSettings.accentColor == preset.accent;
         int col = rowCount % 4;
         int row = rowCount / 4;
         int bX = gX + 20 + col * (btnW + gap);
         int bY = y + row * (btnH + gap);
         String label = (active ? "§l✔ " : "") + preset.label;
         GuiButton.Style style = active ? GuiButton.Style.BLUE : GuiButton.Style.WHITE;
         this.renderBtn(ctx, label, style, bX, bY, btnW, btnH, this.hitTest(mouseX, mouseY, bX, bY, btnW, btnH));
      }

      rowCount = (int)Math.ceil((double)presets.length / 4.0D);
      y += rowCount * (btnH + gap) + 12;
      y = this.renderSection(ctx, gX, y, "§f▌ §b§lTRYB FILTROWANIA");
      String descSet = ModSettings.fullSetOnly ? "§aPełny set: helm + napierśnik + spodnie + buty + broń" : "§7Dowolny item: chociaż 1 przedmiot netherytowy";
      ctx.method_27535(this.field_22793, class_2561.method_43470(descSet), gX + 14, y, -7829351);
      y += 12;
      String descElytra = ModSettings.showElytra ? "§aWykrywaj graczy z elytrem: WŁ" : "§7Wykrywaj graczy z elytrem: WYŁ";
      ctx.method_27535(this.field_22793, class_2561.method_43470(descElytra), gX + 14, y, -7829351);
      y += 14;
      int fBtnW = 245;
      this.renderBtn(ctx, ModSettings.fullSetOnly ? "§l✔ Pełny set: WŁ" : "Pełny set: WYŁ", ModSettings.fullSetOnly ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20, y, fBtnW, btnH, this.hitTest(mouseX, mouseY, gX + 20, y, fBtnW, btnH));
      this.renderBtn(ctx, ModSettings.showElytra ? "§l✔ Elytra: WŁ" : "Elytra: WYŁ", ModSettings.showElytra ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20 + fBtnW + 10, y, fBtnW, btnH, this.hitTest(mouseX, mouseY, gX + 20 + fBtnW + 10, y, fBtnW, btnH));
      y += btnH + 14;
      y = this.renderSection(ctx, gX, y, "§f▌ §e§lOPCJE GUI");
      String perfDesc = ModSettings.performanceMode ? "§aAktywny: brak gradientów, animacji i skinów - max FPS" : "§7Wyłączony: pełne efekty wizualne";
      ctx.method_27535(this.field_22793, class_2561.method_43470(perfDesc), gX + 14, y, -7829351);
      y += 12;
      String tpaDesc = ModSettings.closeOnTpa ? "§aZamknij GUI po akceptacji TPA: WŁ" : "§7Zamknij GUI po akceptacji TPA: WYŁ";
      ctx.method_27535(this.field_22793, class_2561.method_43470(tpaDesc), gX + 14, y, -7829351);
      y += 14;
      int gBtnW = 245;
      this.renderBtn(ctx, ModSettings.performanceMode ? "§l✔ Performance: WŁ" : "Performance: WYŁ", ModSettings.performanceMode ? GuiButton.Style.YELLOW : GuiButton.Style.WHITE, gX + 20, y, gBtnW, btnH, this.hitTest(mouseX, mouseY, gX + 20, y, gBtnW, btnH));
      this.renderBtn(ctx, ModSettings.closeOnTpa ? "§l✔ Zamknij po TPA: WŁ" : "Zamknij po TPA: WYŁ", ModSettings.closeOnTpa ? GuiButton.Style.GREEN : GuiButton.Style.WHITE, gX + 20 + gBtnW + 10, y, gBtnW, btnH, this.hitTest(mouseX, mouseY, gX + 20 + gBtnW + 10, y, gBtnW, btnH));
      y += btnH + 14;
      y = this.renderSection(ctx, gX, y, "§f▌ §c§lBLACKLIST");
      ctx.method_27535(this.field_22793, class_2561.method_43470("§7Zablokowanych: §f" + BlacklistTracker.getAll().size()), gX + 14, y, -7829351);
      y += 14;
      int blBtnW = 220;
      int blBtnX = gX + 270 - blBtnW / 2;
      this.renderBtn(ctx, "⛔ Zarządzaj Blacklistą", GuiButton.Style.RED, blBtnX, y, blBtnW, btnH, this.hitTest(mouseX, mouseY, blBtnX, y, blBtnW, btnH));
      int var10000 = y + btnH + 14;
      ctx.method_25294(gX + 10, gY + 560 - 50, gX + 540 - 10, gY + 560 - 49, -14540220);
      int backW = 120;
      int backX = gX + 270 - backW / 2;
      int backY = gY + 560 - 40;
      this.renderBtn(ctx, "← Powrót", GuiButton.Style.WHITE, backX, backY, backW, 22, this.hitTest(mouseX, mouseY, backX, backY, backW, 22));
      super.method_25394(ctx, mouseX, mouseY, delta);
   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return super.method_25402(mouseX, mouseY, button);
      } else {
         int mx = (int)mouseX;
         int my = (int)mouseY;
         float ease = class_3532.method_15363((float)(System.currentTimeMillis() - this.openTime) / 250.0F, 0.0F, 1.0F);
         ease = 1.0F - (1.0F - ease) * (1.0F - ease);
         int gX = this.guiX;
         int gY = this.guiY + (int)((1.0F - ease) * 20.0F);
         int y = gY + 54;
         y = this.sectionSkip(y);
         ModSettings.ColorPreset[] presets = ModSettings.ColorPreset.values();
         int btnW = 120;
         int btnH = 20;
         int gap = 6;

         int rowCount;
         int blBtnX;
         for(rowCount = 0; rowCount < presets.length; ++rowCount) {
            ModSettings.ColorPreset preset = presets[rowCount];
            int col = rowCount % 4;
            int row = rowCount / 4;
            blBtnX = gX + 20 + col * (btnW + gap);
            int bY = y + row * (btnH + gap);
            if (this.hitTest(mx, my, blBtnX, bY, btnW, btnH)) {
               ModSettings.setColor(preset);
               return true;
            }
         }

         rowCount = (int)Math.ceil((double)presets.length / 4.0D);
         y += rowCount * (btnH + gap) + 12;
         y = this.sectionSkip(y);
         y += 26;
         int fBtnW = 245;
         if (this.hitTest(mx, my, gX + 20, y, fBtnW, btnH)) {
            ModSettings.fullSetOnly = !ModSettings.fullSetOnly;
            return true;
         } else if (this.hitTest(mx, my, gX + 20 + fBtnW + 10, y, fBtnW, btnH)) {
            ModSettings.showElytra = !ModSettings.showElytra;
            return true;
         } else {
            y += btnH + 14;
            y = this.sectionSkip(y);
            y += 26;
            int gBtnW = 245;
            if (this.hitTest(mx, my, gX + 20, y, gBtnW, btnH)) {
               ModSettings.performanceMode = !ModSettings.performanceMode;
               return true;
            } else if (this.hitTest(mx, my, gX + 20 + gBtnW + 10, y, gBtnW, btnH)) {
               ModSettings.closeOnTpa = !ModSettings.closeOnTpa;
               return true;
            } else {
               y += btnH + 14;
               y = this.sectionSkip(y);
               y += 14;
               int blBtnW = 220;
               blBtnX = gX + 270 - blBtnW / 2;
               if (this.hitTest(mx, my, blBtnX, y, blBtnW, btnH)) {
                  class_310.method_1551().method_1507(new BlacklistScreen(this));
                  return true;
               } else {
                  int var10000 = y + btnH + 14;
                  int backW = 120;
                  int backX = gX + 270 - backW / 2;
                  int backY = gY + 560 - 40;
                  if (this.hitTest(mx, my, backX, backY, backW, 22)) {
                     class_310.method_1551().method_1507(this.parent);
                     return true;
                  } else {
                     return super.method_25402(mouseX, mouseY, button);
                  }
               }
            }
         }
      }
   }

   private int renderSection(class_332 ctx, int gX, int y, String label) {
      ctx.method_25294(gX + 10, y, gX + 540 - 10, y + 1, ModSettings.accentColor);
      ctx.method_27535(this.field_22793, class_2561.method_43470(label), gX + 14, y + 4, ModSettings.accentColor2);
      return y + 18;
   }

   private int sectionSkip(int y) {
      return y + 18;
   }

   private void renderBtn(class_332 ctx, String label, GuiButton.Style style, int x, int y, int w, int h, boolean hovered) {
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

   private boolean hitTest(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   private int blendColor(int c1, int c2, float t) {
      int r = (int)((float)(c1 >> 16 & 255) + (float)((c2 >> 16 & 255) - (c1 >> 16 & 255)) * t);
      int g = (int)((float)(c1 >> 8 & 255) + (float)((c2 >> 8 & 255) - (c1 >> 8 & 255)) * t);
      int b = (int)((float)(c1 & 255) + (float)((c2 & 255) - (c1 & 255)) * t);
      return -16777216 | r << 16 | g << 8 | b;
   }

   private int addAlpha(int color, int alpha) {
      return alpha << 24 | color & 16777215;
   }

   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         class_310.method_1551().method_1507(this.parent);
         return true;
      } else {
         return super.method_25404(keyCode, scanCode, modifiers);
      }
   }

   public boolean method_25421() {
      return false;
   }
}

package com.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_3532;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class BlacklistScreen extends class_437 {
   private static final int GUI_W = 500;
   private static final int GUI_H = 420;
   private final class_437 parent;
   private long openTime;
   private int guiX;
   private int guiY;
   private class_342 searchField;
   private String searchQuery = "";
   private int scroll = 0;
   private static final int ENTRY_H = 28;
   private static final int ENTRY_GAP = 3;
   private static final int LIST_TOP_OFFSET = 90;
   private final List<class_4185> unlockButtons = new ArrayList();

   public BlacklistScreen(class_437 parent) {
      super(class_2561.method_43470("Blacklist"));
      this.parent = parent;
      this.openTime = System.currentTimeMillis();
   }

   protected void method_25426() {
      super.method_25426();
      this.guiX = (this.field_22789 - 500) / 2;
      this.guiY = (this.field_22790 - 420) / 2;
      this.searchField = new class_342(this.field_22793, this.guiX + 14, this.guiY + 55, 472, 22, class_2561.method_43470("Szukaj..."));
      this.searchField.method_1880(64);
      this.searchField.method_1863((s) -> {
         this.searchQuery = s;
         this.scroll = 0;
         this.rebuildButtons();
      });
      this.method_37063(this.searchField);
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§f← Powrót"), (b) -> {
         class_310.method_1551().method_1507(this.parent);
      }).method_46434(this.guiX + 250 - 60, this.guiY + 420 - 35, 120, 22).method_46431());
      this.rebuildButtons();
   }

   private void rebuildButtons() {
      Iterator var1 = this.unlockButtons.iterator();

      while(var1.hasNext()) {
         class_4185 btn = (class_4185)var1.next();
         this.method_37066(btn);
      }

      this.unlockButtons.clear();
      List<String> filtered = this.getFiltered();
      int listTop = this.guiY + 90;
      int listH = 280;
      int y = listTop + 4 - this.scroll;

      for(Iterator var5 = filtered.iterator(); var5.hasNext(); y += 31) {
         String name = (String)var5.next();
         if (y + 28 > listTop && y < listTop + listH) {
            class_4185 btn = class_4185.method_46430(class_2561.method_43470("§aOdblokuj"), (b) -> {
               BlacklistTracker.remove(name);
               this.scroll = Math.max(0, this.scroll - 31);
               this.rebuildButtons();
            }).method_46434(this.guiX + 500 - 110, y + 4, 90, 20).method_46431();
            this.unlockButtons.add(btn);
            this.method_37063(btn);
         }
      }

   }

   private List<String> getFiltered() {
      List<String> result = new ArrayList();
      String q = this.searchQuery.toLowerCase().trim();
      Iterator var3 = BlacklistTracker.getAll().iterator();

      while(true) {
         String name;
         do {
            if (!var3.hasNext()) {
               return result;
            }

            name = (String)var3.next();
         } while(!q.isEmpty() && !name.toLowerCase().contains(q));

         result.add(name);
      }
   }

   public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
      float ease = class_3532.method_15363((float)(System.currentTimeMillis() - this.openTime) / 250.0F, 0.0F, 1.0F);
      ease = 1.0F - (1.0F - ease) * (1.0F - ease);
      int offY = (int)((1.0F - ease) * 20.0F);
      int gX = this.guiX;
      int gY = this.guiY + offY;
      this.method_25420(ctx);
      ctx.method_25294(gX - 1, gY - 1, gX + 500 + 1, gY + 420 + 1, ModSettings.accentColor);
      ctx.method_25294(gX, gY, gX + 500, gY + 420, -267909104);
      ctx.method_25294(gX, gY, gX + 500, gY + 48, -15921878);
      ctx.method_25294(gX, gY + 46, gX + 500, gY + 47, ModSettings.accentColor);
      ctx.method_27534(this.field_22793, class_2561.method_43470("§c§l⛔ BLACKLIST"), gX + 250, gY + 17, -48060);
      List<String> filtered = this.getFiltered();
      class_327 var10001 = this.field_22793;
      int var10002 = BlacklistTracker.getAll().size();
      ctx.method_27535(var10001, class_2561.method_43470("§7Zablokowanych: §f" + var10002 + (this.searchQuery.isEmpty() ? "" : "  §8(filtr: " + filtered.size() + ")")), gX + 14, gY + 33, -7829351);
      this.searchField.method_46419(gY + 55);
      int listTop = gY + 90;
      int listH = 280;
      ctx.method_25294(gX + 10, listTop, gX + 500 - 10, listTop + listH, -16316657);
      ctx.method_25294(gX + 10, listTop, gX + 500 - 10, listTop + 1, -14013846);
      ctx.method_44379(gX + 10, listTop, gX + 500 - 10, listTop + listH);
      if (filtered.isEmpty()) {
         ctx.method_27534(this.field_22793, class_2561.method_43470(this.searchQuery.isEmpty() ? "§7Brak zablokowanych graczy" : "§7Brak wyników dla: §f" + this.searchQuery), gX + 250, listTop + listH / 2 - 4, -10066313);
      }

      int y = listTop + 4 - this.scroll;

      for(Iterator var13 = filtered.iterator(); var13.hasNext(); y += 31) {
         String name = (String)var13.next();
         if (y + 28 > listTop && y < listTop + listH) {
            ctx.method_25294(gX + 12, y, gX + 500 - 12, y + 28, -16119270);
            ctx.method_25294(gX + 12, y, gX + 14, y + 28, -3399134);
            ctx.method_27535(this.field_22793, class_2561.method_43470("§c⛔ §f" + name), gX + 20, y + 14 - 4, -3355427);
         }
      }

      ctx.method_44380();
      ctx.method_25294(gX + 10, gY + 420 - 45, gX + 500 - 10, gY + 420 - 44, -14540220);
      super.method_25394(ctx, mouseX, mouseY, delta);
      this.rebuildButtons();
   }

   public boolean method_25401(double mouseX, double mouseY, double amount) {
      this.scroll = Math.max(0, this.scroll - (int)(amount * 15.0D));
      this.rebuildButtons();
      return true;
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

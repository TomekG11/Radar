package com.example;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4185;

public class GuiButton extends class_4185 {
    private final Style style;
    private final String label;

    public GuiButton(int x, int y, int w, int h, String label, Style style, class_4241 action) {
        super(x, y, w, h, class_2561.method_43470(label), action, field_40754);
        this.label = label;
        this.style = style;
    }

    @Override
    public void method_48579(class_332 ctx, int mouseX, int mouseY, float delta) {
        boolean hov = this.method_49606();
        int bg = hov ? blendColor(style.bg, style.border, 0.35F) : style.bg;
        
        ctx.method_25294(method_46426() + 1, method_46427() + 1, 
                         method_46426() + method_25368() - 1, 
                         method_46427() + method_25364() - 1, bg);
        
        ctx.method_25294(method_46426(), method_46427(), 
                         method_46426() + method_25368(), method_46427() + 1, style.border);
        ctx.method_25294(method_46426(), method_46427() + method_25364() - 1, 
                         method_46426() + method_25368(), method_46427() + method_25364(), style.border);
        ctx.method_25294(method_46426(), method_46427(), 
                         method_46426() + 1, method_46427() + method_25364(), style.border);
        ctx.method_25294(method_46426() + method_25368() - 1, method_46427(), 
                         method_46426() + method_25368(), method_46427() + method_25364(), style.border);
        
        if (hov) {
            ctx.method_25294(method_46426() + 1, method_46427() + 1, 
                             method_46426() + method_25368() - 1, method_46427() + 2, 
                             addAlpha(style.border, 102));
            ctx.method_25294(method_46426() + 1, method_46427() + method_25364() - 2, 
                             method_46426() + method_25368() - 1, method_46427() + method_25364() - 1, 
                             addAlpha(style.border, 102));
        }

        class_327 tr = class_310.method_1551().field_1772;
        int tw = tr.method_27525(class_2561.method_43470(label));
        int tx = method_46426() + (method_25368() - tw) / 2;
        int ty = method_46427() + (method_25364() - 8) / 2;
        
        ctx.method_51439(tr, class_2561.method_43470(label), tx + 1, ty + 1, 0x55000000, false);
        ctx.method_51439(tr, class_2561.method_43470(label), tx, ty, hov ? 0xFFFFFFFF : style.text, false);
    }

    private int blendColor(int c1, int c2, float t) {
        int r = lerp((c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF, t);
        int g = lerp((c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF, t);
        int b = lerp(c1 & 0xFF, c2 & 0xFF, t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private int addAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0xFFFFFF);
    }

    private int lerp(int a, int b, float t) {
        return (int)(a + (b - a) * t);
    }

    public enum Style {
        GREEN(0xFF00FF11, 0xFF00BB00, 0xFF003300),
        RED(0xFFAA0000, 0xFFFF4444, 0xFF1A1A1A),
        WHITE(0xFF555555, 0xFFCCCCCC, 0xFF222222),
        YELLOW(0xFFAAA800, 0xFFFFDD00, 0xFF1A1A00),
        ORANGE(0xFFAA5500, 0xFFFFAA00, 0xFF1A0D00),
        BLUE(0xFF0055AA, 0xFF3399FF, 0xFF001A2A);

        public final int border;
        public final int text;
        public final int bg;

        Style(int border, int text, int bg) {
            this.border = border;
            this.text = text;
            this.bg = bg;
        }
    }
}

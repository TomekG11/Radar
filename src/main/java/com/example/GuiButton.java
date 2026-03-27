package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GuiButton extends ButtonWidget {
    private final Style style;
    private final String label;

    public GuiButton(int x, int y, int w, int h, String label, Style style, PressAction action) {
        super(x, y, w, h, Text.literal(label), action, DEFAULT_NARRATION_SUPPLIER);
        this.label = label;
        this.style = style;
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean hov = this.isHovered();
        int bg = hov ? blendColor(style.bg, style.border, 0.35F) : style.bg;
        
        ctx.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, bg);
        ctx.fill(getX(), getY(), getX() + getWidth(), getY() + 1, style.border);
        ctx.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), style.border);
        ctx.fill(getX(), getY(), getX() + 1, getY() + getHeight(), style.border);
        ctx.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), style.border);
        
        if (hov) {
            ctx.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + 2, addAlpha(style.border, 102));
            ctx.fill(getX() + 1, getY() + getHeight() - 2, getX() + getWidth() - 1, getY() + getHeight() - 1, addAlpha(style.border, 102));
        }

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int tw = tr.getWidth(Text.literal(label));
        int tx = getX() + (getWidth() - tw) / 2;
        int ty = getY() + (getHeight() - 8) / 2;
        ctx.drawTextWithShadow(tr, Text.literal(label), tx + 1, ty + 1, 1426063360);
        ctx.drawTextWithShadow(tr, Text.literal(label), tx, ty, hov ? -1 : style.text);
    }

    private int blendColor(int c1, int c2, float t) {
        int r = lerp(c1 >> 16 & 255, c2 >> 16 & 255, t);
        int g = lerp(c1 >> 8 & 255, c2 >> 8 & 255, t);
        int b = lerp(c1 & 255, c2 & 255, t);
        return -16777216 | r << 16 | g << 8 | b;
    }

    private int addAlpha(int color, int alpha) {
        return alpha << 24 | (color & 16777215);
    }

    private int lerp(int a, int b, float t) {
        return (int) ((float) a + (float) (b - a) * t);
    }

    public enum Style {
        GREEN(-16733645, -16711851, -16770550),
        RED(-5636096, -48060, -15073280),
        WHITE(-11184794, -3355427, -15658718),
        YELLOW(-5601280, -8960, -15068160),
        ORANGE(-5614336, -22016, -15070720),
        BLUE(-16759638, -12281345, -16773350);

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

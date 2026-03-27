package com.example;

public class ModSettings {
    public static int accentColor = 0xFF4A9EFF;
    public static int accentColor2 = 0xFF00BFFF;
    public static boolean fullSetOnly = false;
    public static boolean showElytra = true;
    public static boolean performanceMode = false;
    public static boolean closeOnTpa = true;

    public static void setColor(ColorPreset preset) {
        accentColor = preset.accent;
        accentColor2 = preset.accent2;
    }

    public enum ColorPreset {
        BLUE("Niebieski", 0xFF4A9EFF, 0xFF00BFFF),
        GREEN("Zielony", 0xFF00FF44, 0xFF00FF88),
        RED("Czerwony", 0xFFCC3333, 0xFFFF4444),
        PURPLE("Fioletowy", 0xFF9933FF, 0xFFCC66FF),
        ORANGE("Pomarańcz", 0xFFFF8800, 0xFFFFCC00),
        CYAN("Cyjan", 0xFF00FFCC, 0xFF00FFFF),
        PINK("Różowy", 0xFFFF33AA, 0xFFFF99CC);

        public final String label;
        public final int accent;
        public final int accent2;

        ColorPreset(String label, int accent, int accent2) {
            this.label = label;
            this.accent = accent;
            this.accent2 = accent2;
        }
    }
}

package com.example;

public class ModSettings {
    public static int accentColor = -11184641;
    public static int accentColor2 = -16724737;
    public static boolean fullSetOnly = false;
    public static boolean showElytra = true;
    public static boolean performanceMode = false;
    public static boolean closeOnTpa = true;

    public static void setColor(ColorPreset preset) {
        accentColor = preset.accent;
        accentColor2 = preset.accent2;
    }

    public enum ColorPreset {
        BLUE("Niebieski", -11184641, -16724737),
        GREEN("Zielony", -16724924, -16711800),
        RED("Czerwony", -3399134, -48060),
        PURPLE("Fioletowy", -6736897, -3381505),
        ORANGE("Pomarańcz", -30720, -13312),
        CYAN("Cyjan", -16724788, -16711681),
        PINK("Różowy", -47958, -26164);

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

package com.example;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class NetheriteChecker {
    
    public static boolean hasNetheriteItem(PlayerData data) {
        if (ModSettings.fullSetOnly) {
            return isNetheriteArmor(data.helmet) &&
                   isNetheriteArmor(data.chestplate) &&
                   isNetheriteArmor(data.leggings) &&
                   isNetheriteArmor(data.boots) &&
                   isNetheriteWeapon(data.mainHand);
        } else {
            return isNetherite(data.helmet) ||
                   isNetherite(data.chestplate) ||
                   isNetherite(data.leggings) ||
                   isNetherite(data.boots) ||
                   isNetherite(data.mainHand) ||
                   isNetherite(data.offHand) ||
                   isElytra(data.chestplate) ||
                   isElytra(data.offHand);
        }
    }

    public static boolean isNetheriteArmor(class_1799 stack) {
        if (stack != null && !stack.method_7960()) {
            class_1792 item = stack.method_7909();
            return item == class_1802.field_22027 ||
                   item == class_1802.field_22028 ||
                   item == class_1802.field_22029 ||
                   item == class_1802.field_22030;
        }
        return false;
    }

    public static boolean isNetheriteWeapon(class_1799 stack) {
        if (stack != null && !stack.method_7960()) {
            class_1792 item = stack.method_7909();
            return item == class_1802.field_22022 ||
                   item == class_1802.field_22024 ||
                   item == class_1802.field_22025 ||
                   item == class_1802.field_22023 ||
                   item == class_1802.field_22026;
        }
        return false;
    }

    public static boolean isNetherite(class_1799 stack) {
        if (stack != null && !stack.method_7960()) {
            class_1792 item = stack.method_7909();
            return item == class_1802.field_22022 ||
                   item == class_1802.field_22024 ||
                   item == class_1802.field_22025 ||
                   item == class_1802.field_22023 ||
                   item == class_1802.field_22026 ||
                   item == class_1802.field_22027 ||
                   item == class_1802.field_22028 ||
                   item == class_1802.field_22029 ||
                   item == class_1802.field_22030;
        }
        return false;
    }

    public static boolean isElytra(class_1799 stack) {
        if (stack != null && !stack.method_7960()) {
            return stack.method_7909() == class_1802.field_8833;
        }
        return false;
    }

    public static boolean hasElytra(PlayerData data) {
        return isElytra(data.chestplate) || isElytra(data.offHand);
    }
}

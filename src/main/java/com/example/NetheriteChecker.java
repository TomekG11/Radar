package com.example;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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

    public static boolean isNetheriteArmor(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            return item == Items.NETHERITE_HELMET ||
                   item == Items.NETHERITE_CHESTPLATE ||
                   item == Items.NETHERITE_LEGGINGS ||
                   item == Items.NETHERITE_BOOTS;
        }
        return false;
    }

    public static boolean isNetheriteWeapon(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            return item == Items.NETHERITE_SWORD ||
                   item == Items.NETHERITE_AXE ||
                   item == Items.NETHERITE_PICKAXE ||
                   item == Items.NETHERITE_SHOVEL ||
                   item == Items.NETHERITE_HOE;
        }
        return false;
    }

    public static boolean isNetherite(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            return item == Items.NETHERITE_SWORD ||
                   item == Items.NETHERITE_AXE ||
                   item == Items.NETHERITE_PICKAXE ||
                   item == Items.NETHERITE_SHOVEL ||
                   item == Items.NETHERITE_HOE ||
                   item == Items.NETHERITE_HELMET ||
                   item == Items.NETHERITE_CHESTPLATE ||
                   item == Items.NETHERITE_LEGGINGS ||
                   item == Items.NETHERITE_BOOTS;
        }
        return false;
    }

    public static boolean isElytra(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            return stack.getItem() == Items.ELYTRA;
        }
        return false;
    }

    public static boolean hasElytra(PlayerData data) {
        return isElytra(data.chestplate) || isElytra(data.offHand);
    }
}

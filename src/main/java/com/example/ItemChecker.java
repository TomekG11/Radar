package com.example;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemChecker {
    
    public static boolean hasRequiredItems(PlayerData data) {
        return hasLeatherArmor(data) ||
               hasNetheriteItems(data) ||
               hasElytra(data);
    }
    
    // Leather armor
    public static boolean hasLeatherArmor(PlayerData data) {
        return isLeatherArmor(data.helmet) ||
               isLeatherArmor(data.chestplate) ||
               isLeatherArmor(data.leggings) ||
               isLeatherArmor(data.boots);
    }
    
    public static boolean isLeatherArmor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == Items.LEATHER_HELMET ||
               item == Items.LEATHER_CHESTPLATE ||
               item == Items.LEATHER_LEGGINGS ||
               item == Items.LEATHER_BOOTS;
    }
    
    // Netherite
    public static boolean hasNetheriteItems(PlayerData data) {
        return isNetherite(data.helmet) ||
               isNetherite(data.chestplate) ||
               isNetherite(data.leggings) ||
               isNetherite(data.boots) ||
               isNetherite(data.mainHand) ||
               isNetherite(data.offHand);
    }
    
    public static boolean isNetherite(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == Items.NETHERITE_HELMET ||
               item == Items.NETHERITE_CHESTPLATE ||
               item == Items.NETHERITE_LEGGINGS ||
               item == Items.NETHERITE_BOOTS ||
               item == Items.NETHERITE_SWORD ||
               item == Items.NETHERITE_AXE ||
               item == Items.NETHERITE_PICKAXE ||
               item == Items.NETHERITE_SHOVEL ||
               item == Items.NETHERITE_HOE;
    }
    
    // Elytra
    public static boolean hasElytra(PlayerData data) {
        return isElytra(data.chestplate) ||
               isElytra(data.mainHand) ||
               isElytra(data.offHand);
    }
    
    public static boolean isElytra(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.getItem() == Items.ELYTRA;
    }
    
    // Helper do wyświetlania typu gracza
    public static String getPlayerType(PlayerData data) {
        StringBuilder sb = new StringBuilder();
        if (hasNetheriteItems(data)) sb.append("§5⚔ ");
        if (hasLeatherArmor(data)) sb.append("§6🛡 ");
        if (hasElytra(data)) sb.append("§7🪂 ");
        return sb.toString().trim();
    }
}

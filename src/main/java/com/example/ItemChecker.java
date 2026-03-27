package com.example;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class ItemChecker {
    
    public static boolean hasRequiredItems(PlayerData data) {
        return hasLeatherArmor(data) ||
               hasNetheriteItems(data) ||
               hasTrident(data) ||
               hasCustomModelData(data);
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
    
    // Trident
    public static boolean hasTrident(PlayerData data) {
        return isTrident(data.mainHand) || isTrident(data.offHand);
    }
    
    public static boolean isTrident(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.getItem() == Items.TRIDENT;
    }
    
    // Custom Model Data (w ręce)
    public static boolean hasCustomModelData(PlayerData data) {
        return hasCustomModelData(data.mainHand) || hasCustomModelData(data.offHand);
    }
    
    public static boolean hasCustomModelData(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("CustomModelData")) {
            return true;
        }
        return false;
    }
    
    // Helper do wyświetlania typu gracza
    public static String getPlayerType(PlayerData data) {
        StringBuilder sb = new StringBuilder();
        if (hasNetheriteItems(data)) sb.append("§5⚔ ");
        if (hasLeatherArmor(data)) sb.append("§6🛡 ");
        if (hasTrident(data)) sb.append("§b🔱 ");
        if (hasCustomModelData(data)) sb.append("§d✦ ");
        return sb.toString().trim();
    }
}

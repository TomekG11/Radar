package com.example;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class NetheriteChecker {
    public static boolean isNetherite(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        var item = stack.getItem();
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

    public static boolean isElytra(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.getItem() == Items.ELYTRA;
    }
}

package com.example;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.attribute.EntityAttributes;

public class PlayerData {
    public String name;
    public String uuid;
    public ItemStack helmet;
    public ItemStack chestplate;
    public ItemStack leggings;
    public ItemStack boots;
    public ItemStack mainHand;
    public ItemStack offHand;
    public PlayerEntity entity;
    public float health;
    public float maxHealth;
    public float absorption;
    public int armor;
    public float armorToughness;

    public PlayerData(PlayerEntity player) {
        if (player == null) {
            this.name = "";
            this.uuid = "";
            this.entity = null;
            this.helmet = ItemStack.EMPTY;
            this.chestplate = ItemStack.EMPTY;
            this.leggings = ItemStack.EMPTY;
            this.boots = ItemStack.EMPTY;
            this.mainHand = ItemStack.EMPTY;
            this.offHand = ItemStack.EMPTY;
            this.health = 0.0F;
            this.maxHealth = 20.0F;
            this.absorption = 0.0F;
            this.armor = 0;
            this.armorToughness = 0.0F;
        } else {
            this.name = player.getName().getString();
            this.uuid = player.getUuidAsString();
            this.entity = player;
            this.helmet = player.getInventory().getArmorStack(3).copy();
            this.chestplate = player.getInventory().getArmorStack(2).copy();
            this.leggings = player.getInventory().getArmorStack(1).copy();
            this.boots = player.getInventory().getArmorStack(0).copy();
            this.mainHand = player.getMainHandStack().copy();
            this.offHand = player.getOffHandStack().copy();
            this.health = player.getHealth();
            this.maxHealth = player.getMaxHealth();
            this.absorption = player.getAbsorptionAmount();
            this.armor = player.getArmor();
            this.armorToughness = (float) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        }
    }
}

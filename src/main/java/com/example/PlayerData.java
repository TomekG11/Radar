package com.example;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_5134;

public class PlayerData {
    public String name;
    public String uuid;
    public class_1799 helmet;
    public class_1799 chestplate;
    public class_1799 leggings;
    public class_1799 boots;
    public class_1799 mainHand;
    public class_1799 offHand;
    public class_1657 entity;
    public float health;
    public float maxHealth;
    public float absorption;
    public int armor;
    public float armorToughness;

    public PlayerData(class_1657 player) {
        if (player == null) {
            this.name = "";
            this.uuid = "";
            this.entity = null;
            this.helmet = class_1799.field_8037;
            this.chestplate = class_1799.field_8037;
            this.leggings = class_1799.field_8037;
            this.boots = class_1799.field_8037;
            this.mainHand = class_1799.field_8037;
            this.offHand = class_1799.field_8037;
            this.health = 0.0F;
            this.maxHealth = 20.0F;
            this.absorption = 0.0F;
            this.armor = 0;
            this.armorToughness = 0.0F;
        } else {
            this.name = player.method_5477().getString();
            this.uuid = player.method_5845();
            this.entity = player;
            this.helmet = player.method_31548().method_7372(3).method_7972();
            this.chestplate = player.method_31548().method_7372(2).method_7972();
            this.leggings = player.method_31548().method_7372(1).method_7972();
            this.boots = player.method_31548().method_7372(0).method_7972();
            this.mainHand = player.method_6047().method_7972();
            this.offHand = player.method_6079().method_7972();
            this.health = player.method_6032();
            this.maxHealth = player.method_6063();
            this.absorption = player.method_6067();
            this.armor = player.method_6096();
            this.armorToughness = (float)player.method_26825(class_5134.field_23725);
        }
    }
}

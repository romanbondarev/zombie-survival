package com.gdx.game.items.armor;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.items.Item;

public class Armor extends Item {
    public enum ArmorType {
        HEAD, BODY
    }

    private int damageAbsorptionLevel;
    private int maxWear;
    private int currentWearLevel;
    private ArmorType armorType;

    public Armor(ArmorType armorType, String name, int damageAbsorptionLevel, int maxWearLevel, Texture circle, Texture square) {
        super(ItemType.ARMOR, name, circle, square);
        this.damageAbsorptionLevel = damageAbsorptionLevel;
        this.maxWear = maxWearLevel;
        this.armorType = armorType;
    }

    public void wear(int amount) {
        currentWearLevel += amount;
    }

    public boolean isBroken() {
        return currentWearLevel >= maxWear;
    }

    public int getDamageAbsorptionLevel() {
        return damageAbsorptionLevel;
    }

    public float getWearLevel() {
        return ((float) currentWearLevel) / ((float) maxWear);
    }

    public ArmorType getArmorType() {
        return armorType;
    }
}

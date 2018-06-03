package com.gdx.game.items.weapons.ammo;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.items.Item;
import com.gdx.game.items.weapons.Weapon.WeaponType;

public abstract class Ammo extends Item {
    private int bullets;
    private WeaponType weaponType;

    public Ammo(WeaponType weaponType, int bullets, String name, Texture round, Texture square) {
        super(ItemType.WEAPON_ACCESSORY, name, round, square);
        this.weaponType = weaponType;
        this.bullets = bullets;
    }

    public int load(int amount) {
        if (amount <= bullets) {
            bullets -= amount;
            return amount;
        }
        return 0;
    }

    public int getBulletsAmount() {
        return bullets;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public String toString() {
        return weaponType + " AMMO: " + bullets;
    }
}

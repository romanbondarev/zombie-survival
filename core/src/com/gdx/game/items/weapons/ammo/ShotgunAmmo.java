package com.gdx.game.items.weapons.ammo;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;
import com.gdx.game.items.weapons.Weapon;

public class ShotgunAmmo extends Ammo {
    public ShotgunAmmo(int bullets, String name) {
        super(Weapon.WeaponType.SHOTGUN, bullets, name,
                Application.assetManager.get("ui/inventory-items-round/shotgunAmmo.png", Texture.class),
                Application.assetManager.get("ui/inventory-items/shotgunAmmo.png", Texture.class));
    }
}

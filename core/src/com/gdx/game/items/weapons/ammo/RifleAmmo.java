package com.gdx.game.items.weapons.ammo;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;
import com.gdx.game.items.weapons.Weapon;

public final class RifleAmmo extends Ammo {
    public RifleAmmo(int bullets, String name) {
        super(Weapon.WeaponType.RIFLE, bullets, name,
                Application.assetManager.get("ui/inventory-items-round/rifleAmmo.png", Texture.class),
                Application.assetManager.get("ui/inventory-items/rifleAmmo.png", Texture.class));
    }
}

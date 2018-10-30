package com.gdx.game.items.weapons.ammo;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;
import com.gdx.game.items.weapons.Weapon;

public final class HandgunAmmo extends Ammo {
    public HandgunAmmo(int bullets, String name) {
        super(Weapon.WeaponType.HANDGUN, bullets, name,
                Application.assetManager.get("ui/inventory-items-round/handgunAmmo.png", Texture.class));
    }
}

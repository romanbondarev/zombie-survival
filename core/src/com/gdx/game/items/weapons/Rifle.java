package com.gdx.game.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;
import com.gdx.game.utils.Constants;

public final class Rifle extends Weapon {
    public Rifle(String name) {
        super(WeaponType.RIFLE, name, Constants.rifleDamage, 25,
                Application.assetManager.get("ui/inventory-items-round/rifle.png", Texture.class));
    }
}

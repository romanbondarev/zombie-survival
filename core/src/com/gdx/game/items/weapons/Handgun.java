package com.gdx.game.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;
import com.gdx.game.utils.Constants;

public final class Handgun extends Weapon {
    public Handgun(String name) {
        super(WeaponType.HANDGUN, name, Constants.handgunDamage, 10,
                Application.assetManager.get("ui/inventory-items-round/handgun.png", Texture.class));
    }
}

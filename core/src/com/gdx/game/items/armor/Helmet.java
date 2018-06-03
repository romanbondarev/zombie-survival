package com.gdx.game.items.armor;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;

public final class Helmet extends Armor {
    public Helmet(String name, int damageAbsorption, int maxWear) {
        super(ArmorType.HEAD, name, damageAbsorption, maxWear,
                Application.assetManager.get("ui/inventory-items-round/helmet.png", Texture.class),
                Application.assetManager.get("ui/inventory-items/helmet.png", Texture.class));
    }
}

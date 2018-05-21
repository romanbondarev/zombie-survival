package com.gdx.game.items.armor;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;

public final class Vest extends Armor {
    public Vest(String name, int damageAbsorption, int maxWear) {
        super(ArmorType.BODY, name, damageAbsorption, maxWear, Application.assetManager.get("ui/inventory-items-round/vest.png", Texture.class));
    }
}

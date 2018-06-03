package com.gdx.game.items;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.game.Application;
import com.gdx.game.models.Player;

public class MedKit extends Item {
    private int healHP;

    public MedKit(int healHP) {
        super(ItemType.FIRST_AID_KIT, "MEDKIT",
                Application.assetManager.get("ui/inventory-items-round/medkit.png"),
                Application.assetManager.get("ui/inventory-items/medkit.png", Texture.class));
        this.healHP = healHP;
    }

    public void heal(Player player) {
        if (player.getHealth() + healHP > 100) player.setHealth(100);
        else player.setHealth(player.getHealth() + healHP);
        healHP = 0;
    }
}

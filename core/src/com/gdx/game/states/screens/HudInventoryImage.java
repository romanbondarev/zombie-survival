package com.gdx.game.states.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class HudInventoryImage extends Image {
    private int id;

    public HudInventoryImage(Texture texture, int id) {
        super(texture);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

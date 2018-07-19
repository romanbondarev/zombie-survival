package com.gdx.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ItemImage extends Image {
    private int id;

    public ItemImage(Texture texture, int id) {
        super(texture);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

package com.gdx.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * SHOULD BE DELETED IF HudImageButton IS IMPLEMENTED,
 * This is the previous way of drawing ui "buttons" in the game
 */
@Deprecated
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

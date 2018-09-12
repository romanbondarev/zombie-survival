package com.gdx.game.components;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.gdx.game.items.Inventory;
import com.gdx.game.utils.Constants;

public class HudImageButton extends ImageSelectorButton {
    private int id;
    private Inventory inventory;

    public HudImageButton(Skin skin, int id, Inventory inventory) {
        super(skin);
        this.id = id;
        this.inventory = inventory;
    }

    public HudImageButton(Skin skin, Drawable imageUp, int id) {
        super(skin, imageUp);
        this.id = id;
    }

    public HudImageButton(Skin skin, Drawable imageUp, int lineSize, int id) {
        super(skin, imageUp, lineSize);
        this.id = id;
    }

    public HudImageButton(Skin skin, Drawable imageUp, int lineSize, int alignment, int id) {
        super(skin, imageUp, lineSize, alignment);
        this.id = id;
    }

    @Override
    protected void updateImage() {
        if (id >= Constants.INVENTORY_SIZE) {
            getStyle().imageUp = inventory != null && inventory.getTray()[id - Constants.INVENTORY_SIZE] != null
                    ? new SpriteDrawable(inventory.getTray()[id - Constants.INVENTORY_SIZE].getSquareSprite()) : null;
        } else if (id > -1) {
            getStyle().imageUp = inventory != null && inventory.getInventory().get(id) != null
                    ? new SpriteDrawable(inventory.getInventory().get(id).getSquareSprite()) : null;
        } else getStyle().imageUp = null;
        super.updateImage();
    }

    public int getId() {
        return id;
    }
}

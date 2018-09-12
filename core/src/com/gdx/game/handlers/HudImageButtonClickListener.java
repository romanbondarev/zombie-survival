package com.gdx.game.handlers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.game.items.Inventory;
import com.gdx.game.utils.Constants;

import static com.gdx.game.utils.Constants.INVENTORY_SIZE;

public class HudImageButtonClickListener extends ClickListener {

    private final Inventory inventory;
    private final int id;

    public HudImageButtonClickListener(Inventory inventory, int id) {
        this.inventory = inventory;
        this.id = id;
    }

    public HudImageButtonClickListener(int button, Inventory inventory, int id) {
        super(button);
        this.inventory = inventory;
        this.id = id;
    }

    // TODO: 26.08.2018 see the ImageSelectorButton, image is changed there
    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (id == -1) inventory.toggle();
        else if (id < Constants.INVENTORY_SIZE) {
            inventory.selectItem(inventory.getItem(id));
        } else {
            inventory.returnItemToInventory(inventory.getTray()[id - INVENTORY_SIZE]);
        }
    }
}

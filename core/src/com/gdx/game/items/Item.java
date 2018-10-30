package com.gdx.game.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Item {
    private ItemType itemType;
    private String name;
    private Sprite sprite;

    public enum ItemType {
        WEAPON, WEAPON_ACCESSORY, FIRST_AID_KIT, ARMOR
    }

    public Item(ItemType itemType, String name, Texture texture) {
        this.itemType = itemType;
        this.name = name;
        this.sprite = new Sprite(texture);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setSpritePosition(float x, float y) {
        sprite.setPosition(x, y);
    }

    public void rotateSprite(int degrees) {
        getSprite().rotate(degrees);
    }

    @Override
    public String toString() {
        return getName();
    }
}

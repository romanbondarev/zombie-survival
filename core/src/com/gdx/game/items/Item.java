package com.gdx.game.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Item {
    private ItemType itemType;
    private String name;
    private Sprite roundSprite;
    private Sprite squareSprite;

    public enum ItemType {
        WEAPON, WEAPON_ACCESSORY, FIRST_AID_KIT, ARMOR
    }

    public Item(ItemType itemType, String name, Texture round, Texture square) {
        this.itemType = itemType;
        this.name = name;
        this.roundSprite = new Sprite(round);
        this.squareSprite = new Sprite(square);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public Sprite getRoundSprite() {
        return roundSprite;
    }

    public Sprite getSquareSprite() {
        return squareSprite;
    }

    public void setRoundSprite(Sprite roundSprite) {
        this.roundSprite = roundSprite;
    }

    public void setSpritePosition(float x, float y) {
        roundSprite.setPosition(x, y);
    }

    public void rotateSprite(int degrees) {
        getRoundSprite().rotate(degrees);
    }

    @Override
    public String toString() {
        return getName();
    }
}

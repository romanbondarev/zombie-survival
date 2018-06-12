package com.gdx.game.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Item {
    private ItemType itemType;
    private String name;
    private Sprite circleSprite;
    private Sprite squareSprite;

    /**
     * Item types
     */
    public enum ItemType {
        WEAPON, WEAPON_ACCESSORY, FIRST_AID_KIT, ARMOR
    }

    /**
     * Creates an item with a type, name and two textures.
     */
    public Item(ItemType itemType, String name, Texture round, Texture square) {
        this.itemType = itemType;
        this.name = name;
        this.circleSprite = new Sprite(round);
        this.squareSprite = new Sprite(square);
    }

    /**
     * Gets the item's type.
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * Gets the item's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the item's circle shaped sprite.
     */
    public Sprite getCircleSprite() {
        return circleSprite;
    }

    /**
     * Gets the item's square shaped sprite.
     */
    public Sprite getSquareSprite() {
        return squareSprite;
    }

    /**
     * Sets the circle shaped sprite's position.
     *
     * @param x x-axis coordinate
     * @param y y-axis coordinate
     */
    public void setCircleSpritePosition(float x, float y) {
        circleSprite.setPosition(x, y);
    }

    /**
     * Sets the square shaped sprite's position .
     *
     * @param x x-axis coordinate
     * @param y y-axis coordinate
     */
    public void setRoundedSpritePosition(float x, float y) {
        circleSprite.setPosition(x, y);
    }

    /**
     * Sets the sprite's rotation in degrees relative to the current rotation.
     */
    public void rotateSprite(int degrees) {
        getCircleSprite().rotate(degrees);
    }

    /**
     * Replaces the circle shape sprite's sprite.
     */
    @Deprecated
    public void setCircleSprite(Sprite circleSprite) {
        this.circleSprite = circleSprite;
    }

    @Override
    public String toString() {
        return getName();
    }
}

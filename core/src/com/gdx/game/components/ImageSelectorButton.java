package com.gdx.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.gdx.game.utils.Constants;

public class ImageSelectorButton extends ImageButton {
    private final static int DEFAULT_LINE_SIZE = 0;
    private final static int DEFAULT_LINE_ALIGNMENT = Align.right;

    private Texture selector = new Texture("ui/buttons/selector-vertical.png");
    private Texture selectorGreen = new Texture("ui/buttons/selector-vertical-green.png");
    private int lineSize;
    private int alignment;
    private BooleanListener booleanListener;

    public ImageSelectorButton(Skin skin) {
        super(skin);
        this.lineSize = DEFAULT_LINE_SIZE;
        this.alignment = DEFAULT_LINE_ALIGNMENT;
    }

    public ImageSelectorButton(Skin skin, Drawable imageUp) {
        super(skin);
        getStyle().imageUp = imageUp;
        this.lineSize = DEFAULT_LINE_SIZE;
        this.alignment = DEFAULT_LINE_ALIGNMENT;
    }

    public ImageSelectorButton(Skin skin, Drawable imageUp, int lineSize) {
        super(skin);
        getStyle().imageUp = imageUp;
        this.lineSize = lineSize;
        this.alignment = DEFAULT_LINE_ALIGNMENT;
    }

    public ImageSelectorButton(Skin skin, Drawable imageUp, int lineSize, int alignment) {
        super(skin);
        getStyle().imageUp = imageUp;
        this.lineSize = lineSize;
        this.alignment = alignment;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        switch (alignment) {
            case Align.top:
                if (getBoolean()) {
                    batch.draw(selectorGreen, getX(), getY() + getHeight() - lineSize, getWidth(), lineSize);
                } else batch.draw(selector, getX(), getY() + getHeight() - lineSize, getWidth(), lineSize);
                break;
            case Align.bottom:
                if (getBoolean()) {
                    batch.draw(selectorGreen, getX(), getY(), getWidth(), lineSize);
                } else batch.draw(selector, getX(), getY(), getWidth(), lineSize);
                break;
            case Align.left:
                if (getBoolean()) {
                    batch.draw(selectorGreen, getX(), getY(), lineSize, getHeight());
                } else batch.draw(selector, getX(), getY(), lineSize, getHeight());
                break;
            default:
                if (getBoolean()) {
                    batch.draw(selectorGreen, getX() + getWidth() - lineSize, getY(), lineSize, getHeight());
                } else batch.draw(selector, getX() + getWidth() - lineSize, getY(), lineSize, getHeight());
                break;
        }
    }

    public void addBooleanListener(BooleanListener statement) {
        this.booleanListener = statement;
    }

    public boolean getBoolean() {
        return booleanListener != null && booleanListener.getBoolean();
    }

    /**
     * Class for boolean variable tracking.
     */
    public static class BooleanListener {
        public enum Type {
            DEBUG, DIFFICULT_GAME, SHARP_MOVEMENT, CAMERA_LERP, IN_GAME_PAUSE, MENU_ON
        }

        private Type type;

        public BooleanListener(Type type) {
            this.type = type;
        }

        public boolean getBoolean() {
            if (type.equals(Type.DEBUG)) return Constants.DEBUG;
            if (type.equals(Type.DIFFICULT_GAME)) return Constants.DIFFICULT_GAME;
            if (type.equals(Type.SHARP_MOVEMENT)) return Constants.SMOOTH_MOVEMENT;
            if (type.equals(Type.CAMERA_LERP)) return Constants.CAMERA_LERP;
            if (type.equals(Type.IN_GAME_PAUSE)) return Constants.IN_GAME_PAUSE;
            if (type.equals(Type.MENU_ON)) return Constants.MENU_ON;
            return false;
        }
    }
}
package com.gdx.game.states.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.gdx.game.utils.Constants;

public class HudButton extends TextButton {
    // TODO: 18.07.2018 rename class
    private Texture line = new Texture("ui/buttons/selector-vertical.png");
    private Texture lineGreen = new Texture("ui/buttons/selector-vertical-green.png");
    private int lineWidth = 12;
    private int alignment;
    private BooleanListener booleanListener;

    public HudButton(String text, Skin skin) {
        super(text, skin);
    }


    public HudButton(String text, Skin skin, int lineWidth) {
        super(text, skin);
        this.lineWidth = lineWidth;
    }

    public HudButton(String text, Skin skin, int lineWidth, int alignment) {
        super(text, skin);
        this.lineWidth = lineWidth;
        this.alignment = alignment;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (getBoolean()) batch.draw(lineGreen, getX() + getWidth() - lineWidth, getY(), lineWidth, getHeight());
        else batch.draw(line, getX() + getWidth() - lineWidth, getY(), lineWidth, getHeight());
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

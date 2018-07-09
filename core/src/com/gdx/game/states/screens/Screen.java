package com.gdx.game.states.screens;

public interface Screen {
    void render();

    void update();

    void dispose();

    void resize(int width, int height);

    void resetInputProcessor();
}

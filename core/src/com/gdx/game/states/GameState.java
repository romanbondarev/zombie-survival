package com.gdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.game.Application;
import com.gdx.game.managers.GameStateManager;

public abstract class GameState {
    GameStateManager gameStateManager;
    Application application;

    SpriteBatch batch;
    OrthographicCamera camera;

    GameStateManager.State state;

    public GameState(GameStateManager gameStateManager, GameStateManager.State state) {
        this.gameStateManager = gameStateManager;
        this.application = gameStateManager.getApplication();
        this.batch = application.getSpriteBatch();
        this.camera = application.getCamera();
        this.state = state;
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public abstract void update(float delta);

    public abstract void render();

    public abstract void dispose();

    public GameStateManager.State getState() {
        return state;
    }

    public Application getApplication() {
        return application;
    }
}

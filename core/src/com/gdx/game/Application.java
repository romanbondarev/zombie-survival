package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.game.managers.GameStateManager;

import static com.gdx.game.utils.Constants.SCALE;

public class Application extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private GameStateManager gameStateManager;
    public static AssetManager assetManager;

    @Override
    public void create() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width / SCALE, height / SCALE);
        gameStateManager = new GameStateManager(this);
    }

    @Override
    public void render() {
        gameStateManager.update(Gdx.graphics.getDeltaTime());
        gameStateManager.render();
    }

    @Override
    public void resize(int width, int height) {
        gameStateManager.resize((int) (width / SCALE), (int) (height / SCALE));
    }

    @Override
    public void dispose() {
        gameStateManager.dispose();
        batch.dispose();
        assetManager.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }
}

package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.game.managers.GameStateManager;

import static com.gdx.game.utils.Constants.SCALE;

/*
 * Dear, me!
 *
 * I'm truly sorry for the crap I wrote below.
 * In order to make this work I have done unforgivable things.
 * But it works, huraaaay! :)
 *
 * I believe that current you is more experienced and knows what he is doing, because I don't!
 * You had enough of courage if you have decided to finnish this project.
 *
 * May the refactoring be with you,
 * You :3
 *
 * 19.08.2018
 */

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

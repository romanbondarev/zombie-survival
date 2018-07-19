package com.gdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.items.weapons.Weapon;
import com.gdx.game.models.Enemy;
import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.states.screens.Screen;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static com.gdx.game.utils.WCC.getPPM;
import static com.gdx.game.utils.WCC.worldToPixels;

public class Debugger implements Screen {
    private PlayState state;
    private Stage stage;
    private SpriteBatch mainBatch;
    private SpriteBatch itemBatch;
    private ShapeRenderer shapeRenderer;
    private Camera camera;
    private Box2DDebugRenderer b2dr;
    private BitmapFont agency;
    private LocalDateTime startTime;

    public Debugger(GameState state) {
        this.state = ((PlayState) state);
        this.stage = new Stage(new ScreenViewport());
        this.mainBatch = new SpriteBatch();
        this.itemBatch = new SpriteBatch();
        this.camera = ((PlayState) state).getCamera();
        this.b2dr = new Box2DDebugRenderer();
        this.shapeRenderer = new ShapeRenderer();
        this.startTime = LocalDateTime.now();
        this.agency = Application.assetManager.get("agency-fb.ttf", BitmapFont.class);
        this.agency.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.agency.getData().setScale(0.75f);
    }

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);

        List<Enemy> enemies = new LinkedList<>();
        enemies.addAll(state.getZombies());
        enemies.addAll(state.getZombieShooters());

        Player player = state.getPlayer();

        for (Enemy enemy : enemies) {
            // Connections between enemies and the player
            if (Utils.getDistanceBetween(enemy.getPosition(), player.getPosition()) < 10) {
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.line(
                        worldToPixels(player.getPosition().x),
                        worldToPixels(player.getPosition().y),
                        worldToPixels(enemy.getPosition().x),
                        worldToPixels(enemy.getPosition().y)
                );
            }

            for (Enemy enemyCopy : enemies) {
                // Connections between enemies themselves
                if (Utils.getDistanceBetween(enemy.getPosition(), enemyCopy.getPosition()) < 10) {
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.line(
                            worldToPixels(enemyCopy.getPosition().x),
                            worldToPixels(enemyCopy.getPosition().y),
                            worldToPixels(enemy.getPosition().x),
                            worldToPixels(enemy.getPosition().y)
                    );
                }
            }
        }

        state.getItemSpawners().forEach(spawner ->
                shapeRenderer.circle(
                        spawner.getX() + spawner.getBase().getCircleSprite().getWidth() / 2,
                        spawner.getY() + spawner.getBase().getCircleSprite().getHeight() / 2,
                        25)
        );

        shapeRenderer.end();

        /* Top-left statistics */
        mainBatch.begin();
        agency.draw(mainBatch,
                "DURATION = " + printTime() + "\n" +
                        "KILLED = " + state.getKillCounter() + "\n" +
                        "HP = " + state.getPlayer().getHealth() + "\n" +
                        "WEAPON'S DAMAGE = " + (state.getPlayer().getInventory().getSelectedCellItem() != null ? ((Weapon) state.getPlayer().getInventory().getSelectedCellItem()).getDamage() : null) + "\n" +
                        "TOP SCORE = " + (Utils.getTopScoreJSON().isPresent() ? Utils.getTopScoreJSON().get().get("zombies-killed") : null) + "\n" +
                        "HELMET = " + (state.getPlayer().getInventory().getHelmetArmor() != null ? 100 - state.getPlayer().getInventory().getHelmetArmor().getWearLevel() : null) + "\n" +
                        "VEST = " + (state.getPlayer().getInventory().getVestArmor() != null ? 100 - state.getPlayer().getInventory().getVestArmor().getWearLevel() : null),
                20, Gdx.graphics.getHeight() - 20);
        mainBatch.end();


        /* Item spawner's circle with timer */
        itemBatch.setProjectionMatrix(camera.combined);
        itemBatch.begin();

        state.getItemSpawners().forEach(spawner -> agency.draw(
                itemBatch,
                String.valueOf(100 - Math.round(((float) spawner.getCounter()) / spawner.getTimeLimit() * 100.0)),
                spawner.getX() + 3, spawner.getY() + 30, 40, Align.center, true)
        );

        itemBatch.end();

        /* Box2d objects outline renderer */
        b2dr.render(state.getWorld(), camera.combined.scl(getPPM()));
    }

    private String printTime() {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long seconds = Math.abs(duration.getSeconds());
        long hours = seconds / 3600;
        seconds -= (hours * 3600);
        long minutes = seconds / 60;
        seconds -= (minutes * 60);
        return hours + " hours; " + minutes + " minutes; " + seconds + " seconds";
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        mainBatch.dispose();
        itemBatch.dispose();
        shapeRenderer.dispose();
        b2dr.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

package com.gdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.gdx.game.Application;
import com.gdx.game.items.weapons.Weapon;
import com.gdx.game.models.Enemy;
import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.states.screens.Screen;
import com.gdx.game.utils.spawners.ItemSpawner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static com.gdx.game.utils.WCC.getPPM;
import static com.gdx.game.utils.WCC.worldToPixels;

public class Debugger implements Screen {
    private PlayState state;
    private SpriteBatch batch;
    private SpriteBatch batch2;
    private ShapeRenderer shapeRenderer;
    private Camera camera;
    private Box2DDebugRenderer b2dr;
    private Skin skin;
    private BitmapFont agency;
    private LocalDateTime starting;

    public Debugger(GameState state) {
        this.state = ((PlayState) state);
        this.batch = new SpriteBatch();
        this.batch2 = new SpriteBatch();
        this.camera = ((PlayState) state).getCamera();
        this.b2dr = new Box2DDebugRenderer(); // Box2D renderer
        this.shapeRenderer = new ShapeRenderer();
        this.starting = LocalDateTime.now();

        skin = new Skin();
        skin.addRegions(new TextureAtlas("ui/uiSkin.atlas"));
        agency = Application.assetManager.get("agency-fb.ttf", BitmapFont.class);
        agency.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        agency.getData().setScale(0.75f);
        skin.add("default-font", agency, BitmapFont.class);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));
    }

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        List<Enemy> enemies = new LinkedList<>();
        enemies.addAll(state.getZombies());
        enemies.addAll(state.getZombieShooters());

        for (Enemy enemy : enemies) {
            Player player = state.getPlayer();
            if (Utils.getDistanceBetween(enemy.getPosition(), player.getPosition()) < 10) {
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.line(worldToPixels(player.getPosition().x), worldToPixels(player.getPosition().y), worldToPixels(enemy.getPosition().x), worldToPixels(enemy.getPosition().y));
            }
            for (Enemy enemy1 : enemies) {
                if (Utils.getDistanceBetween(enemy.getPosition(), enemy1.getPosition()) < 10) {
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.line(worldToPixels(enemy1.getPosition().x), worldToPixels(enemy1.getPosition().y), worldToPixels(enemy.getPosition().x), worldToPixels(enemy.getPosition().y));
                }
            }
        }

        state.getItemSpawners().forEach(sp -> shapeRenderer.circle(
                sp.getX() + sp.getBase().getCircleSprite().getWidth() / 2,
                sp.getY() + sp.getBase().getCircleSprite().getHeight() / 2,
                25));

        shapeRenderer.end();

        batch.begin();
        agency.draw(batch,
                "DURATION = " + printTime() + "\n" +
                        "KILLED = " + state.getKillCounter() + "\n" +
                        "HP = " + state.getPlayer().getHealth() + "\n" +
                        "WEAPON'S DAMAGE = " + (state.getPlayer().getInventory().getSelectedCellItem() != null ? ((Weapon) state.getPlayer().getInventory().getSelectedCellItem()).getDamage() : null) + "\n" +
                        "TOP SCORE = " + (Utils.getTopScoreJSON().isPresent() ? Utils.getTopScoreJSON().get().get("zombies-killed") : null) + "\n" +
                        "HELMET = " + (state.getPlayer().getInventory().getHelmetArmor() != null ? 100 - state.getPlayer().getInventory().getHelmetArmor().getWearLevel() : null) + "\n" +
                        "VEST = " + (state.getPlayer().getInventory().getVestArmor() != null ? 100 - state.getPlayer().getInventory().getVestArmor().getWearLevel() : null),
                20, Gdx.graphics.getHeight() - 20);
        batch.end();

        batch2.setProjectionMatrix(camera.combined);
        batch2.begin();
        for (ItemSpawner spawner : state.getItemSpawners()) {
            agency.draw(batch2, String.valueOf(100 - Math.round(((float) spawner.getCounter()) / spawner.getTimeLimit() * 100.0)), spawner.getX() + 3, spawner.getY() + 30, 40, Align.center, true);
        }
        batch2.end();

        b2dr.render(state.getWorld(), camera.combined.scl(getPPM()));
    }

    private String printTime() {
        Duration duration = Duration.between(starting, LocalDateTime.now());
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
        batch.dispose();
        batch2.dispose();
        shapeRenderer.dispose();
        b2dr.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void resetInputProcessor() {

    }
}

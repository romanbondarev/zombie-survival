package com.gdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.Application;
import com.gdx.game.handlers.GameContactListener;
import com.gdx.game.items.Item;
import com.gdx.game.items.weapons.Bullet;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.managers.Sounds;
import com.gdx.game.models.Player;
import com.gdx.game.models.Zombie;
import com.gdx.game.models.ZombieShooter;
import com.gdx.game.states.screens.GameOver;
import com.gdx.game.states.screens.Hud;
import com.gdx.game.states.screens.Pause;
import com.gdx.game.utils.CameraStyles;
import com.gdx.game.utils.Constants;
import com.gdx.game.utils.Debugger;
import com.gdx.game.utils.FrameRate;
import com.gdx.game.utils.TMLP;
import com.gdx.game.utils.spawners.ItemSpawner;
import com.gdx.game.utils.spawners.Spawner;

import java.util.LinkedList;
import java.util.List;

import static com.gdx.game.utils.Constants.CAMERA_LERP;
import static com.gdx.game.utils.Constants.DIFFICULT_GAME;
import static com.gdx.game.utils.Constants.GRAVITY;
import static com.gdx.game.utils.Constants.SHARP_MOVEMENT;
import static com.gdx.game.utils.WCC.worldToPixels;

public class PlayState extends GameState {
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    private FrameRate frameRate;
    private Hud hud;
    private Pause pause;
    private GameOver gameOver;
    private Debugger debugger;

    private World world;
    private Player player;
    private List<Bullet> bullets = new LinkedList<>();
    private List<Zombie> zombies = new LinkedList<>();
    private List<ZombieShooter> zombieShooters = new LinkedList<>();
    private List<Spawner> spawners = new LinkedList<>();
    private List<Item> items = new LinkedList<>();
    private List<ItemSpawner> itemSpawners = new LinkedList<>();

    private int levelWidth;
    private int levelHeight;
    private int killCounter = 0;

    public PlayState(GameStateManager gameStateManager, GameStateManager.State state) {
        super(gameStateManager, state);
        world = new World(new Vector2(0, GRAVITY), false);
        world.setContactListener(new GameContactListener());
        player = new Player(this, 600, 1000, 100);

        tiledMap = DIFFICULT_GAME ? Application.assetManager.get("map/mapDifficult.tmx", TiledMap.class) : Application.assetManager.get("map/map.tmx", TiledMap.class);
        levelHeight = tiledMap.getProperties().get("height", Integer.class);
        levelWidth = tiledMap.getProperties().get("width", Integer.class);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TMLP.parseMapLayers(world, tiledMap, "collision", "objects-collision", "houses-collision");
        TMLP.parserMapItems(this, tiledMap, "inventory-items");
        TMLP.parseMapSpawners(this, player, tiledMap, "spawners-zombies");

        frameRate = new FrameRate();
        hud = new Hud(application, this, player);
        pause = new Pause(application);
        gameOver = new GameOver(application);
        debugger = new Debugger(this);

        Music music = Sounds.backgroundMusic();
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Constants.DEAD) {
            gameOver.render();
        } else if (!Constants.IN_GAME_PAUSE) {
            mapRenderer.render(); // Render map

            batch.begin();
            bullets.forEach(bullet -> bullet.getSprite().draw(batch)); // Render bullets textures
            items.forEach(item -> item.getCircleSprite().draw(batch)); // Render items on map
            zombies.forEach(zombie -> zombie.getZombieAnimation().renderAnimation(batch)); // Render the zombies animations
            zombieShooters.forEach(zombie -> zombie.getZombieShooterAnimation().renderAnimation(batch)); // Render the zombieShooters animations
            player.render(); // Render the player animations
            batch.end();

            hud.render(); // Hud
            if (Constants.DEBUG) debugger.render();
        } else {
            pause.render();
        }
        frameRate.render(); // Frames per second indicator
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Constants.IN_GAME_PAUSE = !Constants.IN_GAME_PAUSE;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) Constants.DEBUG = !Constants.DEBUG;
        if (Constants.DEAD) {
            gameOver.resetInputProcessor();
            gameOver.update(killCounter);
        } else if (!Constants.IN_GAME_PAUSE) {
            hud.update();
            world.step(1 / 60f, 6, 2);
            bullets.forEach(Bullet::update); // Update the position of each bullet
            zombies.forEach(zombie -> zombie.update(player)); // Update the position of each zombie
            zombieShooters.forEach(shooter -> shooter.update(player)); // Update the position of each zombieShooter
            items.forEach(item -> item.rotateSprite(-1)); // Item rotation
            itemSpawners.forEach(ItemSpawner::update); // Update the spawner's item respawn time
            spawners.forEach(Spawner::update); // Update the spawner's zombie respawn time

            inputUpdate(); // Key press events
            cameraUpdate(); // Updates the position of camera
            objectDeletion(); // Delete collided bullets or dead zombies

            mapRenderer.setView(camera);
            batch.setProjectionMatrix(camera.combined);
        } else {
            pause.update(killCounter);
        }
        frameRate.update(); // FPS render
    }

    private void objectDeletion() {
        /* Bullet deletion */
        for (Bullet bullet : new LinkedList<>(bullets)) {
            if (bullet.getBody().getUserData() != null && (((Bullet) bullet.getBody().getUserData()).isCanDelete())) {
                world.destroyBody(bullet.getBody());
                bullets.remove(bullet);
            }
        }

        /* Zombie deletion and killing */
        for (Zombie zombie : new LinkedList<>(zombies)) {
            if (zombie.getHP() <= 0) {
                world.destroyBody(zombie.getBody());
                zombies.remove(zombie);
                killCounter++;
            }
        }

        /* ZombieShooter deletion and killing */
        for (ZombieShooter zombieShooter : new LinkedList<>(zombieShooters)) {
            if (zombieShooter.getHP() <= 0) {
                world.destroyBody(zombieShooter.getBody());
                zombieShooters.remove(zombieShooter);
                killCounter++;
            }
        }

        /* Items are deleted from map differently, there is no update() method for them,
         * because items do not have any state that must be updated (they are static).
         * So deletion and addition is made through adding and removing item from list (items, see above). */
    }

    private void cameraUpdate() {
        float lerp = !CAMERA_LERP ? 1f : 0.15f;
        Vector3 position = camera.position;
        position.x = camera.position.x + (worldToPixels(player.getPosition().x) - camera.position.x) * lerp;
        position.y = camera.position.y + (worldToPixels(player.getPosition().y) - camera.position.y) * lerp;
        camera.position.set(position);
        camera.update();
        CameraStyles.boundary(camera, camera.viewportWidth / 2, camera.viewportHeight / 2, levelWidth * 32 - camera.viewportWidth, levelHeight * 32 - camera.viewportHeight);
    }

    private void inputUpdate() {
        player.update();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) SHARP_MOVEMENT = !SHARP_MOVEMENT;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) CAMERA_LERP = !CAMERA_LERP;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        frameRate.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        player.getAnimation().dispose();
        mapRenderer.dispose();
        tiledMap.dispose();
        frameRate.dispose();
        hud.dispose();
        gameOver.dispose();
        debugger.dispose();
        world.dispose();
    }

    public void exitState(GameStateManager.State gameState) {
        gameStateManager.setState(gameState, true);
    }

    public World getWorld() {
        return world;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public Camera getCamera() {
        return camera;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Hud getHud() {
        return hud;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public List<ZombieShooter> getZombieShooters() {
        return zombieShooters;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Spawner> getSpawners() {
        return spawners;
    }

    public List<ItemSpawner> getItemSpawners() {
        return itemSpawners;
    }

    public Player getPlayer() {
        return player;
    }

    public int getKillCounter() {
        return killCounter;
    }
}

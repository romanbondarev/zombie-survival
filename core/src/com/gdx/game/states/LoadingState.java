package com.gdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.utils.Constants;

public class LoadingState extends GameState {
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin skin;
    private float progress;
    private boolean canSwitch = false;

    public LoadingState(GameStateManager gameStateManager, GameStateManager.State state) {
        super(gameStateManager, state);
        stage = new Stage(new ScreenViewport());
        shapeRenderer = new ShapeRenderer();
        progress = 0f;
        queueAssets();

        /*
         * Setting up a skin for UI widgets.
         * Cannot load from asset manager, because assets were not loaded yet.
         */
        skin = new Skin();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("agency-fb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        parameter.color = Color.WHITE;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        BitmapFont agency = generator.generateFont(parameter);
        skin.add("default-font", agency);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));

        /* Loading label */
        Label loading = new Label("LOADING", skin);
        loading.setPosition(-stage.getWidth() - loading.getWidth(), stage.getHeight() / 2 - 50);
        stage.addActor(loading);

        /* Caution label */
        Label loadingBelow = new Label("\nPLEASE DO NOT CLOSE THE GAME", skin);
        loadingBelow.setPosition(stage.getWidth(), stage.getHeight() / 2 - 140);
        stage.addActor(loadingBelow);
        loadingBelow.setFontScale(0.26f);

        /* Actions for labels */
        loading.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.moveBy(-stage.getWidth(), 0),
                Actions.parallel(
                        Actions.moveTo(stage.getWidth() / 2 - loading.getWidth() / 2, stage.getHeight() / 2 - 50, .5f, Interpolation.pow5),
                        Actions.fadeIn(.5f))));

        loadingBelow.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.moveBy(stage.getWidth(), 0),
                Actions.parallel(
                        Actions.moveTo(stage.getWidth() / 2 - loadingBelow.getWidth() * 0.26f / 2, stage.getHeight() / 2 - 140, .5f, Interpolation.pow5),
                        Actions.fadeIn(.5f)),
                Actions.delay(1f),
                Actions.run(() -> canSwitch = true)));
    }

    @Override
    public void update(float delta) {
        stage.act();
        progress = MathUtils.lerp(progress, Application.assetManager.getProgress(), 0.1f);
        if (Application.assetManager.update() && progress >= 1 - 0.001f && canSwitch) {
            gameStateManager.setState(GameStateManager.State.PLAY, true);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, stage.getWidth(), stage.getHeight(),
                new Color(.6f, .2f, .2f, 1f),
                new Color(1, .1f, .1f, 1f),
                new Color(.8f, .2f, .2f, 1f),
                new Color(.2f, .2f, .2f, 1));
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 0, stage.getWidth() * progress, 8);
        shapeRenderer.end();
        stage.draw();
    }

    private void queueAssets() {
        /* Player animations */
        Application.assetManager.load("animation-assets/player/playerRifleMove.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/player/playerRifleReload.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/player/playerRifleShoot.atlas", TextureAtlas.class);

        Application.assetManager.load("animation-assets/player/playerHandgunMove.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/player/playerHandgunReload.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/player/playerHandgunShoot.atlas", TextureAtlas.class);

        Application.assetManager.load("animation-assets/player/playerMeleeAttack.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/player/playerMeleeMove.atlas", TextureAtlas.class);

        /* Zombie animations */
        Application.assetManager.load("animation-assets/zombie/zombieIdle.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/zombie/zombieMove.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/zombie/zombieAttack.atlas", TextureAtlas.class);

        Application.assetManager.load("animation-assets/zombie/shooterIdle.atlas", TextureAtlas.class);
        Application.assetManager.load("animation-assets/zombie/shooterMove.atlas", TextureAtlas.class);

        /* HUD inventory items */
        Application.assetManager.load("ui/background/background.png", Texture.class);
        Application.assetManager.load("ui/background/background-large.png", Texture.class);
        Application.assetManager.load("ui/buttons/hide-button.png", Texture.class);
        Application.assetManager.load("ui/buttons/show-button.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/selector.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/rifle.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/handgun.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/rifleAmmo.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/handgunAmmo.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/helmet.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/vest.png", Texture.class);
        Application.assetManager.load("ui/inventory-items/medkit.png", Texture.class);

        /* Map rounded inventory items */
        Application.assetManager.load("ui/inventory-items-round/rifle.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/handgun.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/shotgun.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/rifleAmmo.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/handgunAmmo.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/helmet.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/vest.png", Texture.class);
        Application.assetManager.load("ui/inventory-items-round/medkit.png", Texture.class);

        /* Bullet */
        Application.assetManager.load("bullet.png", Texture.class);

        /* PlayState */
        Application.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        Application.assetManager.load("map/map.tmx", TiledMap.class);
        Application.assetManager.load("map/mapDifficult.tmx", TiledMap.class);

        // Fonts
        FileHandleResolver resolver = new InternalFileHandleResolver();
        Application.assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        Application.assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter segoeBlack = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        segoeBlack.fontFileName = "SegoeUI-Black.ttf";
        segoeBlack.fontParameters.size = 30;
        segoeBlack.fontParameters.color = Color.WHITE;
        Application.assetManager.load("SegoeUI-Black.ttf", BitmapFont.class, segoeBlack);

        FreetypeFontLoader.FreeTypeFontLoaderParameter agencyFB = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        agencyFB.fontFileName = "agency-fb.ttf";
        agencyFB.fontParameters.size = 30;
        agencyFB.fontParameters.color = Color.WHITE;
        Application.assetManager.load("agency-fb.ttf", BitmapFont.class, segoeBlack);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * Constants.SCALE)), ((int) (height * Constants.SCALE)));
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        skin.dispose();
    }
}

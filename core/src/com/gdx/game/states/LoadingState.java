package com.gdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.utils.Constants;
import com.gdx.game.utils.Utils;

import java.util.stream.IntStream;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.gdx.game.Application.assetManager;

public class LoadingState extends GameState {
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin skin;
    private float progress;

    public LoadingState(GameStateManager gameStateManager, GameStateManager.State state) {
        super(gameStateManager, state);
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.progress = 0.0f;
        this.skin = Utils.initSkin("agency-fb.ttf", 80);

        queueAssets();
        initButtons();
    }

    private void initButtons() {
        /* Loading label */
        Label loading = new Label("LOADING", skin);
        loading.setPosition(-stage.getWidth() - loading.getWidth(), stage.getHeight() / 2 - 50);

        /* Caution label */
        Label caution = new Label("\nPLEASE DO NOT CLOSE THE GAME", skin);
        caution.setPosition(stage.getWidth(), stage.getHeight() / 2 - 140);
        caution.setFontScale(0.26f);

        stage.addActor(loading);
        stage.addActor(caution);

        /* Popup animations */
        loading.addAction(sequence(
                alpha(0),
                moveBy(-stage.getWidth(), 0),
                parallel(
                        moveTo(stage.getWidth() / 2 - loading.getWidth() / 2,
                                stage.getHeight() / 2 - 50, .5f, Interpolation.pow5),
                        fadeIn(.5f))));

        caution.addAction(sequence(
                alpha(0),
                moveBy(stage.getWidth(), 0),
                parallel(
                        moveTo(stage.getWidth() / 2 - caution.getWidth() * 0.26f / 2,
                                stage.getHeight() / 2 - 140, .5f, Interpolation.pow5),
                        fadeIn(.5f)),
                delay(1f)));
    }

    @Override
    public void update(float delta) {
        progress = MathUtils.lerp(progress, assetManager.getProgress(), 0.1f);
        if (assetManager.update() && progress >= 1 - 0.001f) {
            gameStateManager.setState(GameStateManager.State.PLAY, true);
        }
        stage.act();
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
        /* SoundManager and music */
        assetManager.load("sound/effects/rifleSingleShot.wav", Sound.class);
        assetManager.load("sound/effects/handgunSingleShot.wav", Sound.class);
        assetManager.load("sound/effects/itemPickUp.mp3", Sound.class);
        assetManager.load("sound/effects/weaponReload.mp3", Music.class);
        assetManager.load("sound/music/backgroundMusic.mp3", Music.class);
        IntStream.range(1, 24).forEach(i -> assetManager.load("sound/effects/zombies/zombie-" + i + ".wav", Music.class));
        /* WAV files must have 16 bits per sample: 24 */

        /* Player animations */
        assetManager.load("animation-assets/player/playerRifleMove.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/player/playerRifleReload.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/player/playerRifleShoot.atlas", TextureAtlas.class);

        assetManager.load("animation-assets/player/playerHandgunMove.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/player/playerHandgunReload.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/player/playerHandgunShoot.atlas", TextureAtlas.class);

        assetManager.load("animation-assets/player/playerMeleeAttack.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/player/playerMeleeMove.atlas", TextureAtlas.class);

        /* Zombie animations */
        assetManager.load("animation-assets/zombie/zombieIdle.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/zombie/zombieMove.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/zombie/zombieAttack.atlas", TextureAtlas.class);

        assetManager.load("animation-assets/zombie/shooterIdle.atlas", TextureAtlas.class);
        assetManager.load("animation-assets/zombie/shooterMove.atlas", TextureAtlas.class);

        /* HUD inventory items */
        assetManager.load("ui/background/background.png", Texture.class);
        assetManager.load("ui/background/background-large.png", Texture.class);
        assetManager.load("ui/buttons/hide-button.png", Texture.class);
        assetManager.load("ui/buttons/show-button.png", Texture.class);
        assetManager.load("ui/inventory-items/selector.png", Texture.class);
        assetManager.load("ui/inventory-items/rifle.png", Texture.class);
        assetManager.load("ui/inventory-items/handgun.png", Texture.class);
        assetManager.load("ui/inventory-items/shotgun.png", Texture.class);
        assetManager.load("ui/inventory-items/rifleAmmo.png", Texture.class);
        assetManager.load("ui/inventory-items/handgunAmmo.png", Texture.class);
        assetManager.load("ui/inventory-items/shotgunAmmo.png", Texture.class);
        assetManager.load("ui/inventory-items/helmet.png", Texture.class);
        assetManager.load("ui/inventory-items/vest.png", Texture.class);
        assetManager.load("ui/inventory-items/medkit.png", Texture.class);

        /* Map rounded inventory items */
        assetManager.load("ui/inventory-items-round/rifle.png", Texture.class);
        assetManager.load("ui/inventory-items-round/handgun.png", Texture.class);
        assetManager.load("ui/inventory-items-round/shotgun.png", Texture.class);
        assetManager.load("ui/inventory-items-round/rifleAmmo.png", Texture.class);
        assetManager.load("ui/inventory-items-round/handgunAmmo.png", Texture.class);
        assetManager.load("ui/inventory-items-round/shotgunAmmo.png", Texture.class);
        assetManager.load("ui/inventory-items-round/helmet.png", Texture.class);
        assetManager.load("ui/inventory-items-round/vest.png", Texture.class);
        assetManager.load("ui/inventory-items-round/medkit.png", Texture.class);

        /* Bullet */
        assetManager.load("bullet.png", Texture.class);

        /* PlayState */
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("map/map.tmx", TiledMap.class);
        assetManager.load("map/mapDifficult.tmx", TiledMap.class);

        // Fonts
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter segoeBlack = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        segoeBlack.fontFileName = "SegoeUI-Black.ttf";
        segoeBlack.fontParameters.size = 30;
        segoeBlack.fontParameters.color = Color.WHITE;
        assetManager.load("SegoeUI-Black.ttf", BitmapFont.class, segoeBlack);

        FreetypeFontLoader.FreeTypeFontLoaderParameter agencyFB = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        agencyFB.fontFileName = "agency-fb.ttf";
        agencyFB.fontParameters.size = 30;
        agencyFB.fontParameters.color = Color.WHITE;
        assetManager.load("agency-fb.ttf", BitmapFont.class, agencyFB);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * Constants.SCALE)), ((int) (height * Constants.SCALE)));
    }

    @Override
    public void dispose() {
        skin.dispose();
        shapeRenderer.dispose();
        stage.dispose();
    }
}

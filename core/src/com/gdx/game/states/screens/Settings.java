package com.gdx.game.states.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.states.screens.HudButton.BooleanListener.Type;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.gdx.game.states.screens.HudButton.BooleanListener;
import static com.gdx.game.utils.Constants.CAMERA_LERP;
import static com.gdx.game.utils.Constants.DIFFICULT_GAME;
import static com.gdx.game.utils.Constants.MENU_ON;
import static com.gdx.game.utils.Constants.SCALE;
import static com.gdx.game.utils.Constants.SMOOTH_MOVEMENT;

public class Settings implements Screen {
    private GameStateManager gameStateManager;
    private Application application;
    private ShapeRenderer shapeRenderer;
    private Table verticalTable;
    private boolean disolve = false;
    private Stage stage;
    private Skin skin;

    private HudButton difficultyButton;
    private HudButton trackingButton;
    private HudButton controlButton;
    private HudButton backButton;

    public Settings(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        this.application = gameStateManager.getApplication();
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        resetInputProcessor();


        /* Setting up a new font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("agency-fb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        parameter.color = Color.WHITE;
        BitmapFont segoeFont = generator.generateFont(parameter);

        /* Setting up a skin for UI widgets */
        skin = new Skin();
        skin.addRegions(new TextureAtlas("ui/uiSkin.atlas"));
        skin.add("default-font", segoeFont, BitmapFont.class);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));

        initButtons();
    }

    private void initButtons() {
        Runnable dissolveButtons = () -> {
            disolve = true;
            difficultyButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            trackingButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            controlButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            backButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
        };

        Runnable invDissolveButtons = () -> {
            difficultyButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            trackingButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            controlButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            backButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
        };

        verticalTable = new Table();

        /* Initializing a DIFFICULTY switch button */
        difficultyButton = new HudButton("DIFFICULTY", skin);
        difficultyButton.addBooleanListener(new BooleanListener(Type.DIFFICULT_GAME));
        verticalTable.add(difficultyButton).width(520).height(92);
        verticalTable.row();

        /* Initializing a PLAYER TRACKING switch button */
        trackingButton = new HudButton("TRACKING", skin);
        trackingButton.addBooleanListener(new BooleanListener(Type.CAMERA_LERP));
        verticalTable.add(trackingButton).width(520).height(92).space(20);
        verticalTable.row();

        /* Initializing a PLAYER MOVEMENT CONTROL switch button */
        controlButton = new HudButton("CONTROL", skin);
        controlButton.addBooleanListener(new BooleanListener(Type.SHARP_MOVEMENT));
        verticalTable.add(controlButton).width(520).height(92).space(20);
        verticalTable.row();

        /* Initializing a BACK TO MENU switch button */
        backButton = new HudButton("BACK", skin);
        verticalTable.add(backButton).width(520).height(92);

        /* Setting up a vertical group */
        stage.addActor(verticalTable);
        verticalTable.setPosition(stage.getWidth() / 2, stage.getHeight() * 2);
        verticalTable.addAction(moveTo(stage.getWidth() / 2, stage.getHeight() / 2 + verticalTable.getHeight() / 2, 0.5f, Interpolation.pow5));


        difficultyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DIFFICULT_GAME = !DIFFICULT_GAME;
            }
        });

        trackingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CAMERA_LERP = !CAMERA_LERP;
            }
        });

        controlButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SMOOTH_MOVEMENT = !SMOOTH_MOVEMENT;
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.6f), run(() -> MENU_ON = true), run(invDissolveButtons)));
            }
        });
    }

    @Override
    public void update() {
        stage.act();
        if (!disolve) {
            // TODO: 18.07.2018 change names and selector color
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
        shapeRenderer.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * SCALE)), ((int) (height * SCALE)));
    }

    @Override
    public void dispose() {
        skin.dispose();
        shapeRenderer.dispose();
        stage.dispose();
    }

    @Override
    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

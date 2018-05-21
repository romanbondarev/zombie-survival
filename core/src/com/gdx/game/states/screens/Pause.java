package com.gdx.game.states.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Pause {
    private Application application;
    private Stage stage;
    private Camera camera;
    private ShapeRenderer shapeRenderer;

    private BitmapFont segoeFont;
    private Skin skin;

    private Image play;
    private Image menu;
    private Image quit;
    private VerticalGroup verticalGroup;
    private Label counter;

    private Texture playTex;
    private Texture menuTex;
    private Texture quitTex;

    private int killCounter= 0;


    public Pause(Application application) {
        this.stage = new Stage(new ScreenViewport());
        this.application = application;
        this.camera = application.getCamera();
        this.shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);

        /* Setting up a new font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("agency-fb.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        parameter.color = Color.WHITE;
        segoeFont = generator.generateFont(parameter);

        /* Setting up a skin for UI widgets */
        skin = new Skin();
        skin.add("default-font", segoeFont, BitmapFont.class);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));

        /* Initializing the inventory UI */
        initButtons();
    }

    private void initButtons() {
        Runnable dissolveButtons = () -> {
            play.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            menu.addAction(fadeOut(0.5f, Interpolation.pow5));
            quit.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
        };

        Runnable invDissolveButtons = () -> {
            play.addAction(parallel(moveBy(800, 0), alpha(1)));
            menu.addAction(parallel(alpha(1)));
            quit.addAction(parallel(moveBy(-800, 0), alpha(1)));
        };


        ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget().getName() != null) {
                    event.getTarget().addAction(sequence(alpha(0.5f), alpha(1, 0.02f)));
                }
                if (event.getTarget().getName().equals("PLAY_BUTTON")) {
                    event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> Constants.IN_GAME_PAUSE = !Constants.IN_GAME_PAUSE), run(invDissolveButtons)));
                }

                if (event.getTarget().getName().equals("QUIT_BUTTON")) {
                    event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> {
                        Gdx.app.exit();
                    })));
                }

                if (event.getTarget().getName().equals("MENU_BUTTON")) {
                    event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> {
                        Application.assetManager.clear();
                        ((PlayState) application.getGameStateManager().getCurrentState()).exitState(GameStateManager.State.MENU);
                        Constants.IN_GAME_PAUSE = false;
                    })));
                }
            }
        };

        verticalGroup = new VerticalGroup();
        verticalGroup.space(20);

        /* Play again button */
        playTex = new Texture("ui/buttons/pause-continue.png");
        playTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        play = new Image(playTex);
        play.setName("PLAY_BUTTON");
        play.addListener(clickListener);

        /* Go to the main menu button */
        menuTex = new Texture("ui/buttons/pause-menu.png");
        menuTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        menu = new Image(menuTex);
        menu.setName("MENU_BUTTON");
        menu.addListener(clickListener);

        /* Quit game button */
        quitTex = new Texture("ui/buttons/pause-quit.png");
        quitTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        quit = new Image(quitTex);
        quit.setName("QUIT_BUTTON");
        quit.addListener(clickListener);

        /* Kill counter */
        counter = new Label("", skin);

        /* Container for buttons above */
        verticalGroup.addActor(counter);
        verticalGroup.addActor(play);
        verticalGroup.addActor(menu);
        verticalGroup.addActor(quit);

        stage.addActor(verticalGroup);
        verticalGroup.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 + verticalGroup.getPrefHeight() / 2);
    }

    public void update(int amount) {
        killCounter = amount;
        counter.setText("ZOMBIES KILLED: " + amount);
        stage.act();
    }

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, stage.getWidth(), stage.getHeight(),
                new Color(.6f, .2f, .2f, 1f),
                new Color(1, .1f, .1f, 1f),
                new Color(.8f, .2f, .2f, 1f),
                new Color(.2f, .2f, .2f, 1));
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        segoeFont.dispose();
        playTex.dispose();
        menuTex.dispose();
        quitTex.dispose();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * Constants.SCALE)), ((int) (height * Constants.SCALE)));
    }

    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

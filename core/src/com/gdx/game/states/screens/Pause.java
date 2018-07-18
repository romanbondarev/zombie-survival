package com.gdx.game.states.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.Constants;
import com.gdx.game.utils.Utils;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.gdx.game.states.screens.HudButton.BooleanListener;

public class Pause implements Screen {
    private Application application;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private BitmapFont segoeFont;
    private Skin skin;

    private Table verticalTable;
    private Label counter;
    private HudButton continueButton;
    private HudButton menuButton;
    private HudButton quitButton;

    private int killCounter = 0;


    public Pause(Application application) {
        this.stage = new Stage(new ScreenViewport());
        this.application = application;
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
        skin.addRegions(new TextureAtlas("ui/uiSkin.atlas"));
        skin.add("default-font", segoeFont, BitmapFont.class);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));

        /* Initializing the inventory UI */
        initButtons();
    }

    private void initButtons() {
        Runnable dissolveButtons = () -> {
            continueButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            menuButton.addAction(fadeOut(0.5f, Interpolation.pow5));
            quitButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
        };

        Runnable invDissolveButtons = () -> {
            continueButton.addAction(parallel(moveBy(800, 0), alpha(1)));
            menuButton.addAction(parallel(alpha(1)));
            quitButton.addAction(parallel(moveBy(-800, 0), alpha(1)));
        };

        verticalTable = new Table();

        /* Kill counter */
        counter = new Label("", skin);
        verticalTable.add(counter);
        verticalTable.row();

        /* Play again button */
        continueButton = new HudButton("CONTINUE", skin);
        continueButton.addBooleanListener(new BooleanListener(BooleanListener.Type.DEBUG));
        verticalTable.add(continueButton).width(520).height(92).space(20);
        verticalTable.row();

        /* Go to the main menu button */
        menuButton = new HudButton("MAIN MENU", skin);
        verticalTable.add(menuButton).width(520).height(92).space(20);
        verticalTable.row();

        /* Quit game button */
        quitButton = new HudButton("QUIT GAME", skin);
        verticalTable.add(quitButton).width(520).height(92);


        /* Buttons click listeners */
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> Constants.IN_GAME_PAUSE = !Constants.IN_GAME_PAUSE), run(invDissolveButtons)));
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> {
                    Application.assetManager.clear();
                    ((PlayState) application.getGameStateManager().getCurrentState()).exitState(GameStateManager.State.MENU);
                    Constants.IN_GAME_PAUSE = false;
                })));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> {
                    Utils.saveGameJSON(killCounter);
                    Gdx.app.exit();
                })));
            }
        });

        stage.addActor(verticalTable);
        verticalTable.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 + verticalTable.getWidth() / 2);
    }

    @Deprecated
    @Override
    public void update() {
        update(0);
    }

    public void update(int amount) {
        resetInputProcessor();
        killCounter = amount;
        counter.setText("ZOMBIES KILLED: " + amount);
        stage.act();
    }

    @Override
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


    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
        segoeFont.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * Constants.SCALE)), ((int) (height * Constants.SCALE)));
    }

    @Override
    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

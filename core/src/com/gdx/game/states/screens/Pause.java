package com.gdx.game.states.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.gdx.game.components.SelectorButton;
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

public final class Pause implements Screen {
    private Application application;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private Skin skin;

    private Label counter;
    private SelectorButton continueButton;
    private SelectorButton menuButton;
    private SelectorButton quitButton;
    private int killCounter;

    public Pause(Application application) {
        this.stage = new Stage(new ScreenViewport());
        this.application = application;
        this.shapeRenderer = new ShapeRenderer();
        this.killCounter = 0;
        this.skin = Utils.initSkin("agency-fb.ttf", 52);

        initButtons();
        resetInputProcessor();
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

        Table verticalTable = new Table();

        /* Kill counter */
        counter = new Label("", skin);
        verticalTable.add(counter);
        verticalTable.row();

        /* Play again button */
        continueButton = new SelectorButton("CONTINUE", skin);
        verticalTable.add(continueButton).width(520).height(92).space(20);
        verticalTable.row();

        /* Go to the main menu button */
        menuButton = new SelectorButton("MAIN MENU", skin);
        verticalTable.add(menuButton).width(520).height(92).space(20);
        verticalTable.row();

        /* Quit game button */
        quitButton = new SelectorButton("QUIT GAME", skin);
        verticalTable.add(quitButton).width(520).height(92);


        /*
         * Buttons click listeners
         */

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

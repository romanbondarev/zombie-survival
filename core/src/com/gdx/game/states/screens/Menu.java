package com.gdx.game.states.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.components.SelectorButton;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.utils.Constants;
import com.gdx.game.utils.Utils;
import org.json.simple.JSONObject;

import java.util.Optional;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Menu implements Screen {
    private GameStateManager gameStateManager;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private Skin skin;

    private float height;
    private float width;

    private SelectorButton playButton;
    private SelectorButton settingsButton;
    private SelectorButton quitButton;
    private SelectorButton howToButton;

    private boolean loadingAnimation = false;

    public Menu(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.skin = Utils.initSkin("agency-fb.ttf", 52);

        initButtons();
        resetInputProcessor();

        this.width = stage.getWidth();
        this.height = stage.getHeight();

        Optional<JSONObject> topScore = Utils.getTopScoreJSON();
        topScore.ifPresent(jsonObject -> System.out.println(
                "CURRENT TOP SCORE\n"
                        + "Killed zombies amount = " + jsonObject.get("zombies-killed") + "\n"
                        + jsonObject.get("date")
        ));
    }

    private void initButtons() {
        // TODO: 18.07.2018 review actions, animation start at different time
        Runnable dissolveButtons = () -> {
            howToButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            playButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            settingsButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            quitButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
        };

        Runnable invDissolveButtons = () -> {
            howToButton.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), alpha(1)));
            playButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), alpha(1)));
            settingsButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), alpha(1)));
            quitButton.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), alpha(1)));
        };

        Runnable quitApplication = () -> Gdx.app.exit();


        Table horizontalTable = new Table();
        Table verticalTable = new Table();

        /* Play button */
        playButton = new SelectorButton("PLAY", skin);
        verticalTable.add(playButton).width(300).height(92);
        verticalTable.row();

        /* Settings button */
        settingsButton = new SelectorButton("SETTINGS", skin);
        verticalTable.add(settingsButton).width(300).height(92).space(20);
        verticalTable.row();

        /* Quit game button */
        quitButton = new SelectorButton("QUIT", skin);
        verticalTable.add(quitButton).width(300).height(92);

        /* Basic rules and tips button */
        howToButton = new SelectorButton("HOW TO PLAY", skin);
        horizontalTable.add(verticalTable);
        horizontalTable.add(howToButton).width(505).height(316).spaceLeft(20);

        /* Setting up the table's position and popup animation */
        stage.addActor(horizontalTable);
        horizontalTable.setPosition(stage.getWidth() / 2 - horizontalTable.getWidth() / 2, stage.getHeight() / 2);
        horizontalTable.addAction(sequence(alpha(0), delay(0.2f), run(() -> loadingAnimation = true), delay(1f), fadeIn(2, Interpolation.pow5Out)));


        /*
         * Buttons click listeners
         */

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> gameStateManager.setState(GameStateManager.State.LOADING, true))));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> Constants.MENU_ON = false), run(invDissolveButtons)));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getTarget().addAction(sequence(run(dissolveButtons), delay(2f), run(quitApplication)));
            }
        });
    }

    @Override
    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameStateManager.setState(GameStateManager.State.LOADING, true);
        }

        if (loadingAnimation) {
            if (height > 4) height = MathUtils.lerp(height, 4, 0.2f);
            if (height <= 5) width = MathUtils.lerp(width, 0, 0.2f);
        }

        stage.act();
    }

    @Override
    public void render() {
        /* Screen background */
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, stage.getWidth(), stage.getHeight(),
                new Color(.6f, .2f, .2f, 1f),
                new Color(1, .1f, .1f, 1f),
                new Color(.8f, .2f, .2f, 1f),
                new Color(.2f, .2f, .2f, 1));
        shapeRenderer.end();

        stage.draw();

        /* Game start up animation */
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(stage.getWidth() / 2 - width / 2, stage.getHeight() / 2 - height / 2, width, height);
        shapeRenderer.end();
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

    @Override
    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

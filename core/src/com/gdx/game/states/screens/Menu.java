package com.gdx.game.states.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
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
    private Application application;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private float height;
    private float width;
    private HudButton playButton;
    private HudButton settingsButton;
    private HudButton quitButton;
    private HudButton howToButton;
    private Skin skin;
    private boolean load = false;


    public Menu(GameStateManager gameStateManager) {
        this.application = gameStateManager.getApplication();
        this.gameStateManager = gameStateManager;
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

        width = stage.getWidth();
        height = stage.getHeight();

        initButtons();

        Optional<JSONObject> topScore = Utils.getTopScoreJSON();
        topScore.ifPresent(jsonObject -> System.out.println(
                "CURRENT TOP SCORE\n" +
                        "Killed zombies amount = " + jsonObject.get("zombies-killed") + "\n"
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
        playButton = new HudButton("PLAY", skin);
        verticalTable.add(playButton).width(300).height(92);
        verticalTable.row();

        /* Settings button */
        settingsButton = new HudButton("SETTINGS", skin);
        verticalTable.add(settingsButton).width(300).height(92).space(20);
        verticalTable.row();

        /* Quit game button */
        quitButton = new HudButton("QUIT", skin);
        verticalTable.add(quitButton).width(300).height(92);

        /* Basic rules and tips button */
        howToButton = new HudButton("HOW TO", skin);
        horizontalTable.add(verticalTable);
        horizontalTable.add(howToButton).width(505).height(316).spaceLeft(20);

        stage.addActor(horizontalTable);
        horizontalTable.setPosition(stage.getWidth() / 2 - horizontalTable.getWidth() / 2, stage.getHeight() / 2);
        horizontalTable.addAction(sequence(alpha(0), delay(0.2f), run(() -> load = true), delay(1f), fadeIn(2, Interpolation.pow5Out)));


        /* Buttons click listeners*/
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
        if (load) {
            if (height > 4) height = MathUtils.lerp(height, 4, 0.2f);
            if (height <= 5) width = MathUtils.lerp(width, 0, 0.2f);
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
        shapeRenderer.end();
        stage.draw();

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

package com.gdx.game.states.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
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

public class Menu {
    private GameStateManager gameStateManager;
    private Application application;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private Image play;
    private Image settings;
    private Image quit;
    private Image howTo;
    private float height;
    private float width;
    private final Texture quitTex;
    private final Texture playTex;
    private final Texture howToTex;
    private final Texture settingsTex;
    private boolean load = false;


    public Menu(GameStateManager gameStateManager) {
        this.application = gameStateManager.getApplication();
        this.gameStateManager = gameStateManager;
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        resetInputProcessor();

        Runnable dissolveButtons = () -> {
            howTo.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            play.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            settings.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            quit.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
        };
        Runnable invDissolveButtons = () -> {
            howTo.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), alpha(1)));
            play.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), alpha(1)));
            settings.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), alpha(1)));
            quit.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), alpha(1)));
        };

        Runnable quitApplication = () -> Gdx.app.exit();

        ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget().getName() != null) {
                    event.getTarget().addAction(sequence(alpha(0.5f), alpha(1, 0.02f)));
                    if (event.getTarget().getName().equals("QUIT_BUTTON"))
                        event.getTarget().addAction(sequence(run(dissolveButtons), delay(2f), run(quitApplication)));
                    if (event.getTarget().getName().equals("SETTINGS_BUTTON"))
                        event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> Constants.MENU_ON = false), run(invDissolveButtons)));
                    if (event.getTarget().getName().equals("PLAY_BUTTON"))
                        event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.5f), run(() -> gameStateManager.setState(GameStateManager.State.LOADING, true))));
                }
            }
        };

        width = stage.getWidth();
        height = stage.getHeight();

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.space(40);

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.space(20);

        /* Play button */
        playTex = new Texture("ui/buttons/menu-play.png");
        playTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        play = new Image(playTex);
        play.setName("PLAY_BUTTON");
        stage.addListener(clickListener);
        verticalGroup.addActor(play);

        /* Settings button */
        settingsTex = new Texture("ui/buttons/menu-settings.png");
        settingsTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        settings = new Image(settingsTex);
        settings.setName("SETTINGS_BUTTON");
        stage.addListener(clickListener);
        verticalGroup.addActor(settings);

        /* Quit game button */
        quitTex = new Texture("ui/buttons/menu-quit.png");
        quitTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        quit = new Image(quitTex);
        quit.setName("QUIT_BUTTON");
        stage.addListener(clickListener);
        verticalGroup.addActor(quit);

        /* Basic rules and tips button */
        howToTex = new Texture("ui/buttons/menu-howto.png");
        howToTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        howTo = new Image(howToTex);

        horizontalGroup.addActor(verticalGroup);
        horizontalGroup.addActor(howTo);
        stage.addActor(horizontalGroup);
        horizontalGroup.setPosition(stage.getWidth() / 2 - horizontalGroup.getPrefWidth() / 2, stage.getHeight() / 2);
        horizontalGroup.addAction(sequence(alpha(0), delay(0.2f), run(() -> load = true), delay(1f), fadeIn(2, Interpolation.pow5Out)));


        Optional<JSONObject> topScore = Utils.getTopScoreJSON();
        topScore.ifPresent(jsonObject -> System.out.println(
                "CURRENT TOP SCORE\n" +
                        "Killed zombies amount = " + jsonObject.get("zombies-killed") + "\n"
                        + jsonObject.get("date")
        ));
    }

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


    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * Constants.SCALE)), ((int) (height * Constants.SCALE)));
    }


    public void dispose() {
        shapeRenderer.dispose();
        playTex.dispose();
        howToTex.dispose();
        quitTex.dispose();
        settingsTex.dispose();
        stage.dispose();
    }

    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

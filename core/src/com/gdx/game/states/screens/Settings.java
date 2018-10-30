package com.gdx.game.states.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.managers.GameStateManager;
import com.gdx.game.utils.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.gdx.game.utils.Constants.CAMERA_LERP;
import static com.gdx.game.utils.Constants.DEBUG;
import static com.gdx.game.utils.Constants.DIFFICULT_GAME;
import static com.gdx.game.utils.Constants.MENU_ON;
import static com.gdx.game.utils.Constants.SCALE;
import static com.gdx.game.utils.Constants.SHARP_MOVEMENT;

public class Settings {
    private GameStateManager gameStateManager;
    private Application application;
    private ShapeRenderer shapeRenderer;
    private VerticalGroup verticalGroup;
    private boolean disolve = false;
    private Stage stage;
    private Image selector1;
    private Image selector2;
    private Image selector3;
    private Image difficulty;
    private Image tracking;
    private Image control;
    private Image back;
    private final Texture difHardTex;
    private final Texture difEasyTex;
    private final Texture settingsTex;
    private final Texture quitTex;
    private final Texture backTex;
    private final Texture selectorTex;

    public Settings(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        this.application = gameStateManager.getApplication();
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        resetInputProcessor();


        Runnable dissolveButtons = () -> {
            disolve = true;
            difficulty.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            tracking.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            control.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            back.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), fadeOut(0.5f)));
            selector1.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), alpha(0)));
            selector2.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), alpha(0)));
            selector3.addAction(parallel(moveBy(-800, 0, 0.5f, Interpolation.pow2), alpha(0)));
        };

        Runnable invDissolveButtons = () -> {
            difficulty.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            tracking.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            control.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            back.addAction(parallel(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.5f)));
            selector1.addAction(sequence(moveBy(800, 0, 0.5f, Interpolation.pow2), run(() -> disolve = false), fadeIn(0.1f)));
            selector2.addAction(sequence(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.1f)));
            selector3.addAction(sequence(moveBy(800, 0, 0.5f, Interpolation.pow2), fadeIn(0.1f)));
        };

        ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget().getName() != null) {
                    event.getTarget().addAction(sequence(alpha(0.5f), alpha(1, 0.02f)));
                }
                if (event.getTarget().getName() != null && event.getTarget().getName().equals("DIFFICULTY")) {
                    DIFFICULT_GAME = !DIFFICULT_GAME;
                }
                if (event.getTarget().getName() != null && event.getTarget().getName().equals("DEBUG")) {
                    DEBUG = !DEBUG;
                }
                if (event.getTarget().getName() != null && event.getTarget().getName().equals("TRACKING")) {
                    CAMERA_LERP = !CAMERA_LERP;
                }
                if (event.getTarget().getName() != null && event.getTarget().getName().equals("CONTROL")) {
                    SHARP_MOVEMENT = !SHARP_MOVEMENT;
                }
                if (event.getTarget().getName() != null && event.getTarget().getName().equals("BACK")) {
                    event.getTarget().addAction(sequence(run(dissolveButtons), delay(0.6f), run(() -> MENU_ON = true), run(invDissolveButtons)));
                }
            }
        };

        verticalGroup = new VerticalGroup();
        verticalGroup.space(20);

        /* Initializing a DIFFICULTY switch button */
        difHardTex = new Texture("ui/buttons/settings-difHard.png");
        difEasyTex = new Texture("ui/buttons/settings-difEasy.png");
        difEasyTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        difficulty = new Image(difEasyTex);
        difficulty.setName("DIFFICULTY");
        stage.addListener(clickListener);
        verticalGroup.addActor(difficulty);

        /* Initializing a PLAYER TRACKING switch button */
        settingsTex = new Texture("ui/buttons/settings-tracking.png");
        settingsTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        tracking = new Image(settingsTex);
        tracking.setName("TRACKING");
        stage.addListener(clickListener);
        verticalGroup.addActor(tracking);

        /* Initializing a PLAYER MOVEMENT CONTROL switch button */
        quitTex = new Texture("ui/buttons/settings-control.png");
        quitTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        control = new Image(quitTex);
        control.setName("CONTROL");
        stage.addListener(clickListener);
        verticalGroup.addActor(control);

        /* Initializing a BACK TO MENU switch button */
        backTex = new Texture("ui/buttons/settings-back.png");
        backTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        back = new Image(backTex);
        back.setName("BACK");
        stage.addListener(clickListener);
        verticalGroup.addActor(back);

        /* Setting up a vertical group */
        stage.addActor(verticalGroup);
        verticalGroup.setPosition(stage.getWidth() / 2, stage.getHeight() + verticalGroup.getPrefHeight());
        verticalGroup.addAction(moveTo(stage.getWidth() / 2, stage.getHeight() / 2 + verticalGroup.getPrefHeight() / 2, 0.5f, Interpolation.pow5));


        selectorTex = new Texture("ui/buttons/settings-selector.png");
        selectorTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        selector1 = new Image(selectorTex);
        selector1.setPosition(stage.getWidth() / 2 + verticalGroup.getPrefWidth() / 2 - 11, stage.getHeight());
        selector1.addAction(Actions.sequence(alpha(0),
                parallel(
                        moveTo(stage.getWidth() / 2 + verticalGroup.getPrefWidth() / 2 - 11, stage.getHeight() / 2 + 122, 0.5f, Interpolation.pow5),
                        fadeIn(0.5f))));
        stage.addActor(selector1);


        selector2 = new Image(selectorTex);
        selector2.setPosition(stage.getWidth() / 2 + verticalGroup.getPrefWidth() / 2 - 11, stage.getHeight());
        selector2.addAction(Actions.sequence(alpha(0),
                parallel(
                        moveTo(stage.getWidth() / 2 + verticalGroup.getPrefWidth() / 2 - 11, stage.getHeight() / 2 + 10, 0.5f, Interpolation.pow5),
                        fadeIn(0.5f))));
        stage.addActor(selector2);


        selector3 = new Image(selectorTex);
        selector3.setPosition(stage.getWidth() / 2 + verticalGroup.getPrefWidth() / 2 - 11, stage.getHeight());
        selector3.addAction(Actions.sequence(alpha(0),
                parallel(
                        moveTo(stage.getWidth() / 2 + verticalGroup.getPrefWidth() / 2 - 11, stage.getHeight() / 2 - 102, 0.5f, Interpolation.pow5),
                        fadeIn(0.5f))));
        stage.addActor(selector3);
    }

    public void update() {
        stage.act();
        if (!disolve) {
            difficulty.setDrawable(DIFFICULT_GAME ? new SpriteDrawable(new Sprite(difHardTex))
                    : new SpriteDrawable(new Sprite(difEasyTex)));
            selector1.addAction(DIFFICULT_GAME ? alpha(1) : alpha(0));
            selector2.addAction(CAMERA_LERP ? alpha(1) : alpha(0));
            selector3.addAction(Constants.SHARP_MOVEMENT ? alpha(0) : alpha(1));
        }
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
    }

    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * SCALE)), ((int) (height * SCALE)));
    }

    public void dispose() {
        shapeRenderer.dispose();
        settingsTex.dispose();
        difEasyTex.dispose();
        difHardTex.dispose();
        quitTex.dispose();
        backTex.dispose();
        selectorTex.dispose();
        stage.dispose();
    }

    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

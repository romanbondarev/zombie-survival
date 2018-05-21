package com.gdx.game.states;

import com.gdx.game.managers.GameStateManager;
import com.gdx.game.states.screens.Menu;
import com.gdx.game.states.screens.Settings;
import com.gdx.game.utils.Constants;

public class MenuState extends GameState {
    private Menu menu;
    private Settings settings;

    public MenuState(GameStateManager gameStateManager, GameStateManager.State state) {
        super(gameStateManager, state);
        menu = new Menu(gameStateManager);
        settings = new Settings(gameStateManager);
    }

    @Override
    public void update(float delta) {
        if (Constants.MENU_ON) {
            menu.update();
            menu.resetInputProcessor();
        } else {
            settings.update();
            settings.resetInputProcessor();
        }
    }

    @Override
    public void render() {
        if (Constants.MENU_ON) menu.render();
        else settings.render();
    }

    @Override
    public void resize(int width, int height) {
        if (Constants.MENU_ON) menu.resize(width, height);
        else settings.resize(width, height);
    }

    @Override
    public void dispose() {
        menu.dispose();
        settings.dispose();
    }
}

package com.gdx.game.states;

import com.gdx.game.managers.GameStateManager;
import com.gdx.game.states.screens.Menu;
import com.gdx.game.states.screens.Settings;
import com.gdx.game.utils.Constants;

public class MenuState extends GameState {
    private Menu menuTab;
    private Settings settingsTab;

    public MenuState(GameStateManager gameStateManager, GameStateManager.State state) {
        super(gameStateManager, state);
        menuTab = new Menu(gameStateManager);
        settingsTab = new Settings(gameStateManager);
    }

    @Override
    public void update(float delta) {
        if (Constants.MENU_ON) {
            menuTab.update();
            menuTab.resetInputProcessor();
        } else {
            settingsTab.update();
            settingsTab.resetInputProcessor();
        }
    }

    @Override
    public void render() {
        if (Constants.MENU_ON) {
            menuTab.render();
        } else {
            settingsTab.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (Constants.MENU_ON) {
            menuTab.resize(width, height);
        } else {
            settingsTab.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        menuTab.dispose();
        settingsTab.dispose();
    }
}

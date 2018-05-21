package com.gdx.game.managers;

import com.gdx.game.Application;
import com.gdx.game.states.GameState;
import com.gdx.game.states.LoadingState;
import com.gdx.game.states.MenuState;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.Constants;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class GameStateManager {
    private Application application;
    private HashMap<State, GameState> states = new LinkedHashMap<>();
    private GameState currentState;

    public enum State {
        PLAY, LOADING, MENU
    }

    public GameStateManager(Application application) {
        this.application = application;
        this.setState(Constants.DEFAULT_STATE, true);
    }

    public Application getApplication() {
        return application;
    }

    public void update(float delta) {
        currentState.update(delta);
    }

    public void render() {
        currentState.render();
    }

    public void dispose() {
        for (GameState gameState : states.values()) {
            gameState.dispose();
        }
    }

    public void resize(int width, int height) {
        currentState.resize(width, height);
    }

    public void setState(State state, boolean disposeCurrent) {
        GameState current = currentState;

        if (states.containsKey(state)) {
            currentState = states.get(state);
        } else {
            currentState = getState(state);
            states.put(state, currentState);
        }

        if (current != null && disposeCurrent) {
            current.dispose();
            states.remove(current.getState());
        }
    }

    private GameState getState(State state) {
        switch (state) {
            case MENU:
                return new MenuState(this, state);
            case LOADING:
                return new LoadingState(this, state);
            case PLAY:
                return new PlayState(this, state);
        }
        return null;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}

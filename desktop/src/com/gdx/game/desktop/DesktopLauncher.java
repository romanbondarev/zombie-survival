package com.gdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.game.Application;
import com.gdx.game.utils.Constants;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Constants.WIDTH;
        config.height = Constants.HEIGHT;
        config.backgroundFPS = 60;
        config.foregroundFPS = 60;
        config.title = "Zombie Survival";
        config.resizable = false;
        new LwjglApplication(new Application(), config);
    }
}

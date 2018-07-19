package com.gdx.game.utils;

import com.gdx.game.managers.GameStateManager;

public final class Constants {
    public static boolean DEAD = false;

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int INVENTORY_SIZE = 6;
    public static final int TRAY_SIZE = 4;
    public static final int ITEM_RESPAWN_TIME = 15;
    public static final int BULLET_SPEED = 20;

    public static int shooterDamage = 3;
    public static int zombieDamage = 5;
    public static int meleeDamage = 20;
    public static int handgunDamage = 20;
    public static int shotgunDamage = 50;
    public static int rifleDamage = 100; // 35

    public static final float PPM = 64; // PIXELS PER METER
    public static final float GRAVITY = 0f;
    public static final float SCALE = 1.25f;

    public static boolean DEBUG = true;
    public static boolean DIFFICULT_GAME = false;
    public static boolean SMOOTH_MOVEMENT = true;
    public static boolean CAMERA_LERP = true;
    public static boolean IN_GAME_PAUSE = false;
    public static boolean MENU_ON = true;

    public static GameStateManager.State DEFAULT_STATE = GameStateManager.State.MENU;
}

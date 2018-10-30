package com.gdx.game.utils;

import com.gdx.game.managers.GameStateManager;

public final class Constants {
    public static boolean DEAD = false;

    public static final int WIDTH = 1280; // App width
    public static final int HEIGHT = 720; // App height
    public static final int INVENTORY_SIZE = 6; // Amount of slots for items in inventory (pref. < 10)
    public static final int ITEM_RESPAWN_TIME = 15; // Zombie amount after which items are respawned
    public static final int BULLET_SPEED = 20; // Speed of the bullet

    public static int shooterDamage = 3;
    public static int zombieDamage = 5;
    public static int meleeDamage = 20;
    public static int handgunDamage = 20;
    public static int rifleDamage = 35;

    public static final float PPM = 64; // Pixels per meter
    public static final float GRAVITY = 0f;
    public static final float SCALE = 1.25f;

    public static boolean DEBUG = false;
    public static boolean DIFFICULT_GAME = false;
    public static boolean SHARP_MOVEMENT = false;
    public static boolean CAMERA_LERP = true;
    public static boolean IN_GAME_PAUSE = false;
    public static boolean MENU_ON = true;

    public static GameStateManager.State DEFAULT_STATE = GameStateManager.State.MENU;
}

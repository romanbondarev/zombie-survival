package com.gdx.game.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.gdx.game.Application;

public class Sounds {

    public static Sound rifleSingleShot() {
        return Application.assetManager.get("sound/effects/rifleSingleShot.wav", Sound.class);
    }

    public static Sound handgunSingleShot() {
        return Application.assetManager.get("sound/effects/handgunSingleShot.wav", Sound.class);
    }

    public static Sound itemPickUp() {
        return Application.assetManager.get("sound/effects/itemPickUp.mp3", Sound.class);
    }

    public static Music weaponReload() {
        return Application.assetManager.get("sound/effects/weaponReload.mp3", Music.class);
    }

    public static Music backgroundMusic() {
        return Application.assetManager.get("sound/music/backgroundMusic.mp3", Music.class);
    }
}

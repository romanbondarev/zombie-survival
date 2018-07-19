package com.gdx.game.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.gdx.game.Application;

import java.util.Random;

public class SoundManager {

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

    // TODO: 09.07.2018 fix zombie sounds
    public static Music randomZombieSound() {
        // return zombieSound(1 + new Random().nextInt(23));
        return zombieSound(18 + new Random().nextInt(5));
    }

    public static Music zombieSound(int nr) {
        return nr < 24 ? Application.assetManager.get("sound/effects/zombies/zombie-" + nr + ".wav", Music.class) : null;
    }

    public static Music zombieDeath() {
        return zombieSound(13);
    }

    public static Music zombieDamage() {
        return zombieSound(7 + new Random().nextInt(1));
    }
}

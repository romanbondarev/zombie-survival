package com.gdx.game.models.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.Application;
import com.gdx.game.managers.SoundManager;
import com.gdx.game.items.Item;
import com.gdx.game.items.weapons.Weapon;
import com.gdx.game.models.Player;

import static com.gdx.game.utils.WCC.worldToPixels;

public class PlayerAnimation {
    public enum AnimationType {
        MOVE, RELOAD, SHOOT,
        RIFLE_MOVE, RIFLE_RELOAD, RIFLE_SHOOT,
        HANDGUN_MOVE, HANDGUN_RELOAD, HANDGUN_SHOOT,
        MELEE_ATTACK, MELEE_MOVE
    }

    private Player player;

    private AnimationType currentAnimation;
    private Animation<TextureAtlas.AtlasRegion> animation;

    private TextureAtlas textureAtlasMeleeAttack;
    private TextureAtlas textureAtlasMeleeMove;

    private TextureAtlas textureAtlasRifleMove;
    private TextureAtlas textureAtlasRifleReload;
    private TextureAtlas textureAtlasRifleShoot;

    private TextureAtlas textureAtlasHandgunMove;
    private TextureAtlas textureAtlasHandgunReload;
    private TextureAtlas textureAtlasHandgunShoot;

    private float elapsedTime = 0f;
    private float shootTime = 0f;
    private float reloadTime = 0f;

    private boolean isShooting = false;
    private boolean isReloading = false;

    private boolean didShoot = true;
    private boolean didReload = false;
    private boolean didKnife = false;
    private double degrees = 0;

    /**
     * Creates an animation for the player.
     */
    public PlayerAnimation(Player player) {
        this.player = player;
        textureAtlasMeleeAttack = Application.assetManager.get("animation-assets/player/playerMeleeAttack.atlas", TextureAtlas.class);
        textureAtlasMeleeMove = Application.assetManager.get("animation-assets/player/playerMeleeMove.atlas", TextureAtlas.class);

        textureAtlasRifleMove = Application.assetManager.get("animation-assets/player/playerRifleMove.atlas", TextureAtlas.class);
        textureAtlasRifleReload = Application.assetManager.get("animation-assets/player/playerRifleReload.atlas", TextureAtlas.class);
        textureAtlasRifleShoot = Application.assetManager.get("animation-assets/player/playerRifleShoot.atlas", TextureAtlas.class);

        textureAtlasHandgunMove = Application.assetManager.get("animation-assets/player/playerHandgunMove.atlas", TextureAtlas.class);
        textureAtlasHandgunReload = Application.assetManager.get("animation-assets/player/playerHandgunReload.atlas", TextureAtlas.class);
        textureAtlasHandgunShoot = Application.assetManager.get("animation-assets/player/playerHandgunShoot.atlas", TextureAtlas.class);
        setAnimation(AnimationType.RIFLE_MOVE);
    }

    /**
     * Renders the animation.
     *
     * @param batch sprite batch of the game
     */
    public void renderAnimation(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        TextureAtlas.AtlasRegion keyframe = animation.getKeyFrame(elapsedTime, true);

        /* Keyframe change */
        batch.draw(keyframe,
                worldToPixels(player.getPosition().x) - (keyframe.getRegionWidth() / 2),
                worldToPixels(player.getPosition().y) - (keyframe.getRegionHeight() / 2),
                keyframe.getRegionWidth() / 2,
                keyframe.getRegionHeight() / 2,
                keyframe.getRegionWidth(),
                keyframe.getRegionHeight(),
                1, 1, ((float) degrees));
    }

    /**
     * Updates the animation.
     * Changes animations if necessary.
     *
     * @param degrees
     */
    public void updateAnimation(double degrees) {
        if (currentAnimation.equals(AnimationType.RIFLE_RELOAD)) {
            /* RIFLE_RELOAD ANIMATION */
            Music sound = SoundManager.weaponReload();
            if (!sound.isPlaying()) sound.play();
            reloadTime += 0.1f;
            isReloading = true;
            if (reloadTime > 4f) {
                didReload = true;
                isReloading = false;
                setAnimation(AnimationType.RIFLE_MOVE);
                counterToZero();
            }
        } else if (currentAnimation.equals(AnimationType.HANDGUN_RELOAD)) {
            /* HANDGUN_RELOAD ANIMATION */
            Music sound = SoundManager.weaponReload();
            if (!sound.isPlaying()) sound.play();
            reloadTime += 0.1f;
            isReloading = true;
            if (reloadTime > 3f) {
                didReload = true;
                isReloading = false;
                setAnimation(AnimationType.HANDGUN_MOVE);
                counterToZero();
            }
        } else isReloading = false;

        if (currentAnimation.equals(AnimationType.MELEE_ATTACK)) {
            /* MELEE_ATTACK ANIMATION */
            shootTime += 0.1f;
            isShooting = true;
            if (shootTime > 3f) {
                didKnife = true;
                isShooting = false;
                setAnimation(AnimationType.MELEE_MOVE);
                counterToZero();
            }
        } else if (currentAnimation.equals(AnimationType.RIFLE_SHOOT)) {
            /* RIFLE_SHOOT ANIMATION */
            shootTime += 0.1f;
            isShooting = true;
            if (shootTime > 0.5f) {
                SoundManager.rifleSingleShot().play();
                didShoot = true;
                isShooting = false;
                setAnimation(AnimationType.RIFLE_MOVE);
                counterToZero();
            }
        } else if (currentAnimation.equals(AnimationType.HANDGUN_SHOOT)) {
            /* HANDGUN_SHOOT ANIMATION */
            shootTime += 0.1f;
            isShooting = true;
            if (shootTime > 1f) {
                SoundManager.handgunSingleShot().play();
                didShoot = true;
                isShooting = false;
                setAnimation(AnimationType.HANDGUN_MOVE);
                counterToZero();
            }
        } else isShooting = false;
        this.degrees = degrees;
    }

    /**
     * Sets the current animation.
     */
    public void setAnimation(AnimationType type) {
        float duration = 1f / 30f;
        Array<TextureAtlas.AtlasRegion> regions = null;
        AnimationType preCalc = currentAnimation;
        switch (type) {
            /*
             -- RELOADS --
            */
            case RIFLE_RELOAD:
                /* RIFLE_RELOAD animation */
                duration = 1f / 30f;
                regions = textureAtlasRifleReload.getRegions();
                preCalc = AnimationType.RIFLE_RELOAD;
                break;
            case HANDGUN_RELOAD:
                /* HANDGUN_RELOAD animation */
                duration = 1f / 30f;
                regions = textureAtlasHandgunReload.getRegions();
                preCalc = AnimationType.HANDGUN_RELOAD;
                break;

            /*
             -- SHOOTS --
            */
            case MELEE_ATTACK:
                duration = 1f / 25f;
                regions = textureAtlasMeleeAttack.getRegions();
                preCalc = AnimationType.MELEE_ATTACK;
                break;
            case RIFLE_SHOOT:
                /* RIFLE_SHOOT animation */
                duration = 1f / 15f;
                regions = textureAtlasRifleShoot.getRegions();
                preCalc = AnimationType.RIFLE_SHOOT;
                break;
            case HANDGUN_SHOOT:
                /* HANDGUN_SHOOT animation */
                duration = 1f / 15f;
                regions = textureAtlasHandgunShoot.getRegions();
                preCalc = AnimationType.HANDGUN_SHOOT;
                break;

            /*
             -- MOVES --
            */
            case RIFLE_MOVE:
                if (!isReloading() && !isShooting()) {
                    duration = 1f / 20f;
                    regions = textureAtlasRifleMove.getRegions();
                    preCalc = AnimationType.RIFLE_MOVE;
                }
                break;
            case HANDGUN_MOVE:
                if (!isReloading() && !isShooting()) {
                    duration = 1f / 20f;
                    regions = textureAtlasHandgunMove.getRegions();
                    preCalc = AnimationType.HANDGUN_MOVE;
                }
                break;
            case MELEE_MOVE:
                if (!isReloading() && !isShooting()) {
                    duration = 1f / 30f;
                    regions = textureAtlasMeleeMove.getRegions();
                    preCalc = AnimationType.MELEE_MOVE;
                }
                break;
        }
        if (!preCalc.equals(currentAnimation)) {
            counterToZero();
            animation = new Animation<TextureAtlas.AtlasRegion>(duration, regions);
            currentAnimation = preCalc;
        }
    }

    /**
     * Chooses an animation accordingly to an item and action.
     */
    public AnimationType chooseAnimation(Item item, AnimationType event) {
        // TODO: 13.07.2018 add shotgun animation option
        if (item != null) {
            if (event.equals(AnimationType.SHOOT)) {
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.RIFLE)) {
                    return AnimationType.RIFLE_SHOOT;
                }
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.HANDGUN)) {
                    return AnimationType.HANDGUN_SHOOT;
                }
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.SHOTGUN)) {
                    return AnimationType.HANDGUN_SHOOT;
                }
            }
            if (event.equals(AnimationType.RELOAD)) {
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.RIFLE)) {
                    return AnimationType.RIFLE_RELOAD;
                }
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.HANDGUN)) {
                    return AnimationType.HANDGUN_RELOAD;
                }
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.SHOTGUN)) {
                    return AnimationType.HANDGUN_RELOAD;
                }
            }
            if (event.equals(AnimationType.MOVE)) {
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.RIFLE)) {
                    return AnimationType.RIFLE_MOVE;
                }
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.HANDGUN)) {
                    return AnimationType.HANDGUN_MOVE;
                }
                if (((Weapon) item).getWeaponType().equals(Weapon.WeaponType.SHOTGUN)) {
                    return AnimationType.HANDGUN_MOVE;
                }
            }
        }
        return AnimationType.MELEE_MOVE;
    }

    /**
     * Resets all counters to zero.
     */
    private void counterToZero() {
        shootTime = 0f;
        reloadTime = 0f;
        elapsedTime = 0f;
    }

    /**
     * Disposes loaded animation atlases.
     */
    public void dispose() {
        textureAtlasMeleeAttack.dispose();
        textureAtlasMeleeMove.dispose();

        textureAtlasRifleMove.dispose();
        textureAtlasRifleReload.dispose();
        textureAtlasRifleShoot.dispose();

        textureAtlasHandgunMove.dispose();
        textureAtlasHandgunReload.dispose();
        textureAtlasHandgunShoot.dispose();
    }

    /**
     * Resets shooting animation's variables.
     */
    public void resetShooting() {
        isShooting = false;
        shootTime = Float.MAX_VALUE;
    }

    /**
     * Resets reloading animation's variables.
     */
    public void resetReload() {
        isReloading = false;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public boolean didShoot() {
        return didShoot;
    }

    public boolean didKnife() {
        return didKnife;
    }

    public boolean didReload() {
        return didReload;
    }

    public void setDidShoot(boolean didShoot) {
        this.didShoot = didShoot;
    }

    public void setDidReload(boolean didReload) {
        this.didReload = didReload;
    }

    public void setDidKnife(boolean didKnife) {
        this.didKnife = didKnife;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public float getShootTime() {
        return shootTime;
    }
}

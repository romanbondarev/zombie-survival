package com.gdx.game.models.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.Application;
import com.gdx.game.models.ZombieShooter;

import static com.gdx.game.utils.WCC.worldToPixels;

public class ZombieShooterAnimation {
    public enum AnimationType {
        ATTACK, MOVE, IDLE
    }

    private ZombieShooter zombie;
    private AnimationType currentAnimation;

    private Animation<TextureAtlas.AtlasRegion> animation;
    private TextureAtlas textureAtlasMove;
    private TextureAtlas textureAtlasIdle;

    private float elapsedTime = 0f;
    private float attackTime = 0f;

    private boolean didHit = false;
    private double degrees = 0;

    public ZombieShooterAnimation(ZombieShooter zombie) {
        this.zombie = zombie;
        textureAtlasMove = Application.assetManager.get("animation-assets/zombie/shooterMove.atlas", TextureAtlas.class);
        textureAtlasIdle = Application.assetManager.get("animation-assets/zombie/shooterIdle.atlas", TextureAtlas.class);
        setAnimation(AnimationType.MOVE);
    }

    public void renderAnimation(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        TextureAtlas.AtlasRegion keyframe = animation.getKeyFrame(elapsedTime, true);

        /* ZombieShooter HP bar */
        batch.draw(Application.assetManager.get("ui/inventory-items/selector.png", Texture.class),
                worldToPixels(zombie.getPosition().x) - zombie.getHP() * 0.8f / 2,
                worldToPixels(zombie.getPosition().y) + 30,
                zombie.getHP() * 0.8f, 2);

        /* Keyframe change */
        batch.draw(keyframe,
                worldToPixels(zombie.getPosition().x) - (keyframe.getRegionWidth() / 2),
                worldToPixels(zombie.getPosition().y) - (keyframe.getRegionHeight() / 2),
                keyframe.getRegionWidth() / 2,
                keyframe.getRegionHeight() / 2,
                keyframe.getRegionWidth(),
                keyframe.getRegionHeight(),
                1, 1, ((float) degrees));
    }

    public void updateAnimation(double degrees) {
        this.degrees = degrees;
    }

    public void setAnimation(AnimationType type) {
        float duration = 1f / 30f;
        Array<TextureAtlas.AtlasRegion> regions = null;
        AnimationType preCalc = currentAnimation;
        switch (type) {
            case IDLE:
                duration = 1f / 20f;
                regions = textureAtlasIdle.getRegions();
                preCalc = AnimationType.IDLE;
                break;
            case MOVE:
                duration = 1f / 20f;
                regions = textureAtlasMove.getRegions();
                preCalc = AnimationType.MOVE;
                break;
        }
        if (!preCalc.equals(currentAnimation)) {
            counterToZero();
            animation = new Animation<TextureAtlas.AtlasRegion>(duration, regions);
            currentAnimation = preCalc;
        }
    }

    private void counterToZero() {
        attackTime = 0f;
        elapsedTime = 0f;
    }

    public void dispose() {
        textureAtlasMove.dispose();
    }

    public boolean didHit() {
        return didHit;
    }

    public void setDidHit(boolean didHit) {
        this.didHit = didHit;
    }

}

package com.gdx.game.models.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.Application;
import com.gdx.game.models.Zombie;

import static com.gdx.game.utils.WCC.worldToPixels;

public class ZombieAnimation {
    public enum AnimationType {
        ATTACK, MOVE, IDLE
    }

    private Zombie zombie;
    private AnimationType currentAnimation;

    private Animation<TextureAtlas.AtlasRegion> animation;
    private TextureAtlas textureAtlasAttack;
    private TextureAtlas textureAtlasMove;
    private TextureAtlas textureAtlasIdle;

    private float elapsedTime = 0f;
    private float attackTime = 0f;

    private boolean didHit = false;
    private double degrees = 0;

    public ZombieAnimation(Zombie zombie) {
        this.zombie = zombie;
        textureAtlasAttack = Application.assetManager.get("animation-assets/zombie/zombieAttack.atlas", TextureAtlas.class);
        textureAtlasMove = Application.assetManager.get("animation-assets/zombie/zombieMove.atlas", TextureAtlas.class);
        textureAtlasIdle = Application.assetManager.get("animation-assets/zombie/zombieIdle.atlas", TextureAtlas.class);
        setAnimation(AnimationType.ATTACK);
    }

    public void renderAnimation(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        TextureAtlas.AtlasRegion keyframe = animation.getKeyFrame(elapsedTime, true);

        /* Zombie HP bar */
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
        if (currentAnimation.equals(AnimationType.ATTACK)) {
            attackTime += 0.1f;
            if (attackTime > 4f) {
                didHit = true;
                setAnimation(AnimationType.MOVE);
                counterToZero();
            }
        }
        this.degrees = degrees;
    }

    public void setAnimation(AnimationType type) {
        float duration = 1f / 30f;
        Array<TextureAtlas.AtlasRegion> regions = null;
        AnimationType preCalc = currentAnimation;
        switch (type) {
            case ATTACK:
                duration = 1f / 10f;
                regions = textureAtlasAttack.getRegions();
                preCalc = AnimationType.ATTACK;
                break;
            case MOVE:
                duration = 1f / 20f;
                regions = textureAtlasMove.getRegions();
                preCalc = AnimationType.MOVE;
                break;
            case IDLE:
                duration = 1f / 20f;
                regions = textureAtlasIdle.getRegions();
                preCalc = AnimationType.IDLE;
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
        textureAtlasAttack.dispose();
        textureAtlasMove.dispose();
        textureAtlasIdle.dispose();
    }

    public boolean didHit() {
        return didHit;
    }

    public void setDidHit(boolean didHit) {
        this.didHit = didHit;
    }
}

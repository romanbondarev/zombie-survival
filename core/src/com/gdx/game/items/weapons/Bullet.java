package com.gdx.game.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.Application;
import com.gdx.game.utils.Constants;

import static com.gdx.game.utils.WCC.pixelsToWorld;
import static com.gdx.game.utils.WCC.worldToPixels;

public class Bullet {
    private Sprite sprite;
    private Body body;
    private int damage;
    private boolean canDelete = false;

    public Bullet(World world, Vector2 shooter, Vector2 target, int damage) {
        this.damage = damage;
        sprite = new Sprite(Application.assetManager.get("bullet.png", Texture.class));
        sprite.setScale(0.65f);

        float bulletX = shooter.x;
        float bulletY = shooter.y;

        /* MATH */
        float x = target.x - shooter.x;
        float y = target.y - shooter.y;
        double distanceInBetween = Math.sqrt(x * x + y * y);
        double degrees = -Math.toDegrees(Math.atan2(y, x));

        float radius = 35;
        double newX = radius * Math.sin(Math.toRadians(90 - degrees));
        double newY = radius * Math.sin(Math.toRadians(-degrees));
        /* /MATH */

        // Body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(bulletX + (pixelsToWorld((float) newX)), bulletY + pixelsToWorld((float) newY));
        bodyDef.fixedRotation = true;

        // Define shape of the body.
        CircleShape shape = new CircleShape();
        shape.setRadius(pixelsToWorld(3));

        // Define fixture of the body.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;

        // Create the body and add fixture to it
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        sprite.setRotation(-((float) degrees) - 90);
        shape.dispose();

        body.setLinearVelocity((float) (x / distanceInBetween) * Constants.BULLET_SPEED,
                (float) (y / distanceInBetween) * Constants.BULLET_SPEED);
    }

    public void update() {
        sprite.setPosition(worldToPixels(body.getPosition().x) - (sprite.getWidth() / 2),
                worldToPixels(body.getPosition().y) - (sprite.getHeight() / 2));
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Body getBody() {
        return body;
    }

    public int getDamage() {
        return damage;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanDelete() {
        return canDelete;
    }
}

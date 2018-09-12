package com.gdx.game.models;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.managers.SoundManager;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.gdx.game.utils.Constants.DIFFICULT_GAME;
import static com.gdx.game.utils.Constants.PPM;

public abstract class Enemy {
    int HP = 100;
    int multiplier;
    boolean trouble = false;
    boolean one = false;
    boolean two = false;
    boolean three = false;
    boolean four = false;
    boolean hit = false;
    List<Float> xPos = new LinkedList<>();
    List<Float> yPos = new LinkedList<>();
    Random r = new Random();
    GameState gameState;
    Body body;
    World world;
    Music sound;

    /**
     * Creates an enemy.
     *
     * @param gameState game state for an game world access
     * @param x         initial x coordinate (world coordinates)
     * @param y         inittal y coordinate (world coordinates)
     */
    public Enemy(GameState gameState, float x, float y) {
        this.world = ((PlayState) gameState).getWorld();
        this.gameState = gameState;
        this.sound = SoundManager.randomZombieSound();

        // Body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 100;

        // Define shape of the body.
        CircleShape shape = new CircleShape();
        shape.setRadius(23 / PPM);

        // Define fixture of the body.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;

        // Create the body and add fixture to it
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        // Random Zombie Speed
        multiplier = DIFFICULT_GAME ? (r.nextInt(15) + 10) / 10 : (r.nextInt(10) + 10) / 10;
    }

    public abstract void update(Player player);

    /**
     * Reduces the enemy's health by damage amount.
     */
    public void takeDamage(int damage) {
        HP -= damage;
        SoundManager.zombieDamage().play();
        if (HP <= 0) SoundManager.zombieDeath().play();
    }

    /**
     * Gets the enemy's body.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Gets the enemy's position.
     */
    public Vector2 getPosition() {
        return body.getPosition();
    }

    /**
     * Gets the enemy's health.
     */
    public int getHP() {
        return HP;
    }

}

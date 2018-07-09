package com.gdx.game.utils.spawners;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.gdx.game.items.Item;
import com.gdx.game.items.MedKit;
import com.gdx.game.items.armor.Helmet;
import com.gdx.game.items.armor.Vest;
import com.gdx.game.items.weapons.Handgun;
import com.gdx.game.items.weapons.Rifle;
import com.gdx.game.items.weapons.ammo.HandgunAmmo;
import com.gdx.game.items.weapons.ammo.RifleAmmo;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;

import java.util.Random;

public final class ItemSpawner {
    private GameState state;
    private String spawnedItem;
    private Item base;
    private float xPos, yPos;
    private int counter, timeLimit;
    private Random random;

    public ItemSpawner(GameState state, String spawnedItem, float xPos, float yPos) {
        this.state = state;
        this.spawnedItem = spawnedItem;
        this.xPos = xPos;
        this.yPos = yPos;
        this.random = new Random();
        this.base = createItem();
        this.base.setCircleSpritePosition(xPos, yPos);
        ((PlayState) this.state).getItems().add(base);
        this.timeLimit = 3600 + random.nextInt(180) * 10; // At least 1 minute + max 30 seconds
    }

    public void update() {
        if (hasItem()) {
            counter = 0;
        } else {
            counter++;
            if (counter >= timeLimit) spawnItem();
        }
    }

    /**
     * Checks if any item in the world is located at spawner's area.
     */
    private boolean hasItem() {
        return ((PlayState) state).getItems()
                .stream()
                .anyMatch(item -> Intersector.overlaps(
                        new Circle(item.getCircleSprite().getX(), item.getCircleSprite().getY(), 25),
                        new Circle(xPos, yPos, 25))
                );
    }

    /**
     * Creates a new item and adds it to the world.
     */
    private void spawnItem() {
        Item item = createItem();
        item.setCircleSpritePosition(xPos, yPos);
        ((PlayState) state).getItems().add(item);
    }

    private Item createItem() {
        Item temp = null;
        switch (spawnedItem) {
            case "rifle":
                temp = new Rifle("RIFLE");
                ((Rifle) temp).loadAmmo(new RifleAmmo(15 + random.nextInt(15), "RIFFLE AMMO"));
                ((Rifle) temp).reload();
                break;
            case "handgun":
                temp = new Handgun("HANDGUN");
                ((Handgun) temp).loadAmmo(new HandgunAmmo(15 + random.nextInt(15), "HANDGUN AMMO"));
                ((Handgun) temp).reload();
                break;
            case "rifleammo":
                temp = new RifleAmmo(10 + 5 * random.nextInt(6), "RIFLE AMMO");
                break;
            case "handgunammo":
                temp = new HandgunAmmo(10 + 5 * random.nextInt(6), "HANDGUN AMMO");
                break;
            case "medkit":
                temp = new MedKit(25);
                break;
            case "helmet":
                temp = new Helmet("HELMET", 1 + random.nextInt(3), 20 + random.nextInt(30));
                break;
            case "vest":
                temp = new Vest("BULLETPROOF VEST", 1 + random.nextInt(3), 20 + random.nextInt(30));
                break;
        }
        return temp;
    }

    public int getCounter() {
        return counter;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public float getX() {
        return xPos;
    }

    public float getY() {
        return yPos;
    }

    public Item getBase() {
        return base;
    }
}

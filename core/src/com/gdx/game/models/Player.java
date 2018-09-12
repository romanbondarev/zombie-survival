package com.gdx.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.gdx.game.items.Inventory;
import com.gdx.game.items.Item;
import com.gdx.game.items.weapons.Rifle;
import com.gdx.game.items.weapons.Shotgun;
import com.gdx.game.items.weapons.Weapon;
import com.gdx.game.items.weapons.ammo.RifleAmmo;
import com.gdx.game.items.weapons.ammo.ShotgunAmmo;
import com.gdx.game.managers.SoundManager;
import com.gdx.game.models.animations.PlayerAnimation;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.Constants;

import java.util.LinkedList;
import java.util.List;

import static com.gdx.game.utils.Constants.SMOOTH_MOVEMENT;
import static com.gdx.game.utils.Constants.TRAY_SIZE;
import static com.gdx.game.utils.WCC.mouseClickWorldPosition;
import static com.gdx.game.utils.WCC.pixelsToWorld;
import static com.gdx.game.utils.WCC.worldToPixels;

public final class Player {
    private GameState gameState;
    private Camera camera;
    private Body body;
    private Inventory inventory;
    private PlayerAnimation animation;
    private List<Zombie> zombies;
    private List<ZombieShooter> zombieShooters;
    private boolean reloadQueue = false;
    private boolean shootQueue = false;
    private boolean singleShoot = false;
    private int health;

    public Player(GameState gameState, int x, int y, int health) {
        this.gameState = gameState;
        this.camera = ((PlayState) this.gameState).getCamera();
        this.zombies = ((PlayState) gameState).getZombies();
        this.zombieShooters = ((PlayState) gameState).getZombieShooters();

        // Body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pixelsToWorld(x), pixelsToWorld(y));
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 6;

        // Define shape of the body.
        CircleShape shape = new CircleShape();
        shape.setRadius(pixelsToWorld(23));

        // Define fixture of the body.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;

        // Create the body and add fixture to it
        body = ((PlayState) gameState).getWorld().createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        // Creating an inventory
        this.inventory = new Inventory(this);
        this.health = health;
        Constants.DEAD = false;

        initInventory();
        this.animation = new PlayerAnimation(this);
    }


    public void update() {
        Item item = inventory.getSelectedCellItem();
        movementUpdate(item);
        eventsUpdate(item);
        mouseTargetLock();
    }

    private void movementUpdate(Item item) {
        int velX = 0, velY = 0, force = 10;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            animation.setAnimation(animation.chooseAnimation(item, PlayerAnimation.AnimationType.MOVE));
            if (!SMOOTH_MOVEMENT) velY += 1;
            else body.applyForceToCenter(0, force, false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            animation.setAnimation(animation.chooseAnimation(item, PlayerAnimation.AnimationType.MOVE));
            if (!SMOOTH_MOVEMENT) velY -= 1;
            else body.applyForceToCenter(0, -force, false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            animation.setAnimation(animation.chooseAnimation(item, PlayerAnimation.AnimationType.MOVE));
            if (!SMOOTH_MOVEMENT) velX -= 1;
            else body.applyForceToCenter(-force, 0, false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            animation.setAnimation(animation.chooseAnimation(item, PlayerAnimation.AnimationType.MOVE));
            if (!SMOOTH_MOVEMENT) velX += 1;
            else body.applyForceToCenter(force, 0, false);
        }

        if (!SMOOTH_MOVEMENT) body.setLinearVelocity(velX * 4.5f, velY * 4.5f); // DEFAULT
        else body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y);
    }

    private void eventsUpdate(Item item) {
        /* ### beginning SHOOTING logic ### */
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (item instanceof Weapon && ((Weapon) item).canShoot() && !shootQueue) {
                animation.setAnimation(animation.chooseAnimation(item, PlayerAnimation.AnimationType.SHOOT));
                reloadQueue = false;
                shootQueue = true;
                singleShoot = true;
            } else if (item == null) {
                animation.setAnimation(PlayerAnimation.AnimationType.MELEE_ATTACK);
                reloadQueue = false;
                shootQueue = true;
                singleShoot = true;
            }
        } else {
            shootQueue = false;
            singleShoot = false;
            animation.setDidShoot(true);
            animation.setDidKnife(false);
            animation.resetShooting();
            animation.chooseAnimation(item, PlayerAnimation.AnimationType.MOVE);
        }

        if (shootQueue && (animation.didShoot() || animation.didKnife()) && singleShoot) {
            if (item instanceof Rifle) {
                ((Weapon) item).shoot((gameState), camera, this);
                shootQueue = false;
            } else if (item instanceof Weapon) {
                ((Weapon) item).shoot((gameState), camera, this);
                singleShoot = false;
            } else if (item == null) {
                for (Zombie zombie : zombies) {
                    double x = zombie.getPosition().x - this.getPosition().x;
                    double y = zombie.getPosition().y - this.getPosition().y;
                    double distanceInBetween = Math.sqrt(x * x + y * y);
                    if (distanceInBetween < 1f) {
                        zombie.takeDamage(Constants.meleeDamage);
                        shootQueue = false;
                        break;
                    }
                }
                for (ZombieShooter zombieShooter : zombieShooters) {
                    double x = zombieShooter.getPosition().x - this.getPosition().x;
                    double y = zombieShooter.getPosition().y - this.getPosition().y;
                    double distanceInBetween = Math.sqrt(x * x + y * y);
                    if (distanceInBetween < 1f) {
                        zombieShooter.takeDamage(Constants.meleeDamage);
                        shootQueue = false;
                        break;
                    }
                }
                singleShoot = false;
                animation.setDidKnife(false);
            }
            animation.setDidShoot(false);
        }
        /* ### end SHOOTING logic ### */


        /* ### beginning RELOADING logic ### */
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (item instanceof Weapon && ((Weapon) item).canReload()) {
                animation.setAnimation(animation.chooseAnimation(item, PlayerAnimation.AnimationType.RELOAD));
                reloadQueue = true; // Queue the reload
            }
        }

        if (reloadQueue && animation.didReload() && item instanceof Weapon) {
            // If reload is queued and reload animation is finished
            ((Weapon) item).reload();
            reloadQueue = false;
            animation.setDidReload(false);
        }
        /* ### end RELOADING logic ### */


        /* ### Open inventory ### */
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) inventory.toggle();

        /* ### Pick up/use item ### */
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            // See how item removing and adding works in PlayState objectDeletion() method
            for (Item item1 : new LinkedList<>(((PlayState) gameState).getItems())) {
                double x = item1.getCircleSprite().getX() + item1.getCircleSprite().getHeight() / 2 - worldToPixels(getPosition().x);
                double y = item1.getCircleSprite().getY() + item1.getCircleSprite().getWidth() / 2 - worldToPixels(getPosition().y);
                double distanceInBetween = Math.sqrt(x * x + y * y);

                if (distanceInBetween < 46) {
                    if (inventory.hasSpace()) {
                        SoundManager.itemPickUp().play();
                        System.out.println("PICKED UP");
                        ((PlayState) gameState).getItems().remove(item1);
                        inventory.addItemToInventory(item1);
                        break;
                    }
                }
            }
        }

        /* ### Select the TRAY item ### */
        for (int i = 8; i < 17; i++) {
            if (Gdx.input.isKeyJustPressed(i) && i - 8 < TRAY_SIZE){
                animation.resetReload();
                inventory.setSelectedCellID(i - 8);
                animation.setAnimation(animation.chooseAnimation(inventory.getSelectedCellItem(), PlayerAnimation.AnimationType.MOVE));
            }
        }
    }

    private void mouseTargetLock() {
        Vector2 vector = mouseClickWorldPosition(((PlayState) gameState).getCamera(), Gdx.input.getX(), Gdx.input.getY());
        double degrees = Math.toDegrees(Math.atan2(vector.y - getPosition().y, vector.x - getPosition().x));
        animation.updateAnimation(degrees);
    }

    public void render() {
        SpriteBatch batch = ((PlayState) gameState).getBatch();
        animation.renderAnimation(batch);
    }

    private void initInventory() {
        Rifle rifle = new Rifle("Riffle");
        rifle.loadAmmo(new RifleAmmo(5000, "AMMO"));
        rifle.reload();
        Shotgun shotgun = new Shotgun("Handgun");
        shotgun.loadAmmo(new ShotgunAmmo(5000, "AMMO"));
        shotgun.reload();
        inventory.addItemToInventory(rifle);
        inventory.selectItem(rifle);
        inventory.setSelectedCellID(1);
        inventory.addItemToInventory(shotgun);
        inventory.selectItem(shotgun);
        inventory.setSelectedCellID(0);
    }

    public void takeDamage(int damage) {
        damage = 0;
        int helmetDamageAbsorption;
        int vestDamageAbsorption;

        if (inventory.getHelmetArmor() != null) {
            helmetDamageAbsorption = inventory.getHelmetArmor().getDamageAbsorptionLevel();
            inventory.getHelmetArmor().wear(1);
            if (inventory.getHelmetArmor().isBroken()) inventory.setHelmetArmor(null);
        } else helmetDamageAbsorption = 0;

        if (inventory.getVestArmor() != null) {
            vestDamageAbsorption = inventory.getVestArmor().getDamageAbsorptionLevel();
            inventory.getVestArmor().wear(1);
            if (inventory.getVestArmor().isBroken()) inventory.setVestArmor(null);
        } else vestDamageAbsorption = 0;


        int armor = helmetDamageAbsorption + vestDamageAbsorption;
        if (health - damage + armor > 0) {
            if (armor > damage) ;
            else health = health - damage + armor;
        } else {
            Constants.DEAD = true;
        }
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PlayerAnimation getAnimation() {
        return animation;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}

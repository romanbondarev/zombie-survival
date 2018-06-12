package com.gdx.game.items.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.items.Item;
import com.gdx.game.items.weapons.ammo.Ammo;
import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;

import static com.gdx.game.utils.WCC.mouseClickWorldPosition;

public abstract class Weapon extends Item {
    int mainClipAmmo;
    int secondClipAmmo;
    final int clipMaxAmmo;
    private int damage;
    private WeaponType weaponType;

    public enum WeaponType {
        SHOTGUN, RIFLE, HANDGUN
    }

    /**
     * Creates a weapon.
     *
     * @param weaponType  type of the created weapon
     * @param name        name of the created weapon
     * @param damage      damage of the created weapon
     * @param clipMaxAmmo maximum amount of bullets in main clip of the created weapon
     * @param circle      circle shaped texture
     * @param square      square shaped texture
     */
    public Weapon(WeaponType weaponType, String name, int damage, int clipMaxAmmo, Texture circle, Texture square) {
        super(ItemType.WEAPON, name, circle, square);
        this.weaponType = weaponType;
        this.mainClipAmmo = 0;
        this.secondClipAmmo = 0;
        this.clipMaxAmmo = clipMaxAmmo;
        this.damage = damage;
    }

    /**
     * Loads all the ammo bullets to the second clip. (main clip remains untouched)
     */
    public void loadAmmo(Ammo ammo) {
        if (ammo.getWeaponType().equals(weaponType)) {
            while (ammo.getBulletsAmount() > 0 || mainClipAmmo < clipMaxAmmo) {
                if (ammo.getBulletsAmount() <= 0) break;
                secondClipAmmo += ammo.load(1);
            }
        }
    }

    /**
     * Reloads the main clip to the max possible.
     */
    public void reload() {
        while (secondClipAmmo > 0 && mainClipAmmo < clipMaxAmmo) {
            reloadSingle();
        }
    }

    /**
     * Reloads the main clip by the single bullet.
     */
    public void reloadSingle() {
        if (secondClipAmmo > 0 && mainClipAmmo < clipMaxAmmo) {
            secondClipAmmo--;
            mainClipAmmo++;
        }
    }

    /**
     * Single shoot.
     *
     * @param gameState to have an access to bullet list
     * @param camera    for coordinate conversion
     * @param player    target
     */
    public void shoot(GameState gameState, Camera camera, Player player) {
        mainClipAmmo--;
        ((PlayState) gameState).getBullets().add(
                new Bullet(((PlayState) gameState).getWorld(),
                        new Vector2(player.getPosition().x, player.getPosition().y),
                        mouseClickWorldPosition(camera, Gdx.input.getX(), Gdx.input.getY()), getDamage())
        );
    }

    /**
     * Checks if the weapon can shoot.
     */
    public boolean canShoot() {
        return mainClipAmmo > 0;
    }

    /**
     * Checks if the weapon can reload.
     */
    public boolean canReload() {
        return mainClipAmmo < clipMaxAmmo;
    }

    /**
     * Gets the weapon's type.
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Gets the weapon's damage.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the weapon's main clip ammo amount.
     */
    public int getMainClipAmmo() {
        return mainClipAmmo;
    }

    /**
     * Gets the weapon's second clip ammo amount.
     */
    public int getSecondClipAmmo() {
        return secondClipAmmo;
    }

    /**
     * Gets the weapon's main clip maximum possible bullets amount.
     */
    public int getClipMaxAmmo() {
        return clipMaxAmmo;
    }

    @Override
    public String toString() {
        return mainClipAmmo + " / " + secondClipAmmo;
    }
}

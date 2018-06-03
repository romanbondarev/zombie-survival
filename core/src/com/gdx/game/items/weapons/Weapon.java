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

    public Weapon(WeaponType weaponType, String name, int damage, int clipMaxAmmo, Texture round, Texture square) {
        super(ItemType.WEAPON, name, round, square);
        this.weaponType = weaponType;
        this.mainClipAmmo = 0;
        this.secondClipAmmo = 0;
        this.clipMaxAmmo = clipMaxAmmo;
        this.damage = damage;
    }

    public void loadAmmo(Ammo ammo) {
        if (ammo.getWeaponType().equals(weaponType)) {
            while (ammo.getBulletsAmount() > 0 || mainClipAmmo < clipMaxAmmo) {
                if (ammo.getBulletsAmount() <= 0) break;
                secondClipAmmo += ammo.load(1);
            }
        }
    }

    public void reload() {
        while (secondClipAmmo > 0 && mainClipAmmo < clipMaxAmmo) {
            reloadSingle();
        }
    }

    public void reloadSingle() {
        if (secondClipAmmo > 0 && mainClipAmmo < clipMaxAmmo) {
            secondClipAmmo--;
            mainClipAmmo++;
        }
    }

    public void shoot(GameState gameState, Camera camera, Player player) {
        if (canShoot()) mainClipAmmo--;
        ((PlayState) gameState).getBullets().add(
                new Bullet(((PlayState) gameState).getWorld(),
                        new Vector2(player.getPosition().x, player.getPosition().y),
                        mouseClickWorldPosition(camera, Gdx.input.getX(), Gdx.input.getY()), getDamage())
        );
    }

    public boolean canShoot() {
        return mainClipAmmo > 0;
    }

    public boolean canReload() {
        return mainClipAmmo < clipMaxAmmo;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public int getDamage() {
        return damage;
    }

    public int getMainClipAmmo() {
        return mainClipAmmo;
    }

    public int getSecondClipAmmo() {
        return secondClipAmmo;
    }

    public int getClipMaxAmmo() {
        return clipMaxAmmo;
    }

    @Override
    public String toString() {
        return mainClipAmmo + " / " + secondClipAmmo;
    }
}

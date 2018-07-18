package com.gdx.game.items.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.Application;
import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.Constants;
import com.gdx.game.utils.Utils;

import java.util.Arrays;

import static com.gdx.game.utils.WCC.mouseClickWorldPosition;

public class Shotgun extends Weapon {

    // TODO: 09.07.2018 animations, inventory
    public Shotgun(String name) {
        super(WeaponType.SHOTGUN, name, Constants.shotgunDamage, 5,
                Application.assetManager.get("ui/inventory-items-round/shotgun.png", Texture.class),
                Application.assetManager.get("ui/inventory-items/shotgun.png", Texture.class));
    }

    @Override
    public void shoot(GameState gameState, Camera camera, Player player) {
        mainClipAmmo--;

        Vector2 target = mouseClickWorldPosition(camera, Gdx.input.getX(), Gdx.input.getY());
        Vector2 playerPos = player.getPosition();

        for (Integer angle : Arrays.asList(-10, 0, 10)) {
            ((PlayState) gameState).getBullets().add(
                    new Bullet(((PlayState) gameState).getWorld(), playerPos, offsetTarget(target, playerPos, angle), getDamage())
            );
        }
    }


    /**
     * @param target
     * @param playerPos
     * @param angle     negative to the left, positive to the right
     * @return Vector2 of new target
     */
    private Vector2 offsetTarget(Vector2 target, Vector2 playerPos, double angle) {
        double initialAngle = Utils.getDegreesBetween(target, playerPos);
        double A = Utils.getDistanceBetween(target, playerPos);

        double rightAngle = initialAngle - angle;

        double gammaR = 90 - rightAngle;
        double BR = A * Math.sin(Math.toRadians(rightAngle));
        double CR = A * Math.sin(Math.toRadians(gammaR));
        return new Vector2(((float) (playerPos.x + CR)), ((float) (playerPos.y + BR)));
    }
}

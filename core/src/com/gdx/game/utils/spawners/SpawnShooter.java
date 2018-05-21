package com.gdx.game.utils.spawners;

import com.gdx.game.models.Player;
import com.gdx.game.models.ZombieShooter;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;

public final class SpawnShooter extends Spawner {
    public SpawnShooter(GameState gameState, Player player, float xPos, float yPos) {
        super(gameState, player, xPos, yPos);
    }

    @Override
    public void spawn() {
        boolean canSpawn = true;
        for (int i = 0; i < ((PlayState) gamestate).getZombieShooters().size(); i++) {
            double x = ((PlayState) gamestate).getZombieShooters().get(i).getPosition().x;
            double y = ((PlayState) gamestate).getZombieShooters().get(i).getPosition().y;
            distanceInBetween = Math.pow(Math.pow((xPos - x), 2) + Math.pow((yPos - y), 2), 0.5);
            if (distanceInBetween < 2) {
                canSpawn = false;
                break;
            }
        }
        if (canSpawn) ((PlayState) gamestate).getZombieShooters().add(new ZombieShooter(gamestate, xPos, yPos));
    }
}

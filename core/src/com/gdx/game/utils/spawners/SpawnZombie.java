package com.gdx.game.utils.spawners;

import com.gdx.game.models.Player;
import com.gdx.game.models.Zombie;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;

public final class SpawnZombie extends Spawner {
    public SpawnZombie(GameState gameState, Player player, float xPos, float yPos) {
        super(gameState, player, xPos, yPos);
    }

    @Override
    public void spawn() {
        boolean canSpawn = true;
        for (int i = 0; i < ((PlayState) gamestate).getZombies().size(); i++) {
            double x = ((PlayState) gamestate).getZombies().get(i).getPosition().x;
            double y = ((PlayState) gamestate).getZombies().get(i).getPosition().y;
            distanceInBetween = Math.pow(Math.pow((xPos - x), 2) + Math.pow((yPos - y), 2), 0.5);
            if (distanceInBetween < 2) {
                canSpawn = false;
                break;
            }
        }

        if (canSpawn) ((PlayState) gamestate).getZombies().add(new Zombie(gamestate, xPos, yPos));
    }
}

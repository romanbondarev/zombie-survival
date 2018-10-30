package com.gdx.game.utils.spawners;

import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;
import com.gdx.game.utils.WCC;

import java.util.Random;

public abstract class Spawner {

    GameState gamestate;
    private Player player;
    float xPos;
    float yPos;
    double distanceInBetween;
    private int counter = 0;
    private int timeOut = 0;
    private Random random = new Random();

    public Spawner(GameState gameState, Player player, float xPos, float yPos) {
        this.gamestate = gameState;
        this.player = player;
        this.xPos = xPos;
        this.yPos = yPos;
        timeOut = 300 + random.nextInt(30) * 10;
        spawn();
    }

    public void update() {
        double x = WCC.pixelsToWorld(xPos);
        double y = WCC.pixelsToWorld(yPos);
        distanceInBetween = Math.pow(Math.pow((player.getPosition().x - x), 2) + Math.pow((player.getPosition().y - y), 2), 0.5);

        counter++;
        if (counter > timeOut && distanceInBetween < 10) {
            spawn();
            counter = 0;
            timeOut = 300 + random.nextInt(30) * 10;
        }
    }

    public abstract void spawn();
}

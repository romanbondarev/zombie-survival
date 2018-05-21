package com.gdx.game.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;

/**
 * Tiled Map Layer Parser
 */
public class TMLP {
    /**
     * Parses tiles from tile map.
     */
    public static void parseMapLayers(World world, TiledMap tiledMap, String... layers) {
        for (String layer : layers) {
            TiledObjectUtils.parseTiledObjectLayer(world, tiledMap.getLayers().get(layer).getObjects());
        }
    }

    /**
     * Parses items from tile map.
     */
    public static void parserMapItems(GameState state, TiledMap tiledMap, String... layers) {
        for (String layer : layers) {
            TiledObjectUtils.parseTiledItemLayer(state, tiledMap.getLayers().get(layer).getObjects());
        }
    }

    /**
     * Parses spawners from tile map.
     */
    public static void parseMapSpawners(GameState state, Player player, TiledMap tiledMap, String... layers) {
        for (String layer : layers) {
            TiledObjectUtils.parseTiledSpawnerZombieLayer(state, player, tiledMap.getLayers().get(layer).getObjects());
        }
    }


}

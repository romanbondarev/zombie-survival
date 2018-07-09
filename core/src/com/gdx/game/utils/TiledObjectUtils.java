package com.gdx.game.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.models.Player;
import com.gdx.game.models.Zombie;
import com.gdx.game.models.ZombieShooter;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.spawners.ItemSpawner;
import com.gdx.game.utils.spawners.SpawnShooter;
import com.gdx.game.utils.spawners.SpawnZombie;

import static com.gdx.game.utils.Constants.PPM;

public class TiledObjectUtils {
    public static void parseTiledItemLayer(GameState state, MapObjects mapObjects) {
        for (MapObject object : mapObjects) {
            if (object.getName() != null && object.getName().split("\\.")[0].equals("Item")) {
                createItem(((PlayState) state), ((EllipseMapObject) object));
            }
        }
    }

    public static void parseTiledSpawnerZombieLayer(GameState state, Player player, MapObjects mapObjects) {
        for (MapObject object : mapObjects) {
            if (object.getName() != null && object.getName().split("\\.")[0].equals("Spawner")) {
                createSpawner(state, player, ((EllipseMapObject) object));
            }
            if (object.getName() != null && object.getName().split("\\.")[0].equals("Zombie")) {
                createZombie(state, ((EllipseMapObject) object));
            }
        }
    }

    private static void createZombie(GameState state, EllipseMapObject mapObject) {
        if (mapObject.getName().split("\\.")[1].equals("standard")) {
            ((PlayState) state).getZombies().add(new Zombie(state, mapObject.getEllipse().x, mapObject.getEllipse().y));
        }
        if (mapObject.getName().split("\\.")[1].equals("shooter")) {
            ((PlayState) state).getZombieShooters().add(new ZombieShooter(state, mapObject.getEllipse().x, mapObject.getEllipse().y));
        }
    }

    private static void createSpawner(GameState state, Player player, EllipseMapObject mapObject) {
        if (mapObject.getName().split("\\.")[1].equals("zombie")) {
            ((PlayState) state).getSpawners().add(new SpawnZombie(state, player, mapObject.getEllipse().x, mapObject.getEllipse().y));
        }
        if (mapObject.getName().split("\\.")[1].equals("shooter")) {
            ((PlayState) state).getSpawners().add(new SpawnShooter(state, player, mapObject.getEllipse().x, mapObject.getEllipse().y));
        }
    }

    public static void parseTiledObjectLayer(World world, MapObjects mapObjects) {
        for (MapObject object : mapObjects) {
            Shape shape;
            Body body;

            if (object instanceof PolylineMapObject) {
                shape = createPolyline(((PolylineMapObject) object));
            } else {
                continue;
            }

            BodyDef def = new BodyDef();
            def.linearDamping = 5;
            def.type = BodyDef.BodyType.StaticBody;

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1.0f;
            fixtureDef.shape = shape;

            body = world.createBody(def);
            body.createFixture(fixtureDef);
            shape.dispose();
        }
    }

    private static void createItem(PlayState state, EllipseMapObject mapObject) {
        Ellipse ellipse = mapObject.getEllipse();
        state.getItemSpawners().add(new ItemSpawner(state, mapObject.getName().split("\\.")[1], ellipse.x, ellipse.y));
    }

    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        return chainShape;
    }
}

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
import com.gdx.game.items.Item;
import com.gdx.game.items.MedKit;
import com.gdx.game.items.armor.Helmet;
import com.gdx.game.items.armor.Vest;
import com.gdx.game.items.weapons.Handgun;
import com.gdx.game.items.weapons.Rifle;
import com.gdx.game.items.weapons.ammo.HandgunAmmo;
import com.gdx.game.items.weapons.ammo.RifleAmmo;
import com.gdx.game.models.Player;
import com.gdx.game.models.Zombie;
import com.gdx.game.models.ZombieShooter;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.spawners.SpawnShooter;
import com.gdx.game.utils.spawners.SpawnZombie;

import java.util.Random;

import static com.gdx.game.utils.Constants.PPM;

public class TiledObjectUtils {
    public static void parseTiledItemLayer(GameState state, MapObjects mapObjects) {
        for (MapObject object : mapObjects) {
            if (object.getName() != null && object.getName().split("\\.")[0].equals("Item")) {
                createItem(((PlayState) state), ((PlayState) state).getWorld(), ((EllipseMapObject) object));
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

    private static void createItem(PlayState state, World world, EllipseMapObject mapObject) {
        Ellipse ellipse = mapObject.getEllipse();

        Random r = new Random();
        Item temp = null;

        switch (mapObject.getName().split("\\.")[1]) {
            case "rifle":
                temp = new Rifle("RIFLE");
                ((Rifle) temp).loadAmmo(new RifleAmmo(15 + r.nextInt(15), "RIFFLE AMMO"));
                ((Rifle) temp).reload();
                break;
            case "handgun":
                temp = new Handgun("HANDGUN");
                ((Handgun) temp).loadAmmo(new HandgunAmmo(15 + r.nextInt(15), "HANDGUN AMMO"));
                ((Handgun) temp).reload();
                break;
            case "rifleammo":
                temp = new RifleAmmo(10 + 5 * r.nextInt(6), "RIFLE AMMO");
                break;
            case "handgunammo":
                temp = new HandgunAmmo(10 + 5 * r.nextInt(6), "HANDGUN AMMO");
                break;
            case "medkit":
                temp = new MedKit(25);
                break;
            case "helmet":
                temp = new Helmet("HELMET", 1 + r.nextInt(3), 20 + r.nextInt(30));
                break;
            case "vest":
                temp = new Vest("BULLETPROOF VEST", 1 + r.nextInt(3), 20 + r.nextInt(30));
                break;
        }

        if (temp != null) {
            temp.setSpritePosition(ellipse.x - temp.getSprite().getWidth() / 2, ellipse.y - temp.getSprite().getHeight() / 2);
            state.getItems().add(temp);
        }
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

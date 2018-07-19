package com.gdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class Utils {
    private static final String statsPath = "stats.json";

    public static Optional<JSONArray> loadGameJSON() {
        File file = new File(statsPath);
        if (file.length() > 0) {
            Object objs = null;
            try {
                objs = new JSONParser().parse(new FileReader(statsPath));
            } catch (Exception e) {
                Gdx.app.exit();
            }

            JSONArray jsonObject = (JSONArray) objs;
            return Optional.ofNullable(jsonObject);
        }
        return Optional.empty();
    }

    public static void saveGameJSON(int killCounter) {
        JSONObject temp = new JSONObject();
        Optional<JSONArray> loadedGame = loadGameJSON();

        temp.put("date", LocalDateTime.now().toString());
        temp.put("player", "Player 1");
        temp.put("zombies-killed", String.valueOf(killCounter));

        try (FileWriter file2 = new FileWriter(statsPath)) {
            JSONArray tempArray = new JSONArray();
            tempArray.add(temp);

            if (loadedGame.isPresent()) {
                loadedGame.get().add(temp);
                tempArray = loadedGame.get();
            }

            file2.write(tempArray.toString());
            System.out.println("\nJSON Object: " + temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<JSONObject> getTopScoreJSON() {
        Optional<JSONArray> loadedGame = loadGameJSON();
        Object top = null;

        if (loadedGame.isPresent()) {
            top = loadedGame.get().stream().max(Comparator.comparing(o ->
                    Integer.valueOf(((JSONObject) o).get("zombies-killed").toString()))).get();
        }

        return Optional.ofNullable(((JSONObject) top));
    }


    public static double getDistanceBetween(Vector2 target, Vector2 player) {
        double x = target.x - player.x;
        double y = target.y - player.y;
        return Math.sqrt(x * x + y * y);
    }

    public static double getDegreesBetween(Vector2 target, Vector2 player) {
        double x = target.x - player.x;
        double y = target.y - player.y;
        return -Math.toDegrees(Math.atan2(x, y)) + 90;
    }

    public static Skin initSkin(String fontPath, int fontSize) {
        /* Setting up a new font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.color = Color.WHITE;
        BitmapFont segoeFont = generator.generateFont(parameter);

        /* Setting up a skin for UI widgets */
        Skin skin = new Skin();
        skin.addRegions(new TextureAtlas("ui/uiSkin.atlas"));
        skin.add("default-font", segoeFont, BitmapFont.class);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));

        return skin;
    }
}

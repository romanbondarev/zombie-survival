package com.gdx.game.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.gdx.game.utils.Constants.PPM;

/**
 * World Coordinates Converter
 */
public class WCC {

    /**
     * Converts mouse position coordinates to the world coordinates.
     */
    public static Vector2 mouseClickWorldPosition(Camera camera, float x, float y) {
        Vector3 worldCoordinates = new Vector3(x, y, 0);
        camera.unproject(worldCoordinates);
        return new Vector2(worldCoordinates.x / PPM, worldCoordinates.y / PPM);
    }

    /**
     * Converts pixels "size" to the world "size".
     */
    public static float pixelsToWorld(float pixels) {
        return pixels / PPM;
    }

    /**
     * Converts world "size" to the pixels "size".
     */
    public static float worldToPixels(float coords) {
        return coords * PPM;
    }

    /**
     * Gets the pixel coefficient to the world size units.
     */
    public static float getPPM() {
        return PPM;
    }
}

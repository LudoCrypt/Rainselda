package net.ludocrypt.rainselda.util;

import com.badlogic.gdx.graphics.Color;

public class MathHelper {

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static Color hexToColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        int r = hex.length() >= 2 ? Integer.parseInt(hex.substring(0, 2), 16) : 255;
        int g = hex.length() >= 4 ? Integer.parseInt(hex.substring(2, 4), 16) : 0;
        int b = hex.length() >= 6 ? Integer.parseInt(hex.substring(4, 6), 16) : 0;
        int a = hex.length() >= 8 ? Integer.parseInt(hex.substring(6, 8), 16) : 255;

        float fr = r / 255f;
        float fg = g / 255f;
        float fb = b / 255f;
        float fa = a / 255f;

        return new Color(fr, fg, fb, fa);
    }

}

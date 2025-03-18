package net.ludocrypt.rainselda.util;

public class MathHelper {

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

}

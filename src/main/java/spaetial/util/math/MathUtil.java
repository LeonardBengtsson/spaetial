package spaetial.util.math;

public final class MathUtil {
    private MathUtil() {}

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}

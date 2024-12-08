package spaetial.util.hud;

/**
 * Contains various methods that maps a value t, 0 <= t <= 1, to a value y, 0 <= y <= 1
 */
public final class AnimationUtil {
    private AnimationUtil() {}

    public static double easeIn(double t) {
        t = Math.clamp(t, 0, 1);
        return t * t;
    }
    public static double easeOut(double t) {
        t = Math.clamp(t, 0, 1);
        return t * (2 - t);
    }
    public static double easeInOut(double t) {
        t = Math.clamp(t, 0, 1);
        if (t <= .5) return 2 * t * t;
        t -= .5;
        return 2 * t * (1 - t) + .5;
    }
    public static double easeBezier(double t) {
        t = Math.clamp(t, 0, 1);
        return t * t * (3 - 2 * t);
    }
}

package spaetial.util.color;

import spaetial.Spaetial;
import spaetial.util.math.MathUtil;

import java.awt.*;

public final class ColorUtil {
    private static final double PREVIEW_ALPHA_FACTOR = .15;

    private ColorUtil() {}

    public static Color brighter(Color color, double factor) {
        var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return withAlpha(
            new Color(
                Color.HSBtoRGB(
                    hsb[0],
                    hsb[1],
                    (float) MathUtil.lerp(hsb[2], 1, factor)
                )
            ),
            color.getAlpha()
        );
    }

    public static Color darker(Color color, double factor) {
        var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return withAlpha(
            new Color(
                Color.HSBtoRGB(
                    hsb[0],
                    hsb[1],
                    (float) MathUtil.lerp(hsb[2], 0, factor)
                )
            ),
            color.getAlpha()
        );
//        return new Color(
//            (int) MathUtil.lerp(color.getRed(), 0, factor),
//            (int) MathUtil.lerp(color.getBlue(), 0, factor),
//            (int) MathUtil.lerp(color.getGreen(), 0, factor),
//            color.getAlpha()
//        );
    }

    public static Color withPreviewAlpha(Color color) {
        return scaleAlpha(color, PREVIEW_ALPHA_FACTOR);
    }

    public static Color scaleAlpha(Color color, double factor) {
        return withAlpha(color, (int) (factor * color.getAlpha()));
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}

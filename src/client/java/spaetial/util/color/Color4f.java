package spaetial.util.color;

import java.awt.*;

@Deprecated
public record Color4f(float red, float green, float blue, float alpha) {
    public static Color4f create(Color color) {
        return new Color4f(color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f, color.getAlpha() / 256f);
    }
}

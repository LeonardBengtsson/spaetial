package spaetial.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import spaetial.ClientConfig;
import spaetial.util.color.ColorUtil;
import spaetial.util.math.MathUtil;

import java.awt.*;
import java.util.function.Supplier;

@Deprecated
public class Button {
    public final Text text;
    public final boolean isCancelButton;
    private final Runnable clickAction;
    private final ColorScheme colorScheme;
    private final DrawMode drawMode;

    public Button(Text text, boolean isCancelButton, Runnable clickAction, ColorScheme colorScheme, DrawMode drawMode) {
        this.text = text;
        this.isCancelButton = isCancelButton;
        this.clickAction = clickAction;
        this.colorScheme = colorScheme;
        this.drawMode = drawMode;
    }

    public void click() {
        clickAction.run();
    }

    public void draw(DrawContext context, TextRenderer textRenderer, int x, int y, int w, int h, int xTextPadding, int yTextPadding, Color textColor, boolean textShadow, State state) {
//        switch (drawMode) {
//            case FILLED -> context.fill(x, y, x + w, y + h, colorScheme.getColor(state).getRGB());
//            case OUTLINE -> context.drawBorder(x, y, w, h, colorScheme.getColor(state).getRGB());
//            case FILLED_WHEN_HOVERED -> {
//                switch (state) {
//                    case NORMAL -> context.drawBorder(x, y, w, h, colorScheme.getColor(state).getRGB());
//                    case HOVERED -> context.fill(x, y, x + w, y + h, colorScheme.getColor(State.NORMAL).getRGB());
//                    case PRESSED -> context.fill(x, y, x + w, y + h, colorScheme.getColor(state).getRGB());
//                }
//            }
//        }
        Color color;
        if (drawMode == DrawMode.FILLED_WHEN_HOVERED && state == State.HOVERED) {
            color = colorScheme.getColor(State.NORMAL);
        } else {
            color = colorScheme.getColor(state);
        }

        boolean filled = drawMode == DrawMode.FILLED || (drawMode == DrawMode.FILLED_WHEN_HOVERED && state != State.NORMAL);
        if (filled) {
            context.fill(x, y, x + w, y + h, color.getRGB());
        } else {
            context.drawBorder(x, y, w, h, color.getRGB());
        }
        context.drawText(textRenderer, text, x + xTextPadding, y + yTextPadding, textColor.getRGB(), textShadow);
    }

    public enum State {
        NORMAL, HOVERED, PRESSED;
    }

    public enum DrawMode {
        OUTLINE, FILLED, FILLED_WHEN_HOVERED;
    }

    public enum ColorScheme {
        GRAY_TRANSPARENT(() -> new Color(100, 100, 100, 0)),
        GRAY(() -> new Color(150, 150, 150, 150)),
        PRIMARY(ClientConfig.Persistent::getAccentColor);
        @Deprecated
        private static final double BRIGHTER_STRENGTH = .3;
        @Deprecated
        private static final double HOVERED_DARKER_STRENGTH = .1;
        private static final double PRESSED_DARKER_STRENGTH = .3;

        private static final double ALPHA_STRENGTH = .8;
        private final Supplier<Color> normalColor;
        private final Supplier<Color> hoveredColor;

        private final Supplier<Color> pressedColor;

        public Color getColor(State buttonState) {
            return switch (buttonState) {
                case NORMAL -> normalColor.get();
                case HOVERED -> hoveredColor.get();
                case PRESSED -> pressedColor.get();
            };
        }

        ColorScheme(Supplier<Color> color) {
            this.normalColor = color;
//            this.hoveredColor = () -> lerpAlpha(ColorUtil.brighter(color.get(), BRIGHTER_STRENGTH));
            this.hoveredColor = () -> lerpAlpha(ColorUtil.darker(color.get(), HOVERED_DARKER_STRENGTH));
            this.pressedColor = () -> lerpAlpha(ColorUtil.darker(color.get(), PRESSED_DARKER_STRENGTH));
        }

        private static Color lerpAlpha(Color color) {
            return ColorUtil.withAlpha(color, (int) MathUtil.lerp(255, color.getAlpha(), ALPHA_STRENGTH));
        }
    }
}

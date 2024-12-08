package spaetial.gui.element;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public abstract class Element {
    protected final int width;
    protected final int height;

    protected Element(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract void onHover(int dx, int dy);
    public abstract void onClick(int dx, int dy);
    public abstract void onDraw(DrawContext drawContext, TextRenderer textRenderer, float delta, HoverState hoverState);
}

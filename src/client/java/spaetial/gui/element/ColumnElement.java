package spaetial.gui.element;

public class ColumnElement {
    private static final int DEFAULT_PADDING = 5;

    private final int padding;
    private final Element[] children;

    public static ColumnElement create(Element... children) { return create(DEFAULT_PADDING, children); }
    public static ColumnElement create(int padding, Element... children) {
        int width = 0; // TODO
        int height = 0; // TODO
        return new ColumnElement(width, height, padding, children);
    }

    private ColumnElement(int width, int height, int padding, Element[] children) {
        this.padding = padding;
        this.children = children;
    }
}

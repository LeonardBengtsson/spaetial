package spaetial.editing.state;

public enum CopyStateMode {
    CLONE, LINE_STACK, VOLUME_STACK;

    public CopyStateMode cycle() {
        return switch (this) {
            case CLONE -> LINE_STACK;
            case LINE_STACK -> VOLUME_STACK;
            case VOLUME_STACK -> CLONE;
        };
    }
}

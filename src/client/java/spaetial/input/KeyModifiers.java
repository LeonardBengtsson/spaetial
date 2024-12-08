package spaetial.input;

public enum KeyModifiers {
    NONE(false, false, false),
    CTRL(false, true, false),
    SHIFT(true, false, false),
    ALT(false, false, true),
    CTRL_SHIFT(true, true, false),
    CTRL_ALT(false, true, true),
    SHIFT_ALT(true, false, true),
    CTRL_SHIFT_ALT(true, true, true);
    public final boolean shift;
    public final boolean ctrl;
    public final boolean alt;

    KeyModifiers(boolean shift, boolean ctrl, boolean alt) {
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
    }
}

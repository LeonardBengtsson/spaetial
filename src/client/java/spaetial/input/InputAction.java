package spaetial.input;

/**
 * Represents information about how the state of a key or mouse button has been changed.
 * <p>{@link InputAction#BEGIN}: The key went from not being pressed to being pressed
 * <p>{@link InputAction#CONTINUOUS}: The key was and continues to be pressed
 * <p>{@link InputAction#END}: The key went from being pressed to not being pressed
 * <p>{@link InputAction#INVALID}: None of the above/invalid action
 */
public enum InputAction {
    BEGIN(1), CONTINUOUS(2), END(0), INVALID(-1);
    /**
     * The numerical value used by minecraft's internal logic that represents this action, or -1 if none.
     */
    public final int value;
    InputAction(int value) {
        this.value = value;
    }
    public static InputAction fromValue(int type) {
        return switch (type) {
            case 1 -> BEGIN;
            case 2 -> CONTINUOUS;
            case 0 -> END;
            default -> INVALID;
        };
    }
}

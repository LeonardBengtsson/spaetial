package spaetial.editing.selection;

public enum FuzzyConnectionBehaviour {
    /**
     * Defines two blocks to be connected if two of their faces are touching, i.e. if they're adjacent
     */
    FACES,
    /**
     * Defines two blocks to be connected if two of their faces are touching and connected, e.g. a button placed on a
     * wall would be defined as connected, while a lantern placed next to a wall would not be considered connected
     */
    FACES_CONNECTED,
    /**
     * Defines two blocks to be connected if their edges are touching, i.e. if they are placed diagonally adjacent
     */
    EDGES,
    /**
     * Defines two blocks to be connected if two of their corners are touching
     */
    CORNERS;
}

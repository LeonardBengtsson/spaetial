package spaetial.editing.blocks;

import net.minecraft.util.math.Direction;

public record BlockConnectionType(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {
    public static final BlockConnectionType ALL = new BlockConnectionType(true, true, true, true, true, true);
    public static final BlockConnectionType NONE = new BlockConnectionType(false, false, false, false, false, false);

    public boolean isConnectedTowards(Direction direction) {
        return switch (direction) {
            case DOWN -> down;
            case UP -> up;
            case NORTH -> north;
            case SOUTH -> south;
            case WEST -> west;
            case EAST -> east;
        };
    }

    public BlockConnectionType with(Direction direction) {
        return new BlockConnectionType(
            down || direction == Direction.DOWN,
            up || direction == Direction.UP,
            north || direction == Direction.NORTH,
            south || direction == Direction.SOUTH,
            west || direction == Direction.WEST,
            east || direction == Direction.EAST
        );
    }
}

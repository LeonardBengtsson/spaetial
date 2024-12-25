package spaetial.editing.blocks;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

/**
 * An abstract representation of the orientation of a 3D object after applying right-angle rotations and/or flipping it.
 * Consider a 3D person pointing forwards with their right hand:
 *
 * <ul>
 *     <li>
 *         {@code flip} decides whether the person is flipped, pointing with their left hand instead.
 *     </li>
 *     <li>
 *         {@code direction} decides which direction the person is pointing, be it any of the four cardinal directions,
 *         or up or down.
 *     </li>
 *     <li>
 *         {@code rollRotation} decides which direction the person's feet are pointing towards (the secondary direction.
 *         If the {@code direction} is any of the four cardinal directions, the {@code rollRotation} R0 corresponds to
 *         the person's feet pointing down, and if the {@code direction} is up or down, the {@code rollRotation} R0
 *         corresponds to the person's feet pointing south. Any other {@code rollRotation} corresponds to the person
 *         being rotated around their forward axis by the specified amount.
 *     </li>
 * </ul>
 */
public enum RightAngleOrientation {
    SOUTH_DOWN(0, false, Direction.SOUTH, BlockRotation.NONE
            ),
    SOUTH_DOWN_FLIPPED(1, true, Direction.SOUTH, BlockRotation.NONE,
            RightAngleTransformation.FLIP_X),
    SOUTH_LEFT(2, false, Direction.SOUTH, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Z_270),
    SOUTH_LEFT_FLIPPED(3, true, Direction.SOUTH, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.FLIP_X, RightAngleTransformation.ROTATE_Z_270),
    SOUTH_UP(4, false, Direction.SOUTH, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_Z_180),
    SOUTH_UP_FLIPPED(5, true, Direction.SOUTH, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.FLIP_X, RightAngleTransformation.ROTATE_Z_180),
    SOUTH_RIGHT(6, false, Direction.SOUTH, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Z_90),
    SOUTH_RIGHT_FLIPPED(7, true, Direction.SOUTH, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.FLIP_X, RightAngleTransformation.ROTATE_Z_90),
    WEST_DOWN(8, false, Direction.WEST, BlockRotation.NONE,
            RightAngleTransformation.ROTATE_Y_90),
    WEST_DOWN_FLIPPED(9, true, Direction.WEST, BlockRotation.NONE,
            RightAngleTransformation.FLIP_X, RightAngleTransformation.ROTATE_Y_90),
    WEST_LEFT(10, false, Direction.WEST, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.ROTATE_X_90),
    WEST_LEFT_FLIPPED(11, true, Direction.WEST, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_X_90),
    WEST_UP(12, false, Direction.WEST, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.ROTATE_X_180),
    WEST_UP_FLIPPED(13, true, Direction.WEST, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.FLIP_Y),
    WEST_RIGHT(14, false, Direction.WEST, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.ROTATE_X_270),
    WEST_RIGHT_FLIPPED(15, true, Direction.WEST, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_X_270),
    NORTH_DOWN(16, false, Direction.NORTH, BlockRotation.NONE,
            RightAngleTransformation.ROTATE_Y_180),
    NORTH_DOWN_FLIPPED(17, true, Direction.NORTH, BlockRotation.NONE,
            RightAngleTransformation.FLIP_Z),
    NORTH_LEFT(18, false, Direction.NORTH, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_180, RightAngleTransformation.ROTATE_Z_90),
    NORTH_LEFT_FLIPPED(19, true, Direction.NORTH, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Z_90, RightAngleTransformation.FLIP_Z),
    NORTH_UP(20, false, Direction.NORTH, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_X_180),
    NORTH_UP_FLIPPED(21, true, Direction.NORTH, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_X_180, RightAngleTransformation.FLIP_X),
    NORTH_RIGHT(22, false, Direction.NORTH, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_180, RightAngleTransformation.ROTATE_Z_270),
    NORTH_RIGHT_FLIPPED(23, true, Direction.NORTH, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Z_270, RightAngleTransformation.FLIP_Z),
    EAST_DOWN(24, false, Direction.EAST, BlockRotation.NONE,
            RightAngleTransformation.ROTATE_Y_270),
    EAST_DOWN_FLIPPED(25, true, Direction.EAST, BlockRotation.NONE,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.FLIP_Z),
    EAST_LEFT(26, false, Direction.EAST, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.ROTATE_X_270),
    EAST_LEFT_FLIPPED(27, true, Direction.EAST, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_X_270),
    EAST_UP(28, false, Direction.EAST, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.ROTATE_X_180),
    EAST_UP_FLIPPED(29, true, Direction.EAST, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.FLIP_Y),
    EAST_RIGHT(30, false, Direction.EAST, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.ROTATE_X_90),
    EAST_RIGHT_FLIPPED(31, true, Direction.EAST, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_X_90),
    DOWN_DOWN(32, false, Direction.DOWN, BlockRotation.NONE,
            RightAngleTransformation.ROTATE_Y_180, RightAngleTransformation.ROTATE_X_90),
    DOWN_DOWN_FLIPPED(33, true, Direction.DOWN, BlockRotation.NONE,
            RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_X_90),
    DOWN_LEFT(34, false, Direction.DOWN, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.ROTATE_Z_90),
    DOWN_LEFT_FLIPPED(35, true, Direction.DOWN, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_Z_90),
    DOWN_UP(36, false, Direction.DOWN, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_X_270),
    DOWN_UP_FLIPPED(37, true, Direction.DOWN, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.FLIP_X, RightAngleTransformation.ROTATE_X_270),
    DOWN_RIGHT(38, false, Direction.DOWN, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.ROTATE_Z_270),
    DOWN_RIGHT_FLIPPED(39, true, Direction.DOWN, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_Z_270),
    UP_DOWN(40, false, Direction.UP, BlockRotation.NONE,
            RightAngleTransformation.ROTATE_X_90),
    UP_DOWN_FLIPPED(41, true, Direction.UP, BlockRotation.NONE,
            RightAngleTransformation.FLIP_X, RightAngleTransformation.ROTATE_X_90),
    UP_LEFT(42, false, Direction.UP, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.ROTATE_Z_270),
    UP_LEFT_FLIPPED(43, true, Direction.UP, BlockRotation.CLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_270, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_Z_270),
    UP_UP(44, false, Direction.UP, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.ROTATE_Y_180, RightAngleTransformation.ROTATE_X_270),
    UP_UP_FLIPPED(45, true, Direction.UP, BlockRotation.CLOCKWISE_180,
            RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_X_270),
    UP_RIGHT(46, false, Direction.UP, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.ROTATE_Z_90),
    UP_RIGHT_FLIPPED(47, true, Direction.UP, BlockRotation.COUNTERCLOCKWISE_90,
            RightAngleTransformation.ROTATE_Y_90, RightAngleTransformation.FLIP_Z, RightAngleTransformation.ROTATE_Z_90);

    public final int id;
    public final boolean flipped;
    public final Direction localForwards;
    public final Direction localDown;
    public final Direction localRight;
    private final BlockRotation rollRotation;
    public final RightAngleTransformation[] requiredTransformations;

    public static final RightAngleOrientation DEFAULT = SOUTH_DOWN;

    RightAngleOrientation(int id, boolean flipped, Direction forwards, BlockRotation rollRotation, RightAngleTransformation... requiredTransformations) {
        this.id = id;
        this.flipped = flipped;
        this.localForwards = forwards;
        this.localDown = switch (this.localForwards) {
            case DOWN, UP -> rotate(Direction.SOUTH, forwards, rollRotation);
            case NORTH, SOUTH, WEST, EAST -> rotate(Direction.DOWN, forwards, rollRotation);
        };
        this.localRight = rotate(this.localForwards, this.localDown, flipped ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90);
        this.rollRotation = rollRotation;
        this.requiredTransformations = requiredTransformations;
    }

    private static Direction rotate(Direction dir, Direction around, BlockRotation rot) {
        return switch (rot) {
            case NONE -> dir;
            case CLOCKWISE_180 -> dir.getAxis() == around.getAxis() ? dir : dir.getOpposite();
            case CLOCKWISE_90 -> {
                // TODO test that this is correct
                var axis = around.getAxis();
                yield around.getDirection().offset() == 1 ? dir.rotateClockwise(axis) : dir.rotateCounterclockwise(axis);
            }
            case COUNTERCLOCKWISE_90 -> {
                var axis = around.getAxis();
                yield around.getDirection().offset() == 1 ? dir.rotateCounterclockwise(axis) : dir.rotateClockwise(axis);
            }
        };
    }

    private static final RightAngleOrientation[] ID_SORTED = {
            SOUTH_DOWN, SOUTH_DOWN_FLIPPED, SOUTH_LEFT, SOUTH_LEFT_FLIPPED, SOUTH_UP, SOUTH_UP_FLIPPED, SOUTH_RIGHT, SOUTH_RIGHT_FLIPPED,
            WEST_DOWN, WEST_DOWN_FLIPPED, WEST_LEFT, WEST_LEFT_FLIPPED, WEST_UP, WEST_UP_FLIPPED, WEST_RIGHT, WEST_RIGHT_FLIPPED,
            NORTH_DOWN, NORTH_DOWN_FLIPPED, NORTH_LEFT, NORTH_LEFT_FLIPPED, NORTH_UP, NORTH_UP_FLIPPED, NORTH_RIGHT, NORTH_RIGHT_FLIPPED,
            EAST_DOWN, EAST_DOWN_FLIPPED, EAST_LEFT, EAST_LEFT_FLIPPED, EAST_UP, EAST_UP_FLIPPED, EAST_RIGHT, EAST_RIGHT_FLIPPED,
            DOWN_DOWN, DOWN_DOWN_FLIPPED, DOWN_LEFT, DOWN_LEFT_FLIPPED, DOWN_UP, DOWN_UP_FLIPPED, DOWN_RIGHT, DOWN_RIGHT_FLIPPED,
            UP_DOWN, UP_DOWN_FLIPPED, UP_LEFT, UP_LEFT_FLIPPED, UP_UP, UP_UP_FLIPPED, UP_RIGHT, UP_RIGHT_FLIPPED
    };
    public static RightAngleOrientation get(int id) {
        assert id < ID_SORTED.length;
        return ID_SORTED[id];
    }

    public static RightAngleOrientation get(boolean flipped, Direction direction, BlockRotation rollRotation) {
        int d = switch (direction) {
            case SOUTH -> 0;
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            case DOWN -> 4;
            case UP -> 5;
        };
        int r = switch (rollRotation) {
            case NONE -> 0;
            case CLOCKWISE_90 -> 1;
            case CLOCKWISE_180 -> 2;
            case COUNTERCLOCKWISE_90 -> 3;
        };
        return RightAngleOrientation.get((d << 3) | (r << 1) | (flipped ? 0x1 : 0x0));
    }

    private static final RightAngleOrientation[] X_ROT_90_MAP = {
            UP_DOWN, UP_DOWN_FLIPPED, UP_LEFT, UP_LEFT_FLIPPED, UP_UP, UP_UP_FLIPPED, UP_RIGHT, UP_RIGHT_FLIPPED,
            WEST_LEFT, WEST_LEFT_FLIPPED, WEST_UP, WEST_UP_FLIPPED, WEST_RIGHT, WEST_RIGHT_FLIPPED, WEST_DOWN, WEST_DOWN_FLIPPED,
            DOWN_DOWN, DOWN_DOWN_FLIPPED, DOWN_LEFT, DOWN_LEFT_FLIPPED, DOWN_UP, DOWN_UP_FLIPPED, DOWN_RIGHT, DOWN_RIGHT_FLIPPED,
            EAST_RIGHT, EAST_RIGHT_FLIPPED, EAST_DOWN, EAST_DOWN_FLIPPED, EAST_LEFT, EAST_LEFT_FLIPPED, EAST_UP, EAST_UP_FLIPPED,
            SOUTH_UP, SOUTH_UP_FLIPPED, SOUTH_RIGHT, SOUTH_RIGHT_FLIPPED, SOUTH_DOWN, SOUTH_DOWN_FLIPPED, SOUTH_LEFT, SOUTH_LEFT_FLIPPED,
            NORTH_UP, NORTH_UP_FLIPPED, NORTH_RIGHT, NORTH_RIGHT_FLIPPED, NORTH_DOWN, NORTH_DOWN_FLIPPED, NORTH_LEFT, NORTH_LEFT_FLIPPED
    };

    private static final RightAngleOrientation[] Y_ROT_90_MAP = {
            WEST_DOWN, WEST_DOWN_FLIPPED, WEST_LEFT, WEST_LEFT_FLIPPED, WEST_UP, WEST_UP_FLIPPED, WEST_RIGHT, WEST_RIGHT_FLIPPED,
            NORTH_DOWN, NORTH_DOWN_FLIPPED, NORTH_LEFT, NORTH_LEFT_FLIPPED, NORTH_UP, NORTH_UP_FLIPPED, NORTH_RIGHT, NORTH_RIGHT_FLIPPED,
            EAST_DOWN, EAST_DOWN_FLIPPED, EAST_LEFT, EAST_LEFT_FLIPPED, EAST_UP, EAST_UP_FLIPPED, EAST_RIGHT, EAST_RIGHT_FLIPPED,
            SOUTH_DOWN, SOUTH_DOWN_FLIPPED, SOUTH_LEFT, SOUTH_LEFT_FLIPPED, SOUTH_UP, SOUTH_UP_FLIPPED, SOUTH_RIGHT, SOUTH_RIGHT_FLIPPED,
            DOWN_LEFT, DOWN_LEFT_FLIPPED, DOWN_UP, DOWN_UP_FLIPPED, DOWN_RIGHT, DOWN_RIGHT_FLIPPED, DOWN_DOWN, DOWN_DOWN_FLIPPED,
            UP_RIGHT, UP_RIGHT_FLIPPED, UP_DOWN, UP_DOWN_FLIPPED, UP_LEFT, UP_LEFT_FLIPPED, UP_UP, UP_UP_FLIPPED
    };

    private static final RightAngleOrientation[] Z_ROT_90_MAP = {
            SOUTH_RIGHT, SOUTH_RIGHT_FLIPPED, SOUTH_DOWN, SOUTH_DOWN_FLIPPED, SOUTH_LEFT, SOUTH_LEFT_FLIPPED, SOUTH_UP, SOUTH_UP_FLIPPED,
            UP_DOWN, UP_DOWN_FLIPPED, UP_LEFT, UP_LEFT_FLIPPED, UP_UP, UP_UP_FLIPPED, UP_RIGHT, UP_RIGHT_FLIPPED,
            NORTH_LEFT, NORTH_LEFT_FLIPPED, NORTH_UP, NORTH_UP_FLIPPED, NORTH_RIGHT, NORTH_RIGHT_FLIPPED, NORTH_DOWN, NORTH_DOWN_FLIPPED,
            DOWN_UP, DOWN_UP_FLIPPED, DOWN_RIGHT, DOWN_RIGHT_FLIPPED, DOWN_DOWN, DOWN_DOWN_FLIPPED, DOWN_LEFT, DOWN_LEFT_FLIPPED,
            WEST_UP, WEST_UP_FLIPPED, WEST_RIGHT, WEST_RIGHT_FLIPPED, WEST_DOWN, WEST_DOWN_FLIPPED, WEST_LEFT, WEST_LEFT_FLIPPED,
            EAST_DOWN, EAST_DOWN_FLIPPED, EAST_LEFT, EAST_LEFT_FLIPPED, EAST_UP, EAST_UP_FLIPPED, EAST_RIGHT, EAST_RIGHT_FLIPPED
    };

    private static final RightAngleOrientation[] X_FLIP_MAP = {
            NORTH_DOWN_FLIPPED, NORTH_DOWN, NORTH_RIGHT_FLIPPED, NORTH_RIGHT, NORTH_UP_FLIPPED, NORTH_UP, NORTH_LEFT_FLIPPED, NORTH_LEFT,
            WEST_DOWN_FLIPPED, WEST_DOWN, WEST_RIGHT_FLIPPED, WEST_RIGHT, WEST_UP_FLIPPED, WEST_UP, WEST_LEFT_FLIPPED, WEST_LEFT,
            SOUTH_DOWN_FLIPPED, SOUTH_DOWN, SOUTH_RIGHT_FLIPPED, SOUTH_RIGHT, SOUTH_UP_FLIPPED, SOUTH_UP, SOUTH_LEFT_FLIPPED, SOUTH_LEFT,
            EAST_DOWN_FLIPPED, EAST_DOWN, EAST_RIGHT_FLIPPED, EAST_RIGHT, EAST_UP_FLIPPED, EAST_UP, EAST_LEFT_FLIPPED, EAST_LEFT,
            DOWN_UP_FLIPPED, DOWN_UP, DOWN_LEFT_FLIPPED, DOWN_LEFT, DOWN_DOWN_FLIPPED, DOWN_DOWN, DOWN_RIGHT_FLIPPED, DOWN_RIGHT,
            UP_UP_FLIPPED, UP_UP, UP_LEFT_FLIPPED, UP_LEFT, UP_DOWN_FLIPPED, UP_DOWN, UP_RIGHT_FLIPPED, UP_RIGHT
    };

    private static final RightAngleOrientation[] X_ROT_180_MAP;
    private static final RightAngleOrientation[] X_ROT_270_MAP;

    private static final RightAngleOrientation[] Y_ROT_180_MAP;
    private static final RightAngleOrientation[] Y_ROT_270_MAP;
    private static final RightAngleOrientation[] Y_FLIP_MAP;

    private static final RightAngleOrientation[] Z_ROT_180_MAP;
    private static final RightAngleOrientation[] Z_ROT_270_MAP;
    private static final RightAngleOrientation[] Z_FLIP_MAP;

    static {
        X_ROT_180_MAP = new RightAngleOrientation[48];
        X_ROT_270_MAP = new RightAngleOrientation[48];
        Y_ROT_180_MAP = new RightAngleOrientation[48];
        Y_ROT_270_MAP = new RightAngleOrientation[48];
        Z_ROT_180_MAP = new RightAngleOrientation[48];
        Z_ROT_270_MAP = new RightAngleOrientation[48];
        Y_FLIP_MAP = new RightAngleOrientation[48];
        Z_FLIP_MAP = new RightAngleOrientation[48];

        for (int i = 0; i < 48; i++) {
            // rotate by 180 by applying 90-degree rotation twice
            // rotate by 270 by applying 180-degree and then 90-degree rotation
            // flip y: rotate z 90, flip x, rotate z 270
            // flip z: rotate y 90, flip x, rotate y 270

            X_ROT_180_MAP[i] = X_ROT_90_MAP[X_ROT_90_MAP[i].id];
            X_ROT_270_MAP[i] = X_ROT_90_MAP[X_ROT_180_MAP[i].id];
            Y_ROT_180_MAP[i] = Y_ROT_90_MAP[Y_ROT_90_MAP[i].id];
            Y_ROT_270_MAP[i] = Y_ROT_90_MAP[Y_ROT_180_MAP[i].id];
            Z_ROT_180_MAP[i] = Z_ROT_90_MAP[Z_ROT_90_MAP[i].id];
            Z_ROT_270_MAP[i] = Z_ROT_90_MAP[Z_ROT_180_MAP[i].id];
            Y_FLIP_MAP[i] = Z_ROT_270_MAP[X_FLIP_MAP[Z_ROT_90_MAP[i].id].id];
            Z_FLIP_MAP[i] = Y_ROT_270_MAP[X_FLIP_MAP[Y_ROT_90_MAP[i].id].id];
        }
    }

    public RightAngleOrientation rotateX90() { return RightAngleOrientation.X_ROT_90_MAP[this.id]; }
    public RightAngleOrientation rotateX180() { return RightAngleOrientation.X_ROT_180_MAP[this.id]; }
    public RightAngleOrientation rotateX270() { return RightAngleOrientation.X_ROT_270_MAP[this.id]; }
    public RightAngleOrientation rotateY90() { return RightAngleOrientation.Y_ROT_90_MAP[this.id]; }
    public RightAngleOrientation rotateY180() { return RightAngleOrientation.Y_ROT_180_MAP[this.id]; }
    public RightAngleOrientation rotateY270() { return RightAngleOrientation.Y_ROT_270_MAP[this.id]; }
    public RightAngleOrientation rotateZ90() { return RightAngleOrientation.Z_ROT_90_MAP[this.id]; }
    public RightAngleOrientation rotateZ180() { return RightAngleOrientation.Z_ROT_180_MAP[this.id]; }
    public RightAngleOrientation rotateZ270() { return RightAngleOrientation.Z_ROT_270_MAP[this.id]; }
    public RightAngleOrientation flipX() { return RightAngleOrientation.X_FLIP_MAP[this.id]; }
    public RightAngleOrientation flipY() { return RightAngleOrientation.Y_FLIP_MAP[this.id]; }
    public RightAngleOrientation flipZ() { return RightAngleOrientation.Z_FLIP_MAP[this.id]; }
}

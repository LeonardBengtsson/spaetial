package spaetial.editing.blocks;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.function.UnaryOperator;

/**
 * Represents a right-angle rotation or flip applied to a 3D object.
 */
public enum RightAngleTransformation {
    ROTATE_Y_90(RightAngleTransformation::rotY90, Type.ROTATION_Y, Direction.Axis.Y),
    ROTATE_Y_180(RightAngleTransformation::rotY180, Type.ROTATION_Y, Direction.Axis.Y),
    ROTATE_Y_270(RightAngleTransformation::rotY270, Type.ROTATION_Y, Direction.Axis.Y),

    ROTATE_X_90(RightAngleTransformation::rotX90, Type.ROTATION_SIDE, Direction.Axis.X),
    ROTATE_X_270(RightAngleTransformation::rotX270, Type.ROTATION_SIDE, Direction.Axis.X),
    ROTATE_Z_90(RightAngleTransformation::rotZ90, Type.ROTATION_SIDE, Direction.Axis.Z),
    ROTATE_Z_270(RightAngleTransformation::rotZ270, Type.ROTATION_SIDE, Direction.Axis.Z),

    ROTATE_X_180(RightAngleTransformation::rotX180, Type.ROTATION_UPSIDE_DOWN, Direction.Axis.X),
    ROTATE_Z_180(RightAngleTransformation::rotZ180, Type.ROTATION_UPSIDE_DOWN, Direction.Axis.Z),

    FLIP_X(RightAngleTransformation::flipX, Type.FLIP_HORIZONTAL, Direction.Axis.X),
    FLIP_Z(RightAngleTransformation::flipZ, Type.FLIP_HORIZONTAL, Direction.Axis.Z),

    FLIP_Y(RightAngleTransformation::flipY, Type.FLIP_Y, Direction.Axis.Y);

    public final Type type;
    public final Direction.Axis axis;
    private final UnaryOperator<Vec3i> func;

    RightAngleTransformation(UnaryOperator<Vec3i> func, Type type, Direction.Axis axis) {
        this.func = func;
        this.type = type;
        this.axis = axis;
    }

    public enum Type {
        ROTATION_Y,
        ROTATION_SIDE,
        ROTATION_UPSIDE_DOWN,
        FLIP_HORIZONTAL,
        FLIP_Y;
    }

    public Vec3i apply(Vec3i p) {
        return func.apply(p);
    }

    private static Vec3i rotX90(Vec3i p) { return new Vec3i(p.getX(), -p.getZ(), p.getY()); }
    private static Vec3i rotX180(Vec3i p) { return new Vec3i(p.getX(), -p.getY(), -p.getZ()); }
    private static Vec3i rotX270(Vec3i p) { return new Vec3i(p.getX(), p.getZ(), -p.getY()); }
    private static Vec3i rotY90(Vec3i p) { return new Vec3i(-p.getZ(), p.getY(), p.getX()); }
    private static Vec3i rotY180(Vec3i p) { return new Vec3i(-p.getX(), p.getY(), -p.getZ()); }
    private static Vec3i rotY270(Vec3i p) { return new Vec3i(p.getZ(), p.getY(), -p.getX()); }
    private static Vec3i rotZ90(Vec3i p) { return new Vec3i(p.getY(), -p.getX(), p.getZ()); }
    private static Vec3i rotZ180(Vec3i p) { return new Vec3i(-p.getX(), -p.getY(), p.getZ()); }
    private static Vec3i rotZ270(Vec3i p) { return new Vec3i(-p.getY(), p.getX(), p.getZ()); }
    private static Vec3i flipX(Vec3i p) { return new Vec3i(-p.getX(), p.getY(), p.getZ()); }
    private static Vec3i flipY(Vec3i p) { return new Vec3i(p.getX(), -p.getY(), p.getZ()); }
    private static Vec3i flipZ(Vec3i p) { return new Vec3i(p.getX(), p.getY(), -p.getZ()); }
}
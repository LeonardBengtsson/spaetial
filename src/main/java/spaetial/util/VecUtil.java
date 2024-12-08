package spaetial.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;

public final class VecUtil {
    private VecUtil() {}

    public static Vec3i signum(Vec3d vec) {
        return new Vec3i(
            (int) Math.signum(vec.x),
            (int) Math.signum(vec.y),
            (int) Math.signum(vec.z)
        );
    }

    public static BlockPos floor(Vec3d vec) {
        return new BlockPos((int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z));
    }

    public static Vec3i componentWiseDivision(Vec3i a, Vec3i b) {
        return new Vec3i(
            b.getX() == 0 ? 0 : a.getX() / b.getX(),
            b.getY() == 0 ? 0 : a.getY() / b.getY(),
            b.getZ() == 0 ? 0 : a.getZ() / b.getZ()
        );
    }

    public static Vec3d cast(Vec3i vec) {
        return new Vec3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec3i withAxis(Vec3i vec, Direction.Axis axis, int value) {
        return switch (axis) {
            case X -> new Vec3i(value, vec.getY(), vec.getZ());
            case Y -> new Vec3i(vec.getX(), value, vec.getZ());
            case Z -> new Vec3i(vec.getX(), vec.getY(), value);
        };
    }

    public static Vec3i scrollVector(PlayerEntity player, Vec3i scrollDirections) {
        var result = Vec3i.ZERO;
        var rot = player.getRotationClient();
        if (scrollDirections.getX() != 0) {
            var vec = Vec3d.fromPolar(0, rot.y + 90);
            var dir = Direction.getFacing(vec);
            result = result.add(dir.getVector().multiply(scrollDirections.getX()));
        }
        if (scrollDirections.getY() != 0) {
            var vec = Vec3d.fromPolar(rot.x - 90, rot.y);
            var dir = Direction.getFacing(vec);
            result = result.add(dir.getVector().multiply(scrollDirections.getY()));
        }
        if (scrollDirections.getZ() != 0) {
            var vec = Vec3d.fromPolar(rot);
            var dir = Direction.getFacing(vec);
            result = result.add(dir.getVector().multiply(scrollDirections.getZ()));
        }
        return result;
    }

}

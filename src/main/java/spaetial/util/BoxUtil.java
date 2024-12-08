package spaetial.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import spaetial.util.math.RaycastUtil;

public final class BoxUtil {
    private BoxUtil() {}

    public static final double DEFAULT_OFFSET = .01;

    public static Box toBox(BlockBox box) {
        return new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX() + 1, box.getMaxY() + 1, box.getMaxZ() + 1);
    }

    public static Box viewAdjustBox(Box box, Vec3d pov) {
        return viewAdjustBox(box, pov, DEFAULT_OFFSET);
    }

    public static Box viewAdjustBox(Box box, Vec3d pov, double offset) {
        double x1 = box.minX;
        double y1 = box.minY;
        double z1 = box.minZ;
        double x2 = box.maxX;
        double y2 = box.maxY;
        double z2 = box.maxZ;

        x1 += offset * (pov.x > x1 ? 1 : -1);
        y1 += offset * (pov.y > y1 ? 1 : -1);
        z1 += offset * (pov.z > z1 ? 1 : -1);
        x2 += offset * (pov.x > x2 ? 1 : -1);
        y2 += offset * (pov.y > y2 ? 1 : -1);
        z2 += offset * (pov.z > z2 ? 1 : -1);

        return new Box(x1, y1, z1, x2, y2, z2);
    }

    public static BlockPos minPos(BlockBox box) {
        return new BlockPos(box.getMinX(), box.getMinY(), box.getMinZ());
    }

    public static BlockPos maxPos(BlockBox box) {
        return new BlockPos(box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }

    public static BlockPos getCorner(BlockBox box, Vec3i corner) {
        var min = minPos(box);
        var max = maxPos(box);
        return new BlockPos(
            corner.getX() > 0 ? max.getX() : min.getX(),
            corner.getY() > 0 ? max.getY() : min.getY(),
            corner.getZ() > 0 ? max.getZ() : min.getZ()
        );
    }

    public static int largestAxisExcept(BlockBox box, Direction.Axis axis) {
        return switch (axis) {
            case X -> Math.max(box.getBlockCountY(), box.getBlockCountZ());
            case Y -> Math.max(box.getBlockCountX(), box.getBlockCountZ());
            case Z -> Math.max(box.getBlockCountX(), box.getBlockCountY());
        };
    }

    public static BlockBox offset(BlockBox box, Vec3i offset) {
        return box.offset(offset.getX(), offset.getY(), offset.getZ());
    }

    public static BlockBox extend(BlockBox box, BlockPos pos) {
        return new BlockBox(
            Math.min(pos.getX(), box.getMinX()),
            Math.min(pos.getY(), box.getMinY()),
            Math.min(pos.getZ(), box.getMinZ()),
            Math.max(pos.getX(), box.getMaxX()),
            Math.max(pos.getY(), box.getMaxY()),
            Math.max(pos.getZ(), box.getMaxZ())
        );
    }

    public static BlockBox moveTo(BlockBox box, BlockPos minPos) {
        return fromMinAndDimensions(minPos, box.getDimensions());
    }

    public static BlockBox fromMinAndDimensions(BlockPos minPos, Vec3i dimensions) {
        return new BlockBox(
            minPos.getX(),
            minPos.getY(),
            minPos.getZ(),
            minPos.getX() + dimensions.getX() - 1,
            minPos.getY() + dimensions.getY() - 1,
            minPos.getZ() + dimensions.getZ() - 1
        );
    }

    public static BlockBox fromMinAndMax(BlockPos pos1, BlockPos pos2) {
        return new BlockBox(
            pos1.getX(), pos1.getY(), pos1.getZ(),
            pos2.getX(), pos2.getY(), pos2.getZ()
        );
    }

    public static BlockBox fromPositions(BlockPos pos1, BlockPos pos2) {
        return new BlockBox(
            Math.min(pos1.getX(), pos2.getX()),
            Math.min(pos1.getY(), pos2.getY()),
            Math.min(pos1.getZ(), pos2.getZ()),
            Math.max(pos1.getX(), pos2.getX()),
            Math.max(pos1.getY(), pos2.getY()),
            Math.max(pos1.getZ(), pos2.getZ())
        );
    }

    public static int getVolume(BlockBox box) {
        return box.getBlockCountX() * box.getBlockCountY() * box.getBlockCountZ();
    }

    public static BlockBox resizeAction(BlockBox box, Vec3i dirs, Vec3i vec, boolean otherSide) {
        int x_min = box.getMinX();
        int y_min = box.getMinY();
        int z_min = box.getMinZ();
        int x_max = box.getMaxX();
        int y_max = box.getMaxY();
        int z_max = box.getMaxZ();

        if (dirs.getX() < 0 == otherSide) {
            x_min = Math.min(x_max, x_min + vec.getX());
        } else {
            x_max = Math.max(x_min, x_max + vec.getX());
        }
        if (dirs.getY() < 0 == otherSide) {
            y_min = Math.min(y_max, y_min + vec.getY());
        } else {
            y_max = Math.max(y_min, y_max + vec.getY());
        }
        if (dirs.getZ() < 0 == otherSide) {
            z_min = Math.min(z_max, z_min + vec.getZ());
        } else {
            z_max = Math.max(z_min, z_max + vec.getZ());
        }
        return new BlockBox(x_min, y_min, z_min, x_max, y_max, z_max);
    }

    public static BlockPos boxPositioning(PlayerEntity player, double maxRange, boolean surface, Vec3i dimensions) {
        var pos = RaycastUtil.raycastOrCurrentPos(player, maxRange, surface, false, RaycastContext.ShapeType.OUTLINE);
        return new BlockPos(
            pos.getX() - dimensions.getX() / 2,
            pos.getY(),
            pos.getZ() - dimensions.getZ() / 2
        );
    }
}

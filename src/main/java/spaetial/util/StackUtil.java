package spaetial.util;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.function.BiConsumer;

public final class StackUtil {
    private StackUtil() {}

    public static void iterateVecInclusiveYXZ(Vec3i vec, BiConsumer<Integer, Vec3i> consumer) {
        assert vec.getX() >= 0 && vec.getY() >= 0 && vec.getZ() >= 0;
        int index = 0;
        for (int j = 0; j <= vec.getY(); j++) {
            for (int i = 0; i <= vec.getX(); i++) {
                for (int k = 0; k <= vec.getZ(); k++) {
                    consumer.accept(index, new Vec3i(i, j, k));
                    index++;
                }
            }
        }
    }

    public static BlockBox lineStackBox(BlockBox origin, int stackSize, Vec3i spacing) {
        var sx = (stackSize - 1) * spacing.getX();
        var sy = (stackSize - 1) * spacing.getY();
        var sz = (stackSize - 1) * spacing.getZ();
        var x1 = origin.getMinX() + Math.min(sx, 0);
        var y1 = origin.getMinY() + Math.min(sy, 0);
        var z1 = origin.getMinZ() + Math.min(sz, 0);
        var x2 = origin.getMaxX() + Math.max(sx, 0);
        var y2 = origin.getMaxY() + Math.max(sy, 0);
        var z2 = origin.getMaxZ() + Math.max(sz, 0);
        return new BlockBox(
            Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
            Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
        );
    }

    public static void lineStackIterate(BlockBox origin, int stackSize, Vec3i spacing, boolean skipZero, BiConsumer<Integer, BlockBox> consumer) {
        var sx = spacing.getX();
        var sy = spacing.getY();
        var sz = spacing.getZ();
        for (int i = skipZero ? 1 : 0; i < stackSize; i++) {
            consumer.accept(
                i,
                origin.offset(
                    i * sx,
                    i * sy,
                    i * sz
                )
            );
        }
    }

    public static BlockBox volumeStackBox(BlockBox origin, Vec3i stackSize, Vec3i spacing) {
        var sx = stackSize.getX() * (origin.getBlockCountX() + spacing.getX());
        var sy = stackSize.getY() * (origin.getBlockCountY() + spacing.getY());
        var sz = stackSize.getZ() * (origin.getBlockCountZ() + spacing.getZ());
        var x1 = origin.getMinX() + Math.min(sx, 0);
        var y1 = origin.getMinY() + Math.min(sy, 0);
        var z1 = origin.getMinZ() + Math.min(sz, 0);
        var x2 = origin.getMaxX() + Math.max(sx, 0);
        var y2 = origin.getMaxY() + Math.max(sy, 0);
        var z2 = origin.getMaxZ() + Math.max(sz, 0);
        return new BlockBox(
                Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
        );
    }

    public static void volumeStackIterate(BlockBox origin, Vec3i stackSize, Vec3i spacing, boolean skipZero, BiConsumer<Integer, BlockBox> consumer) {
        var sx = origin.getBlockCountX() + spacing.getX();
        var sy = origin.getBlockCountY() + spacing.getY();
        var sz = origin.getBlockCountZ() + spacing.getZ();
        var ox = sx * Math.min(stackSize.getX(), 0);
        var oy = sy * Math.min(stackSize.getY(), 0);
        var oz = sz * Math.min(stackSize.getZ(), 0);
        var size = new Vec3i(
                Math.abs(stackSize.getX()),
                Math.abs(stackSize.getY()),
                Math.abs(stackSize.getZ())
        );
        iterateVecInclusiveYXZ(size, (index, pos) -> {
            if (skipZero && pos == Vec3i.ZERO) return;
            consumer.accept(
                index,
                origin.offset(
                    ox + sx * pos.getX(),
                    oy + sy * pos.getY(),
                    oz + sz * pos.getZ()
                )
            );
        });
    }
}

package spaetial.util.input;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import spaetial.util.BoxUtil;

@Deprecated
public final class ScrollUtil {
    private ScrollUtil() {}

    /**
     * @return A rounded scroll amount value
     */
    public static int intScrollAmount(double scrollAmount) {
        return (int) (Math.signum(scrollAmount) * Math.ceil(Math.abs(scrollAmount)));
    }

    /**
     * @return A vector representing the direction the player is facing and whatever direction they are scrolling in
     */
    public static Vec3i getScrollVector(PlayerEntity player, double scrollAmount) {
        Vec3d rotationVec = player.getRotationVec(0);
        Direction direction = Direction.getFacing(rotationVec.x, rotationVec.y, rotationVec.z);
        return direction.getVector().multiply(intScrollAmount(scrollAmount));
    }

    public static DirectionAndVector getScrollDirectionAndVector(PlayerEntity player, double scrollAmount) {
        Vec3d rotationVec = player.getRotationVec(0);
        Direction direction = Direction.getFacing(rotationVec.x, rotationVec.y, rotationVec.z);
        return new DirectionAndVector(direction, direction.getVector().multiply(intScrollAmount(scrollAmount)));
    }

    public static BlockBox resizeBoxAlternative(PlayerEntity player, BlockBox box, double scrollAmount) {
        var dirAndVec = getScrollDirectionAndVector(player, scrollAmount);
        Direction dir = dirAndVec.direction();
        Direction.Axis axis = dir.getAxis();
        Vec3i vec = dirAndVec.vec();

        BlockPos min = BoxUtil.minPos(box);
        BlockPos max = BoxUtil.maxPos(box);

        int componentMin = min.getComponentAlongAxis(axis);
        int componentMax = max.getComponentAlongAxis(axis);
        int componentPlayer = player.getBlockPos().getComponentAlongAxis(axis);

        boolean moveMaxPos;
        if (componentPlayer < componentMin) {
            moveMaxPos = false;
        } else if (componentPlayer <= componentMax) {
            moveMaxPos = dir.getDirection() == Direction.AxisDirection.POSITIVE;
        } else {
            moveMaxPos = true;
        }

        if (componentMin == componentMax && moveMaxPos == vec.getComponentAlongAxis(axis) < 0) return box;

        if (moveMaxPos) {
            max = max.add(vec);
        } else {
            min = min.add(vec);
        }
        return BoxUtil.fromMinAndMax(min, max);
    }

    public record DirectionAndVector(Direction direction, Vec3i vec) { }
}

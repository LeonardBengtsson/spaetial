package spaetial.util.math;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spaetial.util.VecUtil;

public final class RaycastUtil {
    private RaycastUtil() {}

    /**
     * @param maxRange  The range, in blocks, that the raycast will look for collisions until defaulting to the player's position
     * @param surface   Whether to return the position of the block outside the hit surface, instead of the hit block
     * @param hitFluids Whether to count fluids as hittable blocks, instead of going through them
     * @param shapeType Decides what counts as hitting a block. See {@link RaycastContext.ShapeType}
     * @return The position of the hit block according to the specified arguments, or the player's block position if there was no hit
     *
     * @see RaycastContext.ShapeType
     */
    public static BlockPos raycastOrCurrentPos(PlayerEntity player, double maxRange, boolean surface, boolean hitFluids, RaycastContext.ShapeType shapeType) {
        BlockHitResult hit = raycast(player, maxRange, hitFluids, shapeType);
        return hit.getType() == HitResult.Type.BLOCK ?
                (surface ? hit.getBlockPos().offset(hit.getSide())
                        : hit.getBlockPos()) : player.getBlockPos();
    }

    /**
     * @param maxRange  The range, in blocks, that the raycast will look for collisions until defaulting to the player's position
     * @param surface   Whether to return the position of the block outside the hit surface, instead of the hit block
     * @param hitFluids Whether to count fluids as hittable blocks, instead of going through them
     * @param shapeType Decides what counts as hitting a block. See {@link RaycastContext.ShapeType}
     * @return The position of the hit block according to the specified arguments, or the position at the specified {@code maxRange}
     *
     * @see RaycastContext.ShapeType
     */
    public static BlockPos raycastOrMaxRange(PlayerEntity player, double maxRange, boolean surface, boolean hitFluids, RaycastContext.ShapeType shapeType) {
        return raycastOrAtRange(player, maxRange, maxRange, surface, hitFluids, shapeType);
    }

    /**
     * @param maxRange  The range, in blocks, that the raycast will look for collisions until defaulting to the player's position
     * @param surface   Whether to return the position of the block outside the hit surface, instead of the hit block
     * @param hitFluids Whether to count fluids as hittable blocks, instead of going through them
     * @param shapeType Decides what counts as hitting a block. See {@link RaycastContext.ShapeType}
     * @return The position of the hit block according to the specified arguments, or the position at the specified {@code rangeIfMiss}
     *
     * @see RaycastContext.ShapeType
     */
    public static BlockPos raycastOrAtRange(PlayerEntity player, double maxRange, double rangeIfMiss, boolean surface, boolean hitFluids, RaycastContext.ShapeType shapeType) {
        BlockHitResult hit = raycast(player, maxRange, hitFluids, shapeType);
        return hit.getType() == HitResult.Type.BLOCK
                ? (surface ? hit.getBlockPos().offset(hit.getSide()) : hit.getBlockPos())
                : VecUtil.floor(player.getClientCameraPosVec(0).add(player.getRotationVecClient().multiply(rangeIfMiss)));
    }

    /**
     * @return A {@code BlockHitResult} based on raycasting at the player's block reach (5 blocks in creative, or 4.5 in survival/adventure)
     *
     * @see BlockHitResult
     */
    public static BlockHitResult raycastBlockReach(PlayerEntity player) {
        double range = (player.isCreative() || player.isSpectator()) ? 5 : 4.5;
        return raycast(player, range, false, RaycastContext.ShapeType.OUTLINE);
    }

    /**
     * @return
     */
    public static BlockPos raycastOrAtBlockReach(PlayerEntity player, double maxRange, boolean surface, boolean hitFluids, RaycastContext.ShapeType shapeType) {
        return raycastOrAtRange(player, maxRange, (player.isCreative() || player.isSpectator()) ? 5 : 4.5, surface, hitFluids, shapeType);
    }

    /**
     * @param maxRange  The range, in blocks, that the raycast will look for collisions until defaulting to the player's position
     * @param hitFluids Whether to count fluids as hittable blocks, instead of going through them
     * @param shapeType Decides what counts as hitting a block. See {@link RaycastContext.ShapeType}
     * @return A {@code BlockHitResult} based on the specified arguments
     *
     * @see BlockHitResult
     */
    public static BlockHitResult raycast(PlayerEntity player, double maxRange, boolean hitFluids, RaycastContext.ShapeType shapeType) {
        World world = player.getWorld();
        Vec3d start = player.getClientCameraPosVec(0);
        return world.raycast(new RaycastContext(
                start,
                start.add(player.getRotationVecClient().multiply(maxRange)),
                shapeType,
                hitFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                player
        ));
    }

    /**
     * Calculates the intersection between a ray cast from the player and a plane, given the plane's normal vector and
     * an arbitrary point which lies in the plane
     *
     * @param pointInPlane Any point lying in the plane
     * @param normal       The plane's normal vector, in normalized form
     * @param hitBehind    Decides how to handle intersections that are behind the player
     * @return             The nearest point of intersection, or if there is no intersection or the intersection is
     *                     behind the player and {@code hitBehind} is set to {@link HitBehindBehaviour#MISS}, {@code null}
     *
     * @see HitBehindBehaviour
     */
    public static @Nullable Vec3d raycastPlane(PlayerEntity player, Vec3d pointInPlane, Vec3d normal, @NotNull HitBehindBehaviour hitBehind) {
        Vec3d pos = player.getClientCameraPosVec(0);
        Vec3d dir = player.getRotationVecClient();

        // L(t) = pos + t * dir
        // (P - pointInPlane) · normal = 0
        // (L(t) - pointInPlane) · normal = 0
        // t = ((pointInPlane - pos) · normal) / (dir · normal)

        double dividend = pointInPlane.subtract(pos).dotProduct(normal);
        if (dividend == 0) return pos; // dir lies in plane
        double divisor = dir.dotProduct(normal);
        if (divisor == 0) return null; // no intersection
        double t = dividend / divisor;
        if (t < 0) { // intersection behind player
            switch (hitBehind) {
                case MISS -> { return null; }
                case PROJECT -> { return pos.subtract(normal.multiply(dividend)); }
            }
        }
        return pos.add(dir.multiply(t));
    }

    public enum HitBehindBehaviour {
        /**
         * Return null if the intersection is behind the player
         */
        MISS,

        /**
         * Return the closest point on the plane if the intersection is behind the player
         */
        PROJECT,

        /**
         * Return the intersection point even if it is behind the player
         */
        HIT
    }
}

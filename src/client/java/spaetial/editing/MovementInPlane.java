package spaetial.editing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.render.RenderUtil;
import spaetial.util.BoxUtil;
import spaetial.util.VecUtil;
import spaetial.util.math.RaycastUtil;

import java.awt.*;

public class MovementInPlane {
    private static final double BOX_GRID_SIZE_MULTIPLIER = 2.5;
    public final Vec3d origin;
    public final Direction dir;
    public final BlockPos originBlockPos;
    private final Vec3d normal;
    private final Vec3d normalScaled;
    private Vec3d current;
    private BlockPos currentBlockPos;
    private Vec3i latestOffset = Vec3i.ZERO;

    private MovementInPlane(Vec3d origin, Direction dir) {
        this.origin = origin;
        this.dir = dir;
        this.normal = VecUtil.cast(dir.getVector());
        this.normalScaled = normal.multiply(-.1);
        this.originBlockPos = VecUtil.floor(origin.add(normalScaled));
        this.current = origin;
        this.currentBlockPos = originBlockPos;
    }

    public static @Nullable MovementInPlane create(PlayerEntity player, BlockBox box) {
        Direction dir = Direction.getFacing(player.getRotationVecClient());
        Direction.Axis axis = dir.getAxis();
        Vec3i dirVec = dir.getVector();
        Vec3d cornerPos = BoxUtil.getCorner(box, dirVec).toCenterPos();

        double comp = cornerPos.getComponentAlongAxis(axis);
        Vec3d pointInPlane = cornerPos.withAxis(axis, dir.getDirection() == Direction.AxisDirection.NEGATIVE ? Math.floor(comp) : Math.ceil(comp));
        Vec3d normal = VecUtil.cast(dirVec);

        Vec3d origin = RaycastUtil.raycastPlane(player, pointInPlane, normal, RaycastUtil.HitBehindBehaviour.PROJECT);
        if (origin == null) return null;
        return new MovementInPlane(origin, dir);
    }

    public void update(PlayerEntity player) {
        var point = RaycastUtil.raycastPlane(player, origin, normal, RaycastUtil.HitBehindBehaviour.PROJECT);
        if (point == null) return;
        current = point;
        currentBlockPos = VecUtil.floor(current.add(normalScaled));
    }

    public Vec3i consumeOffset() {
        var offset = getCurrentOffset();
        var diff = offset.subtract(latestOffset);
        latestOffset = offset;
        return diff;
    }

    public Vec3i getCurrentOffset() {
        return currentBlockPos.subtract(originBlockPos);
    }

    /**
     * Draws a grid in the targeted axis centered on a given box
     */
    public void drawGrid(MinecraftClient client, MatrixStack matrices, Vec3d pos, BlockBox box, Color color) {
        var centerPos = BoxUtil.toBox(box).getCenter();
        var radius = BOX_GRID_SIZE_MULTIPLIER * BoxUtil.largestAxisExcept(box, dir.getAxis());
        drawGrid(client, matrices, pos, centerPos, radius, color);
    }

    public void drawGrid(MinecraftClient client, MatrixStack matrices, Vec3d pos, double radius, Color color) {
        drawGrid(client, matrices, pos, current, radius, color);
    }

    public void drawGrid(MinecraftClient client, MatrixStack matrices, Vec3d pos, Vec3d centerPos, double radius, Color color) {
        if (radius < ClientConfig.Persistent.getLineRenderLimit()) {
            RenderUtil.drawFadingGrid(client, matrices, pos, centerPos, dir.getAxis(), radius, color);
        }
    }

    public String getDebugText() {
        return "origin " + origin + " dir " + dir + " pos " + current;
    }
}

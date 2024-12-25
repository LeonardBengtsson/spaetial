package spaetial.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;
import spaetial.editing.region.Region;
import spaetial.mixin.client.WorldRendererAccessor;
import spaetial.util.BoxUtil;

import java.awt.*;

/**
 * Contains various methods for rendering regions of blocks and shapes in the world
 */
public final class RenderUtil {
    private RenderUtil() {}

    private static final Vector3f VEC_1_0_0 = new Vector3f(1, 0, 0);
    private static final Vector3f VEC_0_1_0 = new Vector3f(0, 1, 0);
    private static final Vector3f VEC_0_0_1 = new Vector3f(0, 0, 1);

    /**
     * Renders a region with a cuboid box outline around
     *
     * @param color The box's color
     */
    public static void renderRegionWithOutline(MinecraftClient client, MatrixStack matrices, Camera camera, BlockBox box, @Nullable Region region, Color color) {
        drawBox(client, matrices, camera.getPos(), BoxUtil.viewAdjustBox(BoxUtil.toBox(box), camera.getPos()), color);
        if (region != null) {
            renderSingleRegion(client, camera, matrices, region, box, 1f, 1f, 1f);
        }
    }

    public static VertexConsumerProvider.Immediate preRenderRegion(MinecraftClient client, Camera camera, MatrixStack matrices, float tintR, float tintG, float tintB) {
        RenderSystem.setShader(RenderSystem.getShader());
        RenderSystem.enableBlend();

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11C.GL_LEQUAL);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();

//        Tessellator tess = Tessellator.getInstance();
//        var vertexConsumer = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        RenderSystem.setShaderColor(tintR, tintG, tintB, 1f);

        return immediate;
    }

    public static void postRenderRegion(VertexConsumerProvider.Immediate immediate) {
        immediate.draw();
//        BufferRenderer.drawWithGlobalProgram(vertexConsumer.end());

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        RenderSystem.disableDepthTest();
    }

    public static void internalRenderRegion(MinecraftClient client, Vec3d camPos, MatrixStack matrices, VertexConsumerProvider.Immediate immediate, BlockRenderManager manager, Region region, BlockBox box) {
        // TODO fix issues with fog (can be tested by setting render dist to 2 and rendering a region on the edge)
        assert region.hasSize(box.getDimensions());

        region.iterate(BoxUtil.minPos(box), (pos, block, nbt) -> {
//            VertexConsumer consumer = client.getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayers.getBlockLayer(block));
////            VertexConsumer consumer = client.getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getSolid());
////            VertexConsumer consumer = client.getBufferBuilders().getEffectVertexConsumers().getBuffer(RenderLayer.getTranslucentMovingBlock());
//            VertexConsumerProvider consumerProvider =
//                    layer -> consumer; // client.getBufferBuilders().getBlockBufferBuilders().get(layer);

            matrices.push();
            matrices.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);

            if (block.getRenderType() == BlockRenderType.MODEL) {
                manager.renderBlockAsEntity(block, matrices, immediate, 0xf0, OverlayTexture.DEFAULT_UV);
            }

            @Nullable BlockEntity blockEntity = null;
            if (block.getBlock() instanceof BlockWithEntity blockWithEntity) {
                // TODO this is probably very inefficient, consider reworking the system to store block entities in the region class
                if (nbt == null) {
                    blockEntity = blockWithEntity.createBlockEntity(pos, block);
                } else {
                    // related to spaetial.region.Region::create
                    // if (!nbt.contains("id")) nbt.putString("id", Registries.BLOCK.getId(block.getBlock()).toString());

                    blockEntity = BlockEntity.createFromNbt(pos, block, nbt, client.world.getRegistryManager());
                }
                if (blockEntity != null) {
                    blockEntity.setWorld(client.world);
                }
            }
            if (blockEntity != null) {
                BlockEntityRenderer<BlockEntity> blockEntityRenderer = client.getBlockEntityRenderDispatcher().get(blockEntity);
                if (blockEntityRenderer != null) {
                    blockEntityRenderer.render(blockEntity, 0, matrices, immediate, 0xf0, OverlayTexture.DEFAULT_UV);
                }
            }




//            if (block.getRenderType() == BlockRenderType.MODEL) {
//                client.getBlockRenderManager().renderBlockAsEntity(
//                        block,
//                        matrices,
//                        consumerProvider,
//                        0xf0,
//                        OverlayTexture.DEFAULT_UV
//                );
//            } else if (block.getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
//                client.getItemRenderer().renderItem(new ItemStack(block.getBlock()), ModelTransformationMode.NONE, 0xf0, 0, matrices, consumerProvider, client.world, 0);
//            }

//            client.getBlockRenderManager().renderBlock(block, center, client.world, matrices, consumer, false, client.world.random);

//            BlockEntity blockEntity = null;
//
//            if (nbt == null) {
//                if (block.getBlock() instanceof BlockWithEntity blockEntityType) {
//                    blockEntity = blockEntityType.createBlockEntity(center, block);
//                    if (blockEntity != null) {
//                        blockEntity.setWorld(client.world);
//                    }
//                }
//            } else {
//                if (!nbt.contains("id")) {
//                    nbt.putString("id", Registries.BLOCK.getId(block.getBlock()).toString());
//                }
//                blockEntity = BlockEntity.createFromNbt(center, block, nbt);
//            }
//
//            if (blockEntity != null) {
//                var blockEntityRenderer = client.getBlockEntityRenderDispatcher().get(blockEntity);
//                if (blockEntityRenderer != null) {
//                    blockEntityRenderer.render(
//                            blockEntity,
//                            client.getTickDelta(),
//                            matrices,
//                            consumerProvider,
//                            0xf0,
//                            OverlayTexture.DEFAULT_UV
//                    );
//                }
//            }

            matrices.pop();
        });
    }

    /**
     * Renders a given region as if there were real blocks there
     *
     * @param camera   Used for determining the camera position
     * @param region   The region to be rendered
     * @param box      The position and dimension to render the region in
     * @param tintR    Tints the color of the blocks in the region. Set to {@code 1.0f} to leave color unchanged
     * @param tintG    Tints the color of the blocks in the region. Set to {@code 1.0f} to leave color unchanged
     * @param tintB    Tints the color of the blocks in the region. Set to {@code 1.0f} to leave color unchanged
     */
    public static void renderSingleRegion(MinecraftClient client, Camera camera, MatrixStack matrices, Region region, BlockBox box, float tintR, float tintG, float tintB) {
        assert client.world != null;
        assert client.cameraEntity != null;

        var camPos = camera.getPos();
        BlockRenderManager manager = client.getBlockRenderManager();

        var immediate = preRenderRegion(client, camera, matrices, tintR, tintG, tintB);
        internalRenderRegion(client, camPos, matrices, immediate, manager, region, box);
        postRenderRegion(immediate);
    }

    public static void drawBox(MinecraftClient client, MatrixStack matrices, Vec3d cameraPos, Box box, Color color) {
        float[] clr = color.getRGBComponents(null);

        RenderSystem.setShader(RenderSystem.getShader());
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();

        VertexConsumerProvider.Immediate immediate = ((WorldRendererAccessor) client.worldRenderer).getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
        VertexRendering.drawBox(
            matrices, vertexConsumer,
            box.minX - cameraPos.x,
            box.minY - cameraPos.y,
            box.minZ - cameraPos.z,
            box.maxX - cameraPos.x,
            box.maxY - cameraPos.y,
            box.maxZ - cameraPos.z,
            clr[0], clr[1], clr[2], clr[3]
        );
        immediate.draw(RenderLayer.getLines());
    }

    public static void drawFadingGrid(MinecraftClient client, MatrixStack matrices, Vec3d cameraPos, Vec3d centerPos, Direction.Axis axis, double radius, Color color) {
        float x_center = (float) centerPos.x;
        float y_center = (float) centerPos.y;
        float z_center = (float) centerPos.z;
        float x_cam = (float) cameraPos.x;
        float y_cam = (float) cameraPos.y;
        float z_cam = (float) cameraPos.z;

        float i_center, j_center;
        Vector3f i_vec, j_vec;
        switch (axis) {
            case X -> {
                i_center = y_center;
                j_center = z_center;
                i_vec = VEC_0_1_0;
                j_vec = VEC_0_0_1;
            }
            case Y -> {
                i_center = z_center;
                j_center = x_center;
                i_vec = VEC_0_0_1;
                j_vec = VEC_1_0_0;
            }
            case Z -> {
                i_center = x_center;
                j_center = y_center;
                i_vec = VEC_1_0_0;
                j_vec = VEC_0_1_0;
            }
            default -> throw new NullPointerException();
        }

        float[] clr = color.getRGBComponents(null);
        float red = clr[0], green = clr[1], blue = clr[2], alpha = clr[3];

        Vector3f normal = new Vector3f(i_vec).cross(j_vec).normalize();
        float nx = normal.x, ny = normal.y, nz = normal.z;

        RenderSystem.setShader(RenderSystem.getShader());
        RenderSystem.enableBlend();
        var vertexConsumerProvider = ((WorldRendererAccessor) client.worldRenderer).getBufferBuilders().getEntityVertexConsumers();
        var vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        var entry = matrices.peek();

        float r = (float) radius;
        float r_sq = r*r;
        int i_min = (int) Math.ceil(i_center - radius);
        int i_max = (int) Math.floor(i_center + radius);
        int j_min = (int) Math.ceil(j_center - radius);
        int j_max = (int) Math.floor(j_center + radius);

        for (int i = i_min; i <= i_max; i++) {
            float di = i - i_center;
            float dj = (float) Math.sqrt(r_sq - di*di);

            float x = x_center - x_cam + i_vec.x * di;
            float y = y_center - y_cam + i_vec.y * di;
            float z = z_center - z_cam + i_vec.z * di;
            float dx = j_vec.x * dj;
            float dy = j_vec.y * dj;
            float dz = j_vec.z * dj;

            float eff_alpha = alpha * (1 - Math.abs(di / r));

            vertexConsumer.vertex(entry, x - dx, y - dy, z - dz).color(red, green, blue, 0).normal(entry, dx, dy, dz);
            vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, eff_alpha).normal(entry, dx, dy, dz);
            vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, eff_alpha).normal(entry, dx, dy, dz);
            vertexConsumer.vertex(entry, x + dx, y + dy, z + dz).color(red, green, blue, 0).normal(entry, dx, dy, dz);
        }
        for (int j = j_min; j <= j_max; j++) {
            float dj = j - j_center;
            float di = (float) Math.sqrt(r_sq - dj*dj);

            float x = x_center - x_cam + j_vec.x * dj;
            float y = y_center - y_cam + j_vec.y * dj;
            float z = z_center - z_cam + j_vec.z * dj;
            float dx = i_vec.x * di;
            float dy = i_vec.y * di;
            float dz = i_vec.z * di;

            float eff_alpha = alpha * (1 - Math.abs(dj / r));

            vertexConsumer.vertex(entry, x - dx, y - dy, z - dz).color(red, green, blue, 0).normal(entry, nx, ny, nz);
            vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, eff_alpha).normal(entry, nx, ny, nz);
            vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, eff_alpha).normal(entry, nx, ny, nz);
            vertexConsumer.vertex(entry, x + dx, y + dy, z + dz).color(red, green, blue, 0).normal(entry, nx, ny, nz);
        }
        vertexConsumerProvider.draw();
    }

    public static void drawFading3dCross(MinecraftClient client, MatrixStack matrices, Vec3d cameraPos, Vec3d pos, double radius, Color color) {
        RenderSystem.setShader(RenderSystem.getShader());
        RenderSystem.enableBlend();
        var vertexConsumerProvider = ((WorldRendererAccessor) client.worldRenderer).getBufferBuilders().getEntityVertexConsumers();
        var vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        var entry = matrices.peek();

        float x = (float) (pos.x - cameraPos.x), y = (float) (pos.y - cameraPos.y), z = (float) (pos.z - cameraPos.z);
        float r = (float) radius;

        float[] clr = color.getRGBComponents(null);
        float red = clr[0], green = clr[1], blue = clr[2], alpha = clr[3];

        vertexConsumer.vertex(entry, x - r, y, z).color(red, green, blue, 0).normal(1, 0, 0);
        vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, alpha).normal(1, 0, 0);
        vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, alpha).normal(1, 0, 0);
        vertexConsumer.vertex(entry, x + r, y, z).color(red, green, blue, 0).normal(1, 0, 0);

        vertexConsumer.vertex(entry, x, y - r, z).color(red, green, blue, 0).normal(0, 1, 0);
        vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, alpha).normal(0, 1, 0);
        vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, alpha).normal(0, 1, 0);
        vertexConsumer.vertex(entry, x, y + r, z).color(red, green, blue, 0).normal(0, 1, 0);

        vertexConsumer.vertex(entry, x, y, z - r).color(red, green, blue, 0).normal(0, 0, 1);
        vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, alpha).normal(0, 0, 1);
        vertexConsumer.vertex(entry, x, y, z).color(red, green, blue, alpha).normal(0, 0, 1);
        vertexConsumer.vertex(entry, x, y, z + r).color(red, green, blue, 0).normal(0, 0, 1);

        vertexConsumerProvider.draw();
    }

    public static void drawSphereQuads(MinecraftClient client, MatrixStack matrices, Vec3d cameraPos, boolean invertibleNormals, Vec3d center, double radius, int resolution, Color color) {
        RenderSystem.setShader(RenderSystem.getShader());
        RenderSystem.enableBlend();
        var vertexConsumerProvider = ((WorldRendererAccessor) client.worldRenderer).getBufferBuilders().getEntityVertexConsumers();
        var vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getDebugQuads());

        float x_cam = (float) cameraPos.x, y_cam = (float) cameraPos.y, z_cam = (float) cameraPos.z;
        float x = (float) (center.x - cameraPos.x), y = (float) (center.y - cameraPos.y), z = (float) (center.z - cameraPos.z);

        int res_v = resolution;
        int res_h = res_v * 2;

        var coords = sphereCoords(radius, res_h, res_v);
        var x_values = coords[0];
        var y_values = coords[1];
        var z_values = coords[2];

        boolean invertNormals = false;
        if (invertibleNormals) {
            float dist_cam_sq = x*x + y*y + z*z;
            double inner_radius_threshold = .9 * radius * Math.cos(2 * Math.PI / Math.min(res_h, res_v));
            if (dist_cam_sq < inner_radius_threshold*inner_radius_threshold) {
                invertNormals = true;
            } else if (dist_cam_sq > 1.1 * radius*radius) {
                invertNormals = false;
            } else {
                invertNormals = true;
                loop:
                for (int i = 0; i < res_h; i++) {
                    for (int j = 0; j < res_v; j++) {
                        int next_h = (i + 1) % res_h;
                        int next_v = j + 1;

                        float x0 = x + x_values[i][j];
                        float y0 = y + y_values[i][j];
                        float z0 = z + z_values[i][j];
                        float x1 = x + x_values[next_h][j];
                        float y1 = y + y_values[next_h][j];
                        float z1 = z + z_values[next_h][j];
                        float x2 = x + x_values[next_h][next_v];
                        float y2 = y + y_values[next_h][next_v];
                        float z2 = z + z_values[next_h][next_v];

                        Vector3f normal = new Vector3f(x1 - x0, y1 - y0, z1 - z0).cross(x2 - x0, y2 - y0, z2 - z0);
                        if (normal.dot(x_cam - x0, y_cam - y0, z_cam - z0) < 0) {
                            invertNormals = false;
                            break loop;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < res_h; i++) {
            for (int j = 0; j < res_v; j++) {
                int next_h = (i + 1) % res_h;
                int next_v = j + 1;

                float x0 = x + x_values[i][j];
                float y0 = y + y_values[i][j];
                float z0 = z + z_values[i][j];
                float x1 = x + x_values[next_h][j];
                float y1 = y + y_values[next_h][j];
                float z1 = z + z_values[next_h][j];
                float x2 = x + x_values[next_h][next_v];
                float y2 = y + y_values[next_h][next_v];
                float z2 = z + z_values[next_h][next_v];
                float x3 = x + x_values[i][next_v];
                float y3 = y + y_values[i][next_v];
                float z3 = z + z_values[i][next_v];

                quad(matrices, vertexConsumer, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, invertNormals, color);
            }
        }

        vertexConsumerProvider.draw();
    }

    private static void quad(MatrixStack matrices, VertexConsumer vertexConsumer, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, boolean invertNormal, Color color) {
        Matrix4f posMatrix = matrices.peek().getPositionMatrix();
        if (invertNormal) {
            vertexConsumer.vertex(posMatrix, x0, y0, z0).color(color.getRGB());
            vertexConsumer.vertex(posMatrix, x3, y3, z3).color(color.getRGB());
            vertexConsumer.vertex(posMatrix, x2, y2, z2).color(color.getRGB());
            vertexConsumer.vertex(posMatrix, x1, y1, z1).color(color.getRGB());
        } else {
            vertexConsumer.vertex(posMatrix, x0, y0, z0).color(color.getRGB());
            vertexConsumer.vertex(posMatrix, x1, y1, z1).color(color.getRGB());
            vertexConsumer.vertex(posMatrix, x2, y2, z2).color(color.getRGB());
            vertexConsumer.vertex(posMatrix, x3, y3, z3).color(color.getRGB());
        }
    }

    private static float[][][] sphereCoords(double radius, int res_h, int res_v) {
        int res_v_1 = res_v + 1;

        final double angle_increment_v = Math.PI / res_v;
        final double angle_increment_h = 2 * Math.PI / res_h;

        float[][] x_values = new float[res_h][res_v_1];
        float[][] y_values = new float[res_h][res_v_1];
        float[][] z_values = new float[res_h][res_v_1];
        for (int i = 0; i < res_h; i++) {
            double angle_h = angle_increment_h * i;
            float x = (float) (Math.cos(angle_h) * radius);
            float z = (float) (Math.sin(angle_h) * radius);
            for (int j = 0; j < res_v_1; j++) {
                double angle_v = j * angle_increment_v;
                double sin = Math.sin(angle_v);
                x_values[i][j] = (float) (x * sin);
                z_values[i][j] = (float) (z * sin);
                y_values[i][j] = (float) (Math.cos(angle_v) * radius);
            }
        }
        return new float[][][] { x_values, y_values, z_values };
    }
}

package spaetial.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.tick.TickManager;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import spaetial.ClientEvents;

@Mixin(WorldRenderer.class)
abstract public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    args = "ldc=blockentities",
                    shift = At.Shift.AFTER

//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/TexturedRenderLayers;getChest()Lnet/minecraft/client/render/RenderLayer;",
//                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void render(
        RenderTickCounter tickCounter,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightmapTextureManager lightmapTextureManager,
        Matrix4f projectionMatrix,
        Matrix4f positionMatrix,
        CallbackInfo ci,
        TickManager _tickManager,
        float _float,
        Profiler _profiler,
        Vec3d _vec3d,
        double _double,
        double _double2,
        double _double3,
        boolean _boolean,
        Frustum _frustum,
        float _float2,
        boolean _boolean2,
        Matrix4fStack _matrix4fStack,
        boolean _boolean3,
        MatrixStack _matrixStack,
        VertexConsumerProvider.Immediate _immediate
    ) {
        ClientEvents.onRender(client, _matrixStack, camera);

//        if (client.player != null && client.world != null) {
//            var pos = client.player.getBlockPos();
//            var box = new BlockBoxWithDimension(BlockBox.create(pos.add(-5, -20, -5), pos.add(5, -10, 5)), client.world.getDimensionKey());
//            var region = Region.create(client.world, box.blockBox());
//            BlockRenderUtil.renderRegion(client, camera, matrices, region, new BlockBoxWithDimension(box.blockBox().offset(0, 10, 0), box.dimension()), 1f, 1f, 1f);
//        }
    }
}

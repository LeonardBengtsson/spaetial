package spaetial.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import spaetial.ClientEvents;

@Deprecated
@Mixin(GameRenderer.class)
abstract public class GameRendererMixin {
    @Shadow @Final MinecraftClient client;

    @Inject(
        method = "renderWorld",
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = { "ldc=hand" }
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onRender(
        RenderTickCounter tickCounter, CallbackInfo info, float float_0, boolean bool_0,
        Camera camera, Entity entity, float float_1, double double_0, Matrix4f matrix4f_0, MatrixStack matrixStack,
        float float_3, float float_4, Quaternionf quaternionf, Matrix4f matrix4f_1
    ) {
//        var matrices = new MatrixStack();
//        matrices.multiplyPositionMatrix(matrix4f_1);
//        ClientEvents.onRender(client, matrices, camera);

        {   // TODO temp testing

//            ShapeRenderUtil.drawAdjustedBox(matrices, client.gameRenderer.getCamera().getPos(), new Box(-2, 100, -2, 3, 105, 3), new Color4f(1f, 1f, 0f, .5f));

//            if (client.player != null && client.world != null) {
//                var pos = client.player.getBlockPos();
//                var box = new BlockBoxWithDimension(BlockBox.create(pos.add(-5, -20, -5), pos.add(5, -10, 5)), client.world.getDimensionKey());
//                var region = Region.create(client.world, box.blockBox());
//                BlockRenderUtil.renderRegion(client, client.gameRenderer.getCamera(), matrices, region, new BlockBoxWithDimension(box.blockBox().offset(0, 10, 0), box.dimension()), 1f, 1f, 1f);
//            }
        }
    }
}

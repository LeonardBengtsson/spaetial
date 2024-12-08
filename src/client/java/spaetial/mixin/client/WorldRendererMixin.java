package spaetial.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(WorldRenderer.class)
abstract public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    args = "ldc=terrain",
                    shift = At.Shift.AFTER

//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/TexturedRenderLayers;getChest()Lnet/minecraft/client/render/RenderLayer;",
//                    shift = At.Shift.BEFORE
            )
    )
    private void renderBlocks(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, Matrix4f positionMatrix, CallbackInfo ci) {
//        if (client.player != null && client.world != null) {
//            var pos = client.player.getBlockPos();
//            var box = new BlockBoxWithDimension(BlockBox.create(pos.add(-5, -20, -5), pos.add(5, -10, 5)), client.world.getDimensionKey());
//            var region = Region.create(client.world, box.blockBox());
//            BlockRenderUtil.renderRegion(client, camera, matrices, region, new BlockBoxWithDimension(box.blockBox().offset(0, 10, 0), box.dimension()), 1f, 1f, 1f);
//        }
    }
}

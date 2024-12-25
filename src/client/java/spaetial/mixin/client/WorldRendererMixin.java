package spaetial.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
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
            method = "method_62214", // lambda in WorldRenderer::renderMain
            at = @At(
                value = "INVOKE_STRING",
                target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                args = "ldc=blockentities",
                shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderMain(
        Fog _fog, RenderTickCounter _renderTickCounter, Camera camera, Profiler _profiler, Matrix4f _matrix4f,
        Matrix4f _matrix4f2, Handle<Framebuffer> _handle, Handle<Framebuffer> _handle2, Handle<Framebuffer> _handle3,
        Handle<Framebuffer> _handle4, boolean _boolean, Frustum _frustum, Handle<Framebuffer> _handle5,
        CallbackInfo ci,
        float _float, Vec3d _vec3d, double _double, double _double2, double _double3, MatrixStack matrixStack,
        VertexConsumerProvider.Immediate _immediate
    ) {
        ClientEvents.onRender(client, matrixStack, camera);
    }
}

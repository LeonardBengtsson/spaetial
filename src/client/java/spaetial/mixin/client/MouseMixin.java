package spaetial.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spaetial.ClientEvents;
import spaetial.input.InputAction;
import spaetial.input.ModInput;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method="onMouseButton(JIII)V", at = @At("HEAD"), cancellable = true)
    private void mouseClickHandler(long window, int button, int action, int modifiers, CallbackInfo info) {
        InputAction inputAction = InputAction.fromValue(action);
        boolean shift = (modifiers & 0x1) != 0;
        boolean ctrl = (modifiers & 0x2) != 0;
        boolean alt = (modifiers & 0x4) != 0;

        boolean cancel = ModInput.handleMouseInput(client, button, inputAction, shift, ctrl, alt);
        if (cancel) info.cancel();
    }
    @Inject(method="onMouseScroll(JDD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getInstance()Lnet/minecraft/client/MinecraftClient;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    private void scrollHandler(long window, double horizontal, double vertical, CallbackInfo info) {
        boolean cancel = ModInput.handleScrollWheelInput(client, horizontal, vertical);
        if (cancel) info.cancel();
    }
}

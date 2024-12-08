package spaetial.mixin.client;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spaetial.input.InputAction;
import spaetial.input.ModInput;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method="onKey(JIIII)V", at = @At("HEAD"), cancellable = true)
    private void keyPressHandler(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        if (window != client.getWindow().getHandle()) return;
        InputAction inputAction = InputAction.fromValue(action);
        boolean shift = (modifiers & 0x1) != 0;
        boolean ctrl = (modifiers & 0x2) != 0;
        boolean alt = (modifiers & 0x4) != 0;

        boolean cancel = ModInput.handleKeyPress(client, key, scancode, inputAction, shift, ctrl, alt);
        if (cancel) info.cancel();
    }
}

package spaetial.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spaetial.Spaetial;
import spaetial.server.editing.ServerManager;
import spaetial.server.permissions.PermissionLevel;
import spaetial.util.mixin.NoClipUtil;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(
        method = "tick()V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z",
            shift = At.Shift.AFTER
        )
    )
    public void tickInject(CallbackInfo info) {
        // TODO add permissions
//        PlayerEntity player = (PlayerEntity) (Object) this;
//        boolean noClipOn;
//        if (player.getWorld().isClient) {
//            noClipOn = NoClipUtil.isClientAndNoClipOn;
//        } else {
//            noClipOn = ServerManager.getPlayerConfigOrDefault(player.getUuid()).noClip();
//        }
//        var abilities = player.getAbilities();
//        if (noClipOn) {
//            player.noClip = true;
//            abilities.flying = true;
//            player.setOnGround(false);
//        } else if (!abilities.allowFlying) {
//            abilities.flying = false;
//        }
    }
}

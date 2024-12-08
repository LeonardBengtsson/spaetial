package spaetial.mixin;

import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChainRestrictedNeighborUpdater.class)
public interface ChainRestrictedNeighborUpdaterAccessor {
    @Accessor
    int getMaxChainDepth();
}

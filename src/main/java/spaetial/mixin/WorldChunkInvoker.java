package spaetial.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldChunk.class)
public interface WorldChunkInvoker {
    @Invoker("updateTicker")
    <T extends BlockEntity> void invokeUpdateTicker(T blockEntity);
}

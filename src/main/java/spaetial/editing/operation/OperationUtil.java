package spaetial.editing.operation;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import spaetial.editing.Filter;
import spaetial.editing.Material;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.mixin.WorldChunkInvoker;

public class OperationUtil {
    public static void fillVolume(ServerWorld world, MaskedBlockBox mask, Filter filter, Material material, boolean suppressUpdates) {
        if (filter.allowsAll()) {
            mask.iterate(pos -> setBlockState(world, pos, material.getNext(), suppressUpdates));
        } else {
            mask.iterate(world, (pos, state) -> {
                if (filter.doesAllow(state)) {
                    setBlockState(world, pos, material.getNext(), suppressUpdates);
                } else {
                    material.skip();
                }
            });
        }
    }

    public static boolean setBlockState(ServerWorld world, BlockPos pos, BlockState state, boolean suppressUpdates) {
        if (world.isOutOfHeightLimit(pos)) {
            return false;
        }

        var chunk = world.getWorldChunk(pos);
        var chunkSection = chunk.getSection(chunk.getSectionIndex(pos.getY()));

        var wasEmpty = chunkSection.isEmpty();
        if (state.isAir() && wasEmpty) return false;

        int i = pos.getX() & 0xf, j = pos.getY() & 0xf, k = pos.getZ() & 0xf;

        BlockState oldState = chunkSection.setBlockState(i, j, k, state);
        if (oldState == state) return false;

        chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).trackUpdate(i, pos.getY(), k, state);
        chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).trackUpdate(i, pos.getY(), k, state);
        chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR).trackUpdate(i, pos.getY(), k, state);
        chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).trackUpdate(i, pos.getY(), k, state);

        var isEmpty = chunkSection.isEmpty();
        if (wasEmpty ^ isEmpty) {
            world.getChunkManager().getLightingProvider().setSectionStatus(pos, isEmpty);
        }

        if (ChunkLightProvider.needsLightUpdate(world, pos, oldState, state)) {
            Profiler profiler = world.getProfiler();
            profiler.push("updateSkyLightSources");
            chunk.getChunkSkyLight().isSkyLightAccessible(world, i, pos.getY(), k);
            profiler.swap("queueCheckLight");
            world.getChunkManager().getLightingProvider().checkBlock(pos);
            profiler.pop();
        }

        if (suppressUpdates) {
            if (oldState.hasBlockEntity() && !state.isOf(oldState.getBlock())) {
                world.removeBlockEntity(pos);
            }
        } else {
            oldState.onStateReplaced(world, pos, state, false);
        }

        if (!chunkSection.getBlockState(i, j, k).isOf(state.getBlock())) return false;

        if (!suppressUpdates) {
            state.onBlockAdded(world, pos, state, false);
        }

        if (state.hasBlockEntity()) {
            var blockEntity = chunk.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
            if (blockEntity == null) {
                blockEntity = ((BlockEntityProvider) state.getBlock()).createBlockEntity(pos, state);
                if (blockEntity != null) {
                    chunk.addBlockEntity(blockEntity);
                }
            } else {
                blockEntity.setCachedState(state);
                ((WorldChunkInvoker) chunk).invokeUpdateTicker(blockEntity);
            }
        }
        chunk.setNeedsSaving(true);

        BlockState newState = world.getBlockState(pos);
        if (newState == state) {
            if (chunk.getLevelType() != null && chunk.getLevelType().isAfter(ChunkLevelType.BLOCK_TICKING)) {
                world.updateListeners(pos, oldState, state, 50);
            }
            world.onBlockChanged(pos, oldState, newState);
        }
        return true;

//        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.SKIP_DROPS | Constants.OPTIMIZED_SET_BLOCK_STATE_FLAG, 0);
    }
}

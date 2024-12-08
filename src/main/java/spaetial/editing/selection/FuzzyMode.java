package spaetial.editing.selection;

import net.minecraft.block.*;
import spaetial.editing.blocks.BlockMaterial;

import java.util.Set;

public enum FuzzyMode {
    /**
     * Matches all blocks
     */
    ALL,
    /**
     * Matches non-air blocks
     */
    NON_AIR,
    /**
     * Matches blocks not considered natural. Excludes grass blocks and leaves among others
     */
    NON_NATURAL,
    /**
     * Matches blocks that are not fluid blocks. Specifically, checks for and excludes blocks that are
     * {@link FluidDrainable} but not {@link FluidFillable}, i.e. liquid blocks that are removed when drained and cannot
     * be filled with fluid as they do not exist in an unfilled state
     */
    NON_LIQUID,
    /**
     * Matches blocks that are of the same type as the targeted block, e.g. targeting any stairs block would match any
     * other stairs blocks, and targeting a banner would match standing and wall banners of other colors
     */
    TYPE,
    /**
     * Matches blocks that are of the same family as the targeted block
     */
    FAMILY,
    /**
     * Matches blocks that are of the same material as the targeted block, as defined in {@link BlockMaterial}.
     */
    MATERIAL,
    /**
     * Matches blocks that are the same block as the targeted block, but also allows its variants, e.g.
     * {@code minecraft:torch} and {@code minecraft:wall_torch}.
     */
    BLOCK_VARIANT,
    /**
     * Matches blocks that are the same block as the targeted block.
     */
    BLOCK,
    /**
     * Matches blocks that are the same block as the targeted block and share all its block state properties.
     */
    BLOCK_STATE;

    private static final Set<Block> NATURAL_BLOCKS = Set.of(
        Blocks.GRASS_BLOCK,
        Blocks.STONE
    );

    public boolean connects(BlockState original, BlockState other) {
        return switch (this) {
            case ALL -> true;
            case NON_AIR -> !other.isAir();
            case NON_NATURAL -> !NATURAL_BLOCKS.contains(other.getBlock());
            case NON_LIQUID -> {
                var block = other.getBlock();
                if (block instanceof FluidDrainable) {
                    yield block instanceof FluidFillable;
                }
                yield true;
            }
            case TYPE -> false;
            case FAMILY -> false;
            case MATERIAL -> false;
            case BLOCK_VARIANT -> false;
            case BLOCK -> false;
            case BLOCK_STATE -> original.equals(other);
        };
    }
}

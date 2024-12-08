package spaetial.editing.blocks;

import java.util.ArrayList;
import java.util.List;

public enum BlockType {
    FULL_BLOCK,
    LOG,
    STRIPPED_LOG,
    WOOD,
    STRIPPED_WOOD,
    STAIRS,
    SLAB,
    TRAPDOOR,
    DOOR,
    FENCE,
    PRESSURE_PLATE,
    BUTTON,
    STANDING_SIGN,
    WALL_SIGN,
    HANGING_SIGN,
    WALL_HANGING_SIGN,
    FENCE_GATE,
    WALL,
    STONE_ORE,
    DEEPSLATE_ORE,
    NETHER_ORE,
    RAW_ORE_BLOCK,
    CHISELED,
    CUT,
    CUT_STAIRS,
    CUT_SLAB,
    CRACKED,
    PILLAR,
    SAND_VARIANT,
    LEAVES,
    SAPLING,
    POTTED_PLANT,
    GRASS,
    VINES,
    DIRT_VARIANT,
    GRASS_BLOCK_VARIANT,
    LIGHT_VARIANT,
    STANDING_TORCH,
    WALL_TORCH,
    LANTERN,
    CAMPFIRE,
    CORAL_BLOCK,
    CORAL,
    CORAL_FAN,
    DEAD_CORAL_BLOCK,
    DEAD_CORAL,
    DEAD_CORAL_FAN,
    CONCRETE,
    CONCRETE_POWDER,
    WOOL,
    CARPET,
    TERRACOTTA,
    GLAZED_TERRACOTTA,
    GLASS,
    GLASS_PANE,
    SHULKER_BOX,
    BED,
    CANDLE,
    STANDING_BANNER,
    WALL_BANNER;
    static {
        STRIPPED_LOG.addFallback(LOG);
        WOOD.addFallback(LOG);
        STRIPPED_WOOD.addFallback(STRIPPED_LOG);

        STRIPPED_WOOD.addFallback(LOG);
        FENCE.addFallback(WALL);
        FENCE.addFallback(GLASS_PANE);
        WALL.addFallback(FENCE);
        WALL.addFallback(GLASS_PANE);
        GLASS_PANE.addFallback(FENCE);
        GLASS_PANE.addFallback(WALL);

        STONE_ORE.addFallback(DEEPSLATE_ORE);
        STONE_ORE.addFallback(NETHER_ORE);
        DEEPSLATE_ORE.addFallback(STONE_ORE);
        DEEPSLATE_ORE.addFallback(NETHER_ORE);
        NETHER_ORE.addFallback(STONE_ORE);
        NETHER_ORE.addFallback(DEEPSLATE_ORE);
        RAW_ORE_BLOCK.addFallback(STONE_ORE);
        RAW_ORE_BLOCK.addFallback(DEEPSLATE_ORE);
        RAW_ORE_BLOCK.addFallback(NETHER_ORE);

        CHISELED.addFallback(FULL_BLOCK);
        CUT.addFallback(FULL_BLOCK);
        CUT_STAIRS.addFallback(STAIRS);
        CUT_SLAB.addFallback(SLAB);
        CRACKED.addFallback(FULL_BLOCK);
        PILLAR.addFallback(LOG);
        PILLAR.addFallback(STRIPPED_LOG);
        PILLAR.addFallback(FULL_BLOCK);

        POTTED_PLANT.addFallback(SAPLING);
        SAPLING.addFallback(POTTED_PLANT);

        LANTERN.addFallback(STANDING_TORCH);

        FULL_BLOCK.addFallback(CONCRETE);
        CONCRETE.addFallback(FULL_BLOCK);
        SAND_VARIANT.addFallback(CONCRETE_POWDER);
        CONCRETE_POWDER.addFallback(SAND_VARIANT);
    }
    BlockType() { }
    public final List<BlockType> fallbacks = new ArrayList<>();
    private void addFallback(BlockType blockType) { this.fallbacks.add(blockType); }
}
package spaetial.editing.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.function.BiFunction;

public enum BlockTransformationGroup {
//    AXIS_2(BlockTransformationGroup::axis2, Blocks.NETHER_PORTAL),
//
//    AXIS_3(BlockTransformationGroup::axis3, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD, Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD, Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD, Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.MUDDY_MANGROVE_ROOTS, Blocks.PURPUR_PILLAR, Blocks.QUARTZ_PILLAR, Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.BONE_BLOCK, Blocks.HAY_BLOCK, Blocks.DEEPSLATE, Blocks.INFESTED_DEEPSLATE, Blocks.OCHRE_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.CHAIN),
//
//    FACING_4(BlockTransformationGroup::facing4, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.LOOM, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.PIGLIN_WALL_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.STONECUTTER, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM, Blocks.WHITE_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.BLACK_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.BEE_NEST, Blocks.BEEHIVE, Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.REDSTONE_WALL_TORCH, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.ENDER_CHEST, Blocks.LADDER, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.BAMBOO_WALL_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.TUBE_CORAL_FAN, Blocks.BRAIN_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN, Blocks.FIRE_CORAL_FAN, Blocks.HORN_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.CHISELED_BOOKSHELF, Blocks.COCOA, Blocks.END_PORTAL_FRAME, Blocks.OAK_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.MANGROVE_FENCE_GATE, Blocks.CHERRY_FENCE_GATE, Blocks.BAMBOO_FENCE_GATE, Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE, Blocks.LECTERN, Blocks.COMPARATOR, Blocks.REPEATER, Blocks.SMALL_DRIPLEAF, Blocks.TRIPWIRE_HOOK),
//    FACING_4_BELL(BlockTransformationGroup::facing4bell, Blocks.BELL),
//    FACING_4_HORIZONTAL_BED(BlockTransformationGroup::facing4horizontalBed, Blocks.WHITE_BED, Blocks.LIGHT_GRAY_BED, Blocks.GRAY_BED, Blocks.BLACK_BED, Blocks.BROWN_BED, Blocks.RED_BED, Blocks.ORANGE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.GREEN_BED, Blocks.CYAN_BED, Blocks.LIGHT_BLUE_BED, Blocks.BLUE_BED, Blocks.PURPLE_BED, Blocks.MAGENTA_BED, Blocks.PINK_BED),
//    FACING_4_FACE_3(BlockTransformationGroup::facing4face3, Blocks.LEVER, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.ACACIA_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.MANGROVE_BUTTON, Blocks.CHERRY_BUTTON, Blocks.BAMBOO_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON, Blocks.STONE_BUTTON, Blocks.POLISHED_BLACKSTONE_BUTTON, Blocks.GRINDSTONE),
//    FACING_4_CHEST(BlockTransformationGroup::facing4chest, Blocks.CHEST, Blocks.TRAPPED_CHEST),
//    FACING_5(BlockTransformationGroup::facing5, Blocks.HOPPER),
//    FACING_6(BlockTransformationGroup::facing6, Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER, Blocks.BARREL, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK, Blocks.DISPENSER, Blocks.DROPPER, Blocks.SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.END_ROD, Blocks.LIGHTNING_ROD, Blocks.OBSERVER, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.MOVING_PISTON, Blocks.PISTON_HEAD),
//
//    ROTATION_16(BlockTransformationGroup::rotation16, Blocks.WHITE_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.GRAY_BANNER, Blocks.BLACK_BANNER, Blocks.BROWN_BANNER, Blocks.RED_BANNER, Blocks.ORANGE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.BLUE_BANNER, Blocks.PURPLE_BANNER, Blocks.MAGENTA_BANNER, Blocks.PINK_BANNER, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.PIGLIN_HEAD, Blocks.DRAGON_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.OAK_SIGN, Blocks.BIRCH_SIGN, Blocks.SPRUCE_SIGN, Blocks.JUNGLE_SIGN, Blocks.ACACIA_SIGN, Blocks.DARK_OAK_SIGN, Blocks.MANGROVE_SIGN, Blocks.CHERRY_SIGN, Blocks.BAMBOO_SIGN, Blocks.CRIMSON_SIGN, Blocks.WARPED_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN),
//
//    DIRECTIONS_4(BlockTransformationGroup::directions4, Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE, Blocks.MANGROVE_FENCE, Blocks.CHERRY_FENCE, Blocks.BAMBOO_FENCE, Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE, Blocks.NETHER_BRICK_FENCE, Blocks.GLASS_PANE, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.TRIPWIRE),
//    DIRECTIONS_4_REDSTONE(BlockTransformationGroup::directions4redstone, Blocks.REDSTONE_WIRE),
//    DIRECTIONS_4_WALL(BlockTransformationGroup::directions4wall, Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL, Blocks.GRANITE_WALL, Blocks.DIORITE_WALL, Blocks.ANDESITE_WALL, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.DEEPSLATE_BRICK_WALL, Blocks.BRICK_WALL, Blocks.MUD_BRICK_WALL, Blocks.SANDSTONE_WALL, Blocks.RED_SANDSTONE_WALL, Blocks.PRISMARINE_WALL, Blocks.NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICK_WALL, Blocks.BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.END_STONE_BRICK_WALL),
//    DIRECTIONS_5(BlockTransformationGroup::directions5, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.VINE),
//    DIRECTIONS_6(BlockTransformationGroup::directions6, Blocks.CHORUS_PLANT, Blocks.MUSHROOM_STEM, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK, Blocks.GLOW_LICHEN, Blocks.SCULK_VEIN),
//
//    ORIENTATION_12_JIGSAW(BlockTransformationGroup::orientation12jigsaw, Blocks.JIGSAW),
//
//    HANGING(BlockTransformationGroup::hanging, Blocks.LANTERN, Blocks.SOUL_LANTERN, Blocks.MANGROVE_PROPAGULE),
//    VERTICAL_DIRECTION(BlockTransformationGroup::verticalDirection, Blocks.POINTED_DRIPSTONE),
//
//    SHAPE_6_OTHER_RAIL(BlockTransformationGroup::otherRail, Blocks.POWERED_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL),
//    SHAPE_10_NORMAL_RAIL(BlockTransformationGroup::normalRail, Blocks.RAIL),
//
//    SLAB(BlockTransformationGroup::slab, Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.BIRCH_SLAB, Blocks.JUNGLE_SLAB, Blocks.ACACIA_SLAB, Blocks.DARK_OAK_SLAB, Blocks.MANGROVE_SLAB, Blocks.CHERRY_SLAB, Blocks.BAMBOO_SLAB, Blocks.BAMBOO_MOSAIC_SLAB, Blocks.CRIMSON_SLAB, Blocks.WARPED_SLAB, Blocks.STONE_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.GRANITE_SLAB, Blocks.POLISHED_GRANITE_SLAB, Blocks.DIORITE_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_TILE_SLAB, Blocks.BRICK_SLAB, Blocks.MUD_BRICK_SLAB, Blocks.SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB, Blocks.DARK_PRISMARINE_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.PURPUR_SLAB, Blocks.QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB),
//    STAIRS(BlockTransformationGroup::stairs, Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.MANGROVE_STAIRS, Blocks.CHERRY_STAIRS, Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_MOSAIC_STAIRS, Blocks.CRIMSON_STAIRS, Blocks.WARPED_STAIRS, Blocks.STONE_STAIRS, Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.GRANITE_STAIRS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.DIORITE_STAIRS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.BRICK_STAIRS, Blocks.MUD_BRICK_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.END_STONE_BRICK_STAIRS, Blocks.PURPUR_STAIRS, Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS),
//    TRAPDOOR(BlockTransformationGroup::trapdoor, Blocks.IRON_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.ACACIA_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.MANGROVE_TRAPDOOR, Blocks.CHERRY_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.CRIMSON_TRAPDOOR, Blocks.WARPED_TRAPDOOR),
//
//    HALF_VERTICAL(BlockTransformationGroup::halfVertical, Blocks.LILAC, Blocks.SUNFLOWER, Blocks.ROSE_BUSH, Blocks.PEONY, Blocks.TALL_GRASS, Blocks.TALL_SEAGRASS, Blocks.LARGE_FERN),
//    FACING_4_VERTICAL_DOOR(BlockTransformationGroup::facing4VerticalDoor, Blocks.IRON_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.MANGROVE_DOOR, Blocks.CHERRY_DOOR, Blocks.BAMBOO_DOOR, Blocks.CRIMSON_DOOR, Blocks.WARPED_DOOR);
//
//    private static final BlockState EMPTY = Blocks.AIR.getDefaultState();
//
//    private static final HashMap<Block, BlockTransformationGroup> LOOKUP = new HashMap<>();
//    static {
//        for (var group : BlockTransformationGroup.values()) {
//            for (Block b : group.entries) {
//                LOOKUP.put(b, group);
//            }
//        }
//    }
//    private final BiFunction<BlockState, Transformation, BlockState> transformFunction;
//    private final Block[] entries;
//    BlockTransformationGroup(BiFunction<BlockState, Transformation, BlockState> transformFunction, Block... entries) {
//        this.transformFunction = transformFunction;
//        this.entries = entries;
//    }
//
//    public static BlockTransformationGroup group(Block block) {
//        return LOOKUP.get(block);
//    }
//
//    public static BlockState reorientBlockState(BlockState state, Orientation ori) {
//        BlockTransformationGroup group = LOOKUP.get(state.getBlock());
//        if (group == null) return state;
//        for (Transformation t : ori.requiredTransformations) {
//            state = group.transformFunction.apply(state, t);
//        }
//        return state;
//    }
//
//    private static BlockState axis2(BlockState state, Transformation t) {
//        switch (t) {
//            case ROTATE_Y_90, ROTATE_Y_270 -> {
//                Direction.Axis axis = state.get(Properties.AXIS);
//                if (axis == Direction.Axis.X)
//                    return state.with(Properties.AXIS, Direction.Axis.Z);
//                else
//                    return state.with(Properties.AXIS, Direction.Axis.X);
//            }
//        }
//        return state;
//    }
//
//    private static BlockState axis3(BlockState state, Transformation t) {
//        switch (t) {
//            case ROTATE_Y_90, ROTATE_Y_270 -> {
//                Direction.Axis axis = state.get(Properties.AXIS);
//                if (axis == Direction.Axis.X)
//                    return state.with(Properties.AXIS, Direction.Axis.Z);
//                else if (axis == Direction.Axis.Z)
//                    return state.with(Properties.AXIS, Direction.Axis.X);
//            }
//            case ROTATE_X_90, ROTATE_X_270 -> {
//                Direction.Axis axis = state.get(Properties.AXIS);
//                if (axis == Direction.Axis.Y)
//                    return state.with(Properties.AXIS, Direction.Axis.Z);
//                else if (axis == Direction.Axis.Z)
//                    return state.with(Properties.AXIS, Direction.Axis.Y);
//            }
//            case ROTATE_Z_90, ROTATE_Z_270 -> {
//                Direction.Axis axis = state.get(Properties.AXIS);
//                if (axis == Direction.Axis.X)
//                    return state.with(Properties.AXIS, Direction.Axis.Y);
//                else if (axis == Direction.Axis.Y)
//                    return state.with(Properties.AXIS, Direction.Axis.X);
//            }
//        }
//        return state;
//    }
//
//    private static BlockState facing4(BlockState state, Transformation t) {
//        return genericFacing4(state, t, true);
//    }
//
//    private static BlockState genericFacing4(BlockState state, Transformation t, boolean horizontal) {
//        var prop = horizontal ? Properties.HORIZONTAL_FACING : Properties.FACING;
//        Direction dir = state.get(prop);
//        return state.with(prop, transformFacing4(dir, t));
//    }
//
//    private static BlockState facing4bell(BlockState state, Transformation t) {
//        Attachment attachment = state.get(Properties.ATTACHMENT);
//        Direction dir = state.get(Properties.HORIZONTAL_FACING);
//        Direction newDir;
//        if (attachment == Attachment.CEILING) {
//            newDir = transformFacing6(Direction.UP, t);
//        } else if (attachment == Attachment.FLOOR) {
//            newDir = transformFacing6(Direction.DOWN, t);
//        } else {
//            newDir = transformFacing6(dir, t);
//        }
//        if (newDir == Direction.UP) {
//            return state.with(Properties.ATTACHMENT, Attachment.CEILING);
//        } else if (newDir == Direction.DOWN) {
//            return state.with(Properties.ATTACHMENT, Attachment.FLOOR);
//        }
//        if (attachment == Attachment.CEILING || attachment == Attachment.FLOOR) {
//            return state.with(Properties.ATTACHMENT, Attachment.SINGLE_WALL).with(Properties.FACING, newDir);
//        }
//        return state.with(Properties.FACING, newDir);
//    }
//
//    private static BlockState facing4chest(BlockState state, Transformation t) {
//        ChestType type = state.get(Properties.CHEST_TYPE);
//        if (type == ChestType.SINGLE || t.type != Transformation.Type.FLIP_HORIZONTAL) return genericFacing4(state, t, true);
//        return genericFacing4(state.with(Properties.CHEST_TYPE, type == ChestType.LEFT ? ChestType.RIGHT : ChestType.LEFT), t, true);
//    }
//
//    private static BlockState facing4horizontalBed(BlockState state, Transformation t) {
//        Direction dir = state.get(Properties.HORIZONTAL_FACING);
//        if (t.type == Transformation.Type.ROTATION_SIDE && t.axis != dir.getAxis()) {
//            return EMPTY;
//        }
//        return state;
//    }
//
//    private static BlockState facing4face3(BlockState state, Transformation t) {
//        WallMountLocation face = state.get(Properties.WALL_MOUNT_LOCATION);
//        Direction dir = state.get(Properties.HORIZONTAL_FACING);
//        Direction newDir;
//        if (face == WallMountLocation.CEILING) {
//            newDir = transformFacing6(Direction.UP, t);
//        } else if (face == WallMountLocation.FLOOR) {
//            newDir = transformFacing6(Direction.DOWN, t);
//        } else {
//            newDir = transformFacing6(dir, t);
//        }
//        if (newDir == Direction.UP) {
//            return state.with(Properties.WALL_MOUNT_LOCATION, WallMountLocation.CEILING).with(Properties.HORIZONTAL_FACING, dir.getOpposite());
//        } else if (newDir == Direction.DOWN) {
//            return state.with(Properties.WALL_MOUNT_LOCATION, WallMountLocation.FLOOR);
//        }
//        return state.with(Properties.WALL_MOUNT_LOCATION, WallMountLocation.WALL).with(Properties.HORIZONTAL_FACING, newDir);
//    }
//
//    private static BlockState facing5(BlockState state, Transformation t) {
//        Direction dir = state.get(Properties.FACING);
//        if (dir != Direction.DOWN) return genericFacing4(state, t, false);
//        return state;
//    }
//
//    private static BlockState facing6(BlockState state, Transformation t) {
//        Direction dir = state.get(Properties.FACING);
//        return state.with(Properties.FACING, transformFacing6(dir, t));
//    }
//
//    private static Direction transformFacing4(Direction dir, Transformation t) {
//        switch (t.type) {
//            case ROTATION_Y -> {
//                return switch (t) {
//                    case ROTATE_Y_90 -> dir.rotateYClockwise();
//                    case ROTATE_Y_180 -> dir.getOpposite();
//                    case ROTATE_Y_270 -> dir.rotateYCounterclockwise();
//                    default -> dir;
//                };
//            }
//            case ROTATION_UPSIDE_DOWN -> {
//                if (dir.getAxis() != t.axis) return dir.getOpposite();
//            }
//            case FLIP_HORIZONTAL -> {
//                if (dir.getAxis() == t.axis) return dir.getOpposite();
//            }
//        }
//        return dir;
//    }
//
//    private static Direction transformFacing6(Direction dir, Transformation t) {
//        if (t.type == Transformation.Type.ROTATION_SIDE) {
//            if (dir.getAxis() == t.axis) return dir;
//            switch (t) {
//                case ROTATE_X_90 -> {
//                    return switch (dir) {
//                        case DOWN -> Direction.NORTH;
//                        case UP -> Direction.SOUTH;
//                        case NORTH -> Direction.UP;
//                        case SOUTH -> Direction.DOWN;
//                        default -> dir;
//                    };
//                }
//                case ROTATE_X_270 -> {
//                    return switch (dir) {
//                        case DOWN -> Direction.SOUTH;
//                        case UP -> Direction.NORTH;
//                        case NORTH -> Direction.DOWN;
//                        case SOUTH -> Direction.UP;
//                        default -> dir;
//                    };
//                }
//                case ROTATE_Z_90 -> {
//                    return switch (dir) {
//                        case DOWN -> Direction.WEST;
//                        case UP -> Direction.EAST;
//                        case EAST -> Direction.DOWN;
//                        case WEST -> Direction.UP;
//                        default -> dir;
//                    };
//                }
//                case ROTATE_Z_270 -> {
//                    return switch (dir) {
//                        case DOWN -> Direction.EAST;
//                        case UP -> Direction.WEST;
//                        case EAST -> Direction.UP;
//                        case WEST -> Direction.DOWN;
//                        default -> dir;
//                    };
//                }
//            }
//        } else {
//            if (dir.getAxis() != Direction.Axis.Y) return transformFacing4(dir, t);
//            switch (t.type) {
//                case ROTATION_UPSIDE_DOWN, FLIP_Y -> {
//                    return dir.getOpposite();
//                }
//            }
//        }
//        return dir;
//    }
//
//    private static BlockState rotation16(BlockState state, Transformation t) {
//        switch (t.type) {
//            case ROTATION_Y -> {
//                int rot = state.get(Properties.ROTATION);
//                switch (t) {
//                    case ROTATE_Y_90 -> {
//                        rot += 4;
//                    }
//                    case ROTATE_Y_180 -> {
//                        rot += 8;
//                    }
//                    case ROTATE_Y_270 -> {
//                        rot += 12;
//                    }
//                }
//                return state.with(Properties.ROTATION, rot % 16);
//            }
//            case FLIP_HORIZONTAL, ROTATION_UPSIDE_DOWN -> {
//                int rot = state.get(Properties.ROTATION);
//                if (t.axis == Direction.Axis.X ^ t.type == Transformation.Type.ROTATION_UPSIDE_DOWN) {
//                    // flip about x-axis
//                    return state.with(Properties.ROTATION, (-rot) % 16);
//                } else {
//                    // flip about z-axis
//                    return state.with(Properties.ROTATION, (8 - rot) % 16);
//                }
//            }
//        }
//        return state;
//    }
//
//    private static BlockState directions4(BlockState state, Transformation t) {
//        DirectionSet2 dirs = new DirectionSet2(
//                false,
//                false,
//                state.get(Properties.NORTH),
//                state.get(Properties.EAST),
//                state.get(Properties.SOUTH),
//                state.get(Properties.WEST)
//        );
//        DirectionSet2 newDirs = transformDirections(dirs, t);
//        return state
//                .with(Properties.NORTH, newDirs.north)
//                .with(Properties.EAST, newDirs.east)
//                .with(Properties.SOUTH, newDirs.south)
//                .with(Properties.WEST, newDirs.west);
//    }
//
//    private static BlockState directions4redstone(BlockState state, Transformation t) {
//        DirectionSet3 dirs = new DirectionSet3(
//                Direction3.NONE,
//                Direction3.NONE,
//                Direction3.fromRedstone(state.get(Properties.NORTH_WIRE_CONNECTION)),
//                Direction3.fromRedstone(state.get(Properties.EAST_WIRE_CONNECTION)),
//                Direction3.fromRedstone(state.get(Properties.SOUTH_WIRE_CONNECTION)),
//                Direction3.fromRedstone(state.get(Properties.WEST_WIRE_CONNECTION))
//        );
//        DirectionSet3 newDirs = transformDirections(dirs, t);
//        return state
//                .with(Properties.NORTH_WIRE_CONNECTION, newDirs.north.toRedstone())
//                .with(Properties.EAST_WIRE_CONNECTION, newDirs.east.toRedstone())
//                .with(Properties.SOUTH_WIRE_CONNECTION, newDirs.south.toRedstone())
//                .with(Properties.WEST_WIRE_CONNECTION, newDirs.west.toRedstone());
//    }
//
//    private static BlockState directions4wall(BlockState state, Transformation t) {
//        DirectionSet3 dirs = new DirectionSet3(
//                Direction3.NONE,
//                Direction3.NONE,
//                Direction3.fromWall(state.get(Properties.NORTH_WALL_SHAPE)),
//                Direction3.fromWall(state.get(Properties.EAST_WALL_SHAPE)),
//                Direction3.fromWall(state.get(Properties.SOUTH_WALL_SHAPE)),
//                Direction3.fromWall(state.get(Properties.WEST_WALL_SHAPE))
//        );
//        DirectionSet3 newDirs = transformDirections(dirs, t);
//        return state
//                .with(Properties.NORTH_WALL_SHAPE, newDirs.north.toWall())
//                .with(Properties.EAST_WALL_SHAPE, newDirs.east.toWall())
//                .with(Properties.SOUTH_WALL_SHAPE, newDirs.south.toWall())
//                .with(Properties.WEST_WALL_SHAPE, newDirs.west.toWall());
//    }
//
//    private static BlockState directions5(BlockState state, Transformation t) {
//        DirectionSet2 dirs = new DirectionSet2(
//                state.get(Properties.UP),
//                false,
//                state.get(Properties.NORTH),
//                state.get(Properties.EAST),
//                state.get(Properties.SOUTH),
//                state.get(Properties.WEST)
//        );
//        DirectionSet2 newDirs = transformDirections(dirs, t);
//        return state
//                .with(Properties.UP, newDirs.up)
//                .with(Properties.NORTH, newDirs.north)
//                .with(Properties.EAST, newDirs.east)
//                .with(Properties.SOUTH, newDirs.south)
//                .with(Properties.WEST, newDirs.west);
//    }
//
//    private static BlockState directions6(BlockState state, Transformation t) {
//        DirectionSet2 dirs = new DirectionSet2(
//                state.get(Properties.UP),
//                state.get(Properties.DOWN),
//                state.get(Properties.NORTH),
//                state.get(Properties.EAST),
//                state.get(Properties.SOUTH),
//                state.get(Properties.WEST)
//        );
//        DirectionSet2 newDirs = transformDirections(dirs, t);
//        return state
//                .with(Properties.UP, newDirs.up)
//                .with(Properties.DOWN, newDirs.down)
//                .with(Properties.NORTH, newDirs.north)
//                .with(Properties.EAST, newDirs.east)
//                .with(Properties.SOUTH, newDirs.south)
//                .with(Properties.WEST, newDirs.west);
//    }
//
//    private static DirectionSet2 transformDirections(DirectionSet2 dirs, Transformation t) {
//        boolean u = dirs.up;
//        boolean d = dirs.down;
//        boolean n = dirs.north;
//        boolean e = dirs.east;
//        boolean s = dirs.south;
//        boolean w = dirs.west;
//        switch (t) {
//            case ROTATE_Y_90 -> {
//                e = dirs.north;
//                s = dirs.east;
//                w = dirs.south;
//                n = dirs.west;
//            }
//            case ROTATE_Y_180 -> {
//                s = dirs.north;
//                w = dirs.east;
//                n = dirs.south;
//                e = dirs.west;
//            }
//            case ROTATE_Y_270 -> {
//                w = dirs.north;
//                n = dirs.east;
//                e = dirs.south;
//                s = dirs.west;
//            }
//            case ROTATE_X_90 -> {
//                n = dirs.up;
//                d = dirs.north;
//                s = dirs.down;
//                u = dirs.south;
//            }
//            case ROTATE_X_180 -> {
//                d = dirs.up;
//                s = dirs.north;
//                u = dirs.down;
//                n = dirs.south;
//            }
//            case ROTATE_X_270 -> {
//                s = dirs.up;
//                u = dirs.north;
//                n = dirs.down;
//                d = dirs.south;
//            }
//            case ROTATE_Z_90 -> {
//                e = dirs.up;
//                d = dirs.east;
//                w = dirs.down;
//                u = dirs.west;
//            }
//            case ROTATE_Z_180 -> {
//                d = dirs.up;
//                w = dirs.east;
//                u = dirs.down;
//                e = dirs.west;
//            }
//            case ROTATE_Z_270 -> {
//                w = dirs.up;
//                u = dirs.east;
//                e = dirs.down;
//                d = dirs.west;
//            }
//            case FLIP_X -> {
//                w = dirs.east;
//                e = dirs.west;
//            }
//            case FLIP_Z -> {
//                n = dirs.south;
//                s = dirs.north;
//            }
//            case FLIP_Y -> {
//                u = dirs.down;
//                d = dirs.up;
//            }
//        }
//        return new DirectionSet2(u, d, n, e, s, w);
//    }
//
//    private static DirectionSet3 transformDirections(DirectionSet3 dirs, Transformation t) {
//        Direction3 u = dirs.up;
//        Direction3 d = dirs.down;
//        Direction3 n = dirs.north;
//        Direction3 e = dirs.east;
//        Direction3 s = dirs.south;
//        Direction3 w = dirs.west;
//        switch (t) {
//            case ROTATE_Y_90 -> {
//                e = dirs.north;
//                s = dirs.east;
//                w = dirs.south;
//                n = dirs.west;
//            }
//            case ROTATE_Y_180 -> {
//                s = dirs.north;
//                w = dirs.east;
//                n = dirs.south;
//                e = dirs.west;
//            }
//            case ROTATE_Y_270 -> {
//                w = dirs.north;
//                n = dirs.east;
//                e = dirs.south;
//                s = dirs.west;
//            }
//            case ROTATE_X_90 -> {
//                n = dirs.up;
//                d = dirs.north;
//                s = dirs.down;
//                u = dirs.south;
//            }
//            case ROTATE_X_180 -> {
//                d = dirs.up;
//                s = dirs.north;
//                u = dirs.down;
//                n = dirs.south;
//            }
//            case ROTATE_X_270 -> {
//                s = dirs.up;
//                u = dirs.north;
//                n = dirs.down;
//                d = dirs.south;
//            }
//            case ROTATE_Z_90 -> {
//                e = dirs.up;
//                d = dirs.east;
//                w = dirs.down;
//                u = dirs.west;
//            }
//            case ROTATE_Z_180 -> {
//                d = dirs.up;
//                w = dirs.east;
//                u = dirs.down;
//                e = dirs.west;
//            }
//            case ROTATE_Z_270 -> {
//                w = dirs.up;
//                u = dirs.east;
//                e = dirs.down;
//                d = dirs.west;
//            }
//            case FLIP_X -> {
//                w = dirs.east;
//                e = dirs.west;
//            }
//            case FLIP_Z -> {
//                n = dirs.south;
//                s = dirs.north;
//            }
//            case FLIP_Y -> {
//                u = dirs.down;
//                d = dirs.up;
//            }
//        }
//        return new DirectionSet3(u, d, n, e, s, w);
//    }
//
//    private static BlockState orientation12jigsaw(BlockState state, Transformation t) {
//        JigsawOrientation ori = state.get(Properties.ORIENTATION);
//        Direction primary = switch (ori) {
//            case DOWN_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST -> Direction.DOWN;
//            case UP_EAST, UP_NORTH, UP_SOUTH, UP_WEST -> Direction.UP;
//            case WEST_UP -> Direction.WEST;
//            case EAST_UP -> Direction.EAST;
//            case NORTH_UP -> Direction.NORTH;
//            case SOUTH_UP -> Direction.SOUTH;
//        };
//        Direction secondary = switch (ori) {
//            case DOWN_EAST, UP_EAST -> Direction.EAST;
//            case DOWN_NORTH, UP_NORTH -> Direction.NORTH;
//            case DOWN_SOUTH, UP_SOUTH -> Direction.SOUTH;
//            case DOWN_WEST, UP_WEST -> Direction.WEST;
//            case WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP -> Direction.UP;
//        };
//        Direction newPrimary = transformFacing6(primary, t);
//        Direction newSecondary = transformFacing6(secondary, t);
//        switch (newPrimary) {
//            case UP -> {
//                return state.with(Properties.ORIENTATION, switch (newSecondary) {
//                    case SOUTH -> JigsawOrientation.UP_SOUTH;
//                    case WEST -> JigsawOrientation.UP_WEST;
//                    case EAST -> JigsawOrientation.UP_EAST;
//                    default -> JigsawOrientation.UP_NORTH;
//                });
//            }
//            case DOWN -> {
//                return state.with(Properties.ORIENTATION, switch (newSecondary) {
//                    case SOUTH -> JigsawOrientation.DOWN_SOUTH;
//                    case WEST -> JigsawOrientation.DOWN_WEST;
//                    case EAST -> JigsawOrientation.DOWN_EAST;
//                    default -> JigsawOrientation.DOWN_NORTH;
//                });
//            }
//            case NORTH -> {
//                return state.with(Properties.ORIENTATION, JigsawOrientation.NORTH_UP);
//            }
//            case SOUTH -> {
//                return state.with(Properties.ORIENTATION, JigsawOrientation.SOUTH_UP);
//            }
//            case WEST -> {
//                return state.with(Properties.ORIENTATION, JigsawOrientation.WEST_UP);
//            }
//            case EAST -> {
//                return state.with(Properties.ORIENTATION, JigsawOrientation.EAST_UP);
//            }
//        }
//        return state;
//    }
//
//    private static BlockState hanging(BlockState state, Transformation t) {
//        boolean hanging = state.get(Properties.HANGING);
//        if (t.type == Transformation.Type.ROTATION_UPSIDE_DOWN || t.type == Transformation.Type.FLIP_Y) {
//            return state.with(Properties.HANGING, !hanging);
//        }
//        return state;
//    }
//
//    private static BlockState verticalDirection(BlockState state, Transformation t) {
//        Direction dir = state.get(Properties.VERTICAL_DIRECTION);
//        if (t.type == Transformation.Type.ROTATION_UPSIDE_DOWN || t.type == Transformation.Type.FLIP_Y) {
//            return state.with(Properties.VERTICAL_DIRECTION, dir.getOpposite());
//        }
//        return state;
//    }
//
//    private static BlockState otherRail(BlockState state, Transformation t) {
//        return rail(state, t, false);
//    }
//
//    private static BlockState normalRail(BlockState state, Transformation t) {
//        return rail(state, t, true);
//    }
//
//    private static BlockState rail(BlockState state, Transformation t, boolean canBeCurved) {
//        RailShape shape = state.get(canBeCurved ? Properties.RAIL_SHAPE : Properties.STRAIGHT_RAIL_SHAPE);
//        if (shape.isAscending()) {
//            Direction dir = switch (shape) {
//                case ASCENDING_EAST -> Direction.EAST;
//                case ASCENDING_WEST -> Direction.WEST;
//                case ASCENDING_NORTH -> Direction.NORTH;
//                default -> Direction.SOUTH;
//            };
//            Direction newDir = dir;
//            switch (t.type) {
//                case ROTATION_Y -> {
//                    newDir = transformFacing4(dir, t);
//                }
//                case ROTATION_UPSIDE_DOWN -> {
//                    if (dir.getAxis() == t.axis) newDir = dir.getOpposite();
//                }
//                case ROTATION_SIDE -> {
//                    if (dir.getAxis() != t.axis) newDir = dir.getOpposite();
//                }
//                default -> {
//                    return state;
//                }
//            }
//            return state.with(canBeCurved ? Properties.RAIL_SHAPE : Properties.STRAIGHT_RAIL_SHAPE, switch (newDir) {
//                case NORTH -> RailShape.ASCENDING_NORTH;
//                case SOUTH -> RailShape.ASCENDING_SOUTH;
//                case WEST -> RailShape.ASCENDING_WEST;
//                default -> RailShape.ASCENDING_EAST;
//            });
//        } else {
//            if (shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST) {
//                if (t == Transformation.ROTATE_Y_90 || t == Transformation.ROTATE_Y_270) {
//                    return state.with(canBeCurved ? Properties.RAIL_SHAPE : Properties.STRAIGHT_RAIL_SHAPE, shape == RailShape.NORTH_SOUTH ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
//                } else if (t.type == Transformation.Type.ROTATION_SIDE) {
//                    if (shape == RailShape.NORTH_SOUTH && t.axis == Direction.Axis.X) {
//                        return state.with(Properties.RAIL_SHAPE, t == Transformation.ROTATE_X_90 ? RailShape.ASCENDING_SOUTH : RailShape.ASCENDING_NORTH);
//                    } else if (shape == RailShape.EAST_WEST && t.axis == Direction.Axis.Z) {
//                        return state.with(Properties.RAIL_SHAPE, t == Transformation.ROTATE_Z_90 ? RailShape.ASCENDING_WEST : RailShape.ASCENDING_EAST);
//                    }
//                }
//            } else if (canBeCurved) {
//                if (t.type == Transformation.Type.ROTATION_Y) {
//                    Direction dir = switch (shape) {
//                        case SOUTH_EAST -> Direction.EAST;
//                        case SOUTH_WEST -> Direction.SOUTH;
//                        case NORTH_WEST -> Direction.WEST;
//                        default -> Direction.NORTH;
//                    };
//                    Direction newDir = transformFacing4(dir, t);
//                    return state.with(Properties.RAIL_SHAPE, switch (newDir) {
//                        case NORTH -> RailShape.NORTH_EAST;
//                        case SOUTH -> RailShape.SOUTH_WEST;
//                        case WEST -> RailShape.NORTH_WEST;
//                        default -> RailShape.SOUTH_EAST;
//                    });
//                } else if (t.type == Transformation.Type.ROTATION_UPSIDE_DOWN) {
//                    return state.with(Properties.RAIL_SHAPE, switch (shape) {
//                        case SOUTH_EAST -> t.axis == Direction.Axis.X ? RailShape.NORTH_EAST : RailShape.SOUTH_WEST;
//                        case SOUTH_WEST -> t.axis == Direction.Axis.X ? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
//                        case NORTH_WEST -> t.axis == Direction.Axis.X ? RailShape.SOUTH_EAST : RailShape.NORTH_WEST;
//                        default -> t.axis == Direction.Axis.X ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
//                    });
//                } else if (t.type == Transformation.Type.FLIP_HORIZONTAL) {
//                    return state.with(Properties.RAIL_SHAPE, switch (shape) {
//                        case SOUTH_EAST -> t.axis == Direction.Axis.X ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
//                        case SOUTH_WEST -> t.axis == Direction.Axis.X ? RailShape.SOUTH_EAST : RailShape.NORTH_WEST;
//                        case NORTH_WEST -> t.axis == Direction.Axis.X ? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
//                        default -> t.axis == Direction.Axis.X ? RailShape.NORTH_EAST : RailShape.SOUTH_WEST;
//                    });
//                }
//            }
//        }
//        return state;
//    }
//
//    private static BlockState slab(BlockState state, Transformation t) {
//        SlabType type = state.get(Properties.SLAB_TYPE);
//        if (type != SlabType.DOUBLE && (t.type == Transformation.Type.ROTATION_UPSIDE_DOWN || t.type == Transformation.Type.FLIP_Y)) {
//            return state.with(Properties.SLAB_TYPE, type == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
//        }
//        return state;
//    }
//
//    private static BlockState stairs(BlockState state, Transformation t) {
//        Direction dir = state.get(Properties.HORIZONTAL_FACING);
//        StairShape shape = state.get(Properties.STAIR_SHAPE);
//        BlockHalf half = state.get(Properties.BLOCK_HALF);
//        switch (t.type) {
//            case ROTATION_Y -> {
//                return genericFacing4(state, t, true);
//            }
//            case FLIP_Y -> {
//                return state.with(Properties.BLOCK_HALF, half == BlockHalf.BOTTOM ? BlockHalf.TOP : BlockHalf.BOTTOM);
//            }
//            case FLIP_HORIZONTAL -> {
//                if (shape == StairShape.STRAIGHT) {
//                    return genericFacing4(state, t, true);
//                } else {
//                    StairShape mirrored = switch (shape) {
//                        case INNER_LEFT -> StairShape.INNER_RIGHT;
//                        case INNER_RIGHT -> StairShape.INNER_LEFT;
//                        case OUTER_LEFT -> StairShape.OUTER_RIGHT;
//                        default -> StairShape.OUTER_LEFT;
//                    };
//                    return genericFacing4(state, t, true).with(Properties.STAIR_SHAPE, mirrored);
//                }
//            }
//            case ROTATION_UPSIDE_DOWN -> {
//                if (t.axis == dir.getAxis()) {
//                    return state.with(Properties.BLOCK_HALF, half == BlockHalf.BOTTOM ? BlockHalf.TOP : BlockHalf.BOTTOM);
//                } else {
//                    return state
//                            .with(Properties.HORIZONTAL_FACING, dir.getOpposite())
//                            .with(Properties.BLOCK_HALF, half == BlockHalf.BOTTOM ? BlockHalf.TOP : BlockHalf.BOTTOM);
//                }
//            }
//            case ROTATION_SIDE -> {
//                if (t.axis != dir.getAxis()) {
//                    boolean rot;
//                    if (t.axis == Direction.Axis.X) {
//                        rot = dir == Direction.NORTH ^ t == Transformation.ROTATE_X_90;
//                    } else {
//                        rot = dir == Direction.EAST ^ t == Transformation.ROTATE_Z_90;
//                    }
//                    if (rot) {
//                        return state.with(Properties.BLOCK_HALF, half == BlockHalf.BOTTOM ? BlockHalf.TOP : BlockHalf.BOTTOM);
//                    } else {
//                        return state.with(Properties.FACING, dir.getOpposite());
//                    }
//                }
//            }
//        }
//        return state;
//    }
//
//    private static BlockState trapdoor(BlockState state, Transformation t) {
//        switch (t.type) {
//            case ROTATION_Y, FLIP_HORIZONTAL -> {
//                return genericFacing4(state, t, true);
//            }
//            case ROTATION_SIDE -> {
//                Direction dir = state.get(Properties.HORIZONTAL_FACING);
//                BlockHalf half = state.get(Properties.BLOCK_HALF);
//                boolean open = state.get(Properties.OPEN);
//                Direction primary;
//                Direction secondary;
//                if (open) {
//                    primary = dir;
//                    secondary = half == BlockHalf.BOTTOM ? Direction.DOWN : Direction.UP;
//                } else {
//                    primary = half == BlockHalf.BOTTOM ? Direction.DOWN : Direction.UP;
//                    secondary = dir;
//                }
//                Direction newPrimary = transformFacing6(primary, t);
//                Direction newSecondary = transformFacing6(secondary, t);
//                switch (newPrimary) {
//                    case UP -> {
//                        return state
//                                .with(Properties.HORIZONTAL_FACING, newSecondary)
//                                .with(Properties.BLOCK_HALF, BlockHalf.TOP)
//                                .with(Properties.OPEN, false);
//                    }
//                    case DOWN -> {
//                        return state
//                                .with(Properties.HORIZONTAL_FACING, newSecondary)
//                                .with(Properties.BLOCK_HALF, BlockHalf.BOTTOM)
//                                .with(Properties.OPEN, false);
//                    }
//                    default -> {
//                        return state
//                                .with(Properties.HORIZONTAL_FACING, newPrimary)
//                                .with(Properties.BLOCK_HALF, newSecondary == Direction.DOWN ? BlockHalf.BOTTOM : (newSecondary == Direction.UP ? BlockHalf.TOP : half))
//                                .with(Properties.OPEN, true);
//                    }
//                }
//            }
//            case ROTATION_UPSIDE_DOWN -> {
//                Direction dir = state.get(Properties.HORIZONTAL_FACING);
//                Direction newDir = transformFacing4(dir, t);
//                BlockHalf half = state.get(Properties.BLOCK_HALF);
//                return state.with(Properties.HORIZONTAL_FACING, newDir).with(Properties.BLOCK_HALF, half == BlockHalf.BOTTOM ? BlockHalf.TOP : BlockHalf.BOTTOM);
//            }
//            case FLIP_Y -> {
//                BlockHalf half = state.get(Properties.BLOCK_HALF);
//                return state.with(Properties.BLOCK_HALF, half == BlockHalf.BOTTOM ? BlockHalf.TOP : BlockHalf.BOTTOM);
//            }
//        }
//        return state;
//    }
//
//    private static BlockState halfVertical(BlockState state, Transformation t) {
//        DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
//        if (t.type == Transformation.Type.ROTATION_UPSIDE_DOWN || t.type == Transformation.Type.FLIP_Y) {
//            return state.with(Properties.DOUBLE_BLOCK_HALF, half == DoubleBlockHalf.LOWER ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
//        } else if (t.type == Transformation.Type.ROTATION_SIDE) {
//            return EMPTY;
//        }
//        return state;
//    }
//
//    private static BlockState facing4VerticalDoor(BlockState state, Transformation t) {
//        DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
//        Direction dir = state.get(Properties.HORIZONTAL_FACING);
//        Direction newDir = transformFacing4(dir, t);
//        if (t.type == Transformation.Type.FLIP_Y) {
//            return state.with(Properties.DOUBLE_BLOCK_HALF, half == DoubleBlockHalf.LOWER ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
//        } else if (t.type == Transformation.Type.ROTATION_UPSIDE_DOWN) {
//            DoorHinge hinge = state.get(Properties.DOOR_HINGE);
//            return state
//                    .with(Properties.DOUBLE_BLOCK_HALF, half == DoubleBlockHalf.LOWER ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER)
//                    .with(Properties.DOOR_HINGE, hinge == DoorHinge.LEFT ? DoorHinge.RIGHT : DoorHinge.LEFT)
//                    .with(Properties.HORIZONTAL_FACING, newDir);
//        } else if (t.type == Transformation.Type.ROTATION_SIDE) {
//            return EMPTY;
//        }
//        return state.with(Properties.HORIZONTAL_FACING, newDir);
//    }
//
//    private record DirectionSet2(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) { }
//
//    private record DirectionSet3(Direction3 up, Direction3 down, Direction3 north, Direction3 east, Direction3 south, Direction3 west) { }
//
//    private enum Direction3 {
//        NONE, LOW_SIDE, TALL_UP;
//
//        public static Direction3 fromWall(WallShape shape) {
//            return switch (shape) {
//                case NONE -> NONE;
//                case LOW -> LOW_SIDE;
//                case TALL -> TALL_UP;
//            };
//        }
//
//        public static Direction3 fromRedstone(WireConnection shape) {
//            return switch (shape) {
//                case NONE -> NONE;
//                case SIDE -> LOW_SIDE;
//                case UP -> TALL_UP;
//            };
//        }
//
//        public WallShape toWall() {
//            return switch (this) {
//                case NONE -> WallShape.NONE;
//                case LOW_SIDE -> WallShape.LOW;
//                case TALL_UP -> WallShape.TALL;
//            };
//        }
//
//        public WireConnection toRedstone() {
//            return switch (this) {
//                case NONE -> WireConnection.NONE;
//                case LOW_SIDE -> WireConnection.SIDE;
//                case TALL_UP -> WireConnection.UP;
//            };
//        }
//    }
}

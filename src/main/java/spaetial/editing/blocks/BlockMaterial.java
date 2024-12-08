package spaetial.editing.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import spaetial.Spaetial;
import spaetial.util.Translatable;

import java.util.*;

public enum BlockMaterial implements Translatable {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    MANGROVE("mangrove"),
    CHERRY("cherry"),
    BAMBOO("bamboo"),
    CRIMSON("crimson"),
    WARPED("warped"),

    IRON("iron"),
    COAL("coal"),
    COPPER("copper"),
    GOLD("gold"),
    REDSTONE("redstone"),
    EMERALD("emerald"),
    LAPIS("lapis"),
    DIAMOND("diamond"),
    QUARTZ("quartz"),
    NETHERITE("netherite"),

    STONE_BRICKS("stone_bricks"),
    DEEPSLATE_BRICKS("deepslate_bricks"),
    SANDSTONE("sandstone"),
    RED_SANDSTONE("red_sandstone"),
    NETHER_BRICKS("nether_bricks"),
    RED_NETHER_BRICKS("red_nether_bricks"),
    MOSSY_STONE_BRICKS("mossy_stone_bricks"),
    COBBLED_DEEPSLATE("cobbled_deepslate"),
    POLISHED_DEEPSLATE("polished_deepslate"),
    DEEPSLATE_TILES("deepslate_tiles"),
    BLACKSTONE("blackstone"),
    POLISHED_BLACKSTONE("polished_blackstone"),
    BLACKSTONE_BRICKS("blackstone_bricks"),
    STONE("stone"),
    COBBLESTONE("cobblestone"),
    MOSSY_COBBLESTONE("mossy_cobblestone"),
    GRANITE("granite"),
    DIORITE("diorite"),
    ANDESITE("andesite"),
    BRICKS("bricks"),
    MUD("mud"),
    SMOOTH_SANDSTONE("smooth_sandstone"),
    SMOOTH_RED_SANDSTONE("smooth_red_sandstone"),
    PRISMARINE("prismarine"),
    DARK_PRISMARINE("dark_prismarine"),
    END("end"),
    PURPUR("purpur"),
    SMOOTH_QUARTZ("smooth_quartz"),
    BASALT("basalt"),
    AZALEA("azalea"),
    FLOWERING_AZALEA("flowering_azalea"),
    RED_MUSHROOM("red_mushroom"),
    BROWN_MUSHROOM("brown_mushroom"),
    OVERWORLD("overworld"),
    NON_SOUL("non_soul"),
    SOUL("soul"),
    UNCOLORED("uncolored"), WHITE("white"), LIGHT_GRAY("light_gray"), GRAY("gray"),
    BLACK("black"), BROWN("brown"), RED("red"), ORANGE("orange"), YELLOW("yellow"),
    LIME("lime"), GREEN("green"), CYAN("cyan"), LIGHT_BLUE("light_blue"), BLUE("blue"),
    PURPLE("purple"), MAGENTA("magenta"), PINK("pink");

    static {
        // chiseled, cut, cut slab, sand variant
        SMOOTH_SANDSTONE.addFallback(SANDSTONE);
        SMOOTH_RED_SANDSTONE.addFallback(RED_SANDSTONE);
        SMOOTH_QUARTZ.addFallback(QUARTZ);

        // fence
        RED_NETHER_BRICKS.addFallback(NETHER_BRICKS);

        // chiseled and cracked
        COBBLESTONE.addFallback(STONE_BRICKS);
        MOSSY_COBBLESTONE.addFallback(STONE_BRICKS);
        MOSSY_STONE_BRICKS.addFallback(STONE_BRICKS);

        // button
        STONE_BRICKS.addFallback(STONE);
        MOSSY_COBBLESTONE.addFallback(STONE);
        COBBLESTONE.addFallback(STONE);
        MOSSY_COBBLESTONE.addFallback(STONE);

        // chiseled and cracked
        COBBLED_DEEPSLATE.addFallback(DEEPSLATE_BRICKS);
        POLISHED_DEEPSLATE.addFallback(DEEPSLATE_BRICKS);

        // gilded, chiseled and cracked
        BLACKSTONE.addFallback(POLISHED_BLACKSTONE);
        BLACKSTONE.addFallback(BLACKSTONE_BRICKS);
        POLISHED_BLACKSTONE.addFallback(BLACKSTONE);
        POLISHED_BLACKSTONE.addFallback(BLACKSTONE_BRICKS);
        BLACKSTONE_BRICKS.addFallback(BLACKSTONE);
        BLACKSTONE_BRICKS.addFallback(POLISHED_BLACKSTONE);

        // i mean white is basically uncolored
        UNCOLORED.addFallback(WHITE);
    }

    private static final Map<String, BlockMaterial> LOOKUP = new HashMap<>();
    static {
        for (BlockMaterial g : BlockMaterial.values()) {
            LOOKUP.put(g.name, g);
        }
    }

    public static BlockMaterial fromName(String name) {
        return LOOKUP.get(name);
    }

    public final String name;
    private final EnumMap<BlockType, Block> entries = new EnumMap<>(BlockType.class);

    BlockMaterial(String name) {
        this.name = name;
    }

    private void add(BlockType type, Block block) {
        entries.put(type, block);
        TYPE_LOOKUP.put(block, type);
        if (!GROUP_LOOKUP.containsKey(block)) GROUP_LOOKUP.put(block, new ArrayList<>());
        List<BlockMaterial> groupEntry = GROUP_LOOKUP.get(block);
        groupEntry.add(this);
    }

    private static final HashMap<Block, BlockType> TYPE_LOOKUP = new HashMap<>();

    private static final HashMap<Block, List<BlockMaterial>> GROUP_LOOKUP = new HashMap<>();
    private static final Set<BlockMaterial> COLOR_GROUPS = Set.of(UNCOLORED, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN, RED, ORANGE, YELLOW, LIME, GREEN, CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK);

    private final List<BlockMaterial> fallbacks = new ArrayList<>();

    private void addFallback(BlockMaterial group) { fallbacks.add(group); }
    public static BlockState changeGroup(BlockState state, BlockMaterial group) {
        Block block = state.getBlock();
        BlockType type = TYPE_LOOKUP.get(block);
        if (group.entries.containsKey(type)) {
            return convertState(state, group.entries.get(type));
        }
        for (BlockMaterial groupFallback : group.fallbacks) {
            if (groupFallback.entries.containsKey(type)) {
                return convertState(state, groupFallback.entries.get(type));
            }
        }
        for (BlockType typeFallback : type.fallbacks) {
            if (group.entries.containsKey(typeFallback)) {
                return convertState(state, group.entries.get(typeFallback));
            }
            for (BlockMaterial groupFallback : group.fallbacks) {
                if (groupFallback.entries.containsKey(typeFallback)) {
                    return convertState(state, groupFallback.entries.get(typeFallback));
                }
            }
        }
        return state;
    }

    public static BlockType getType(Block block) {
        BlockMaterial group = getMainGroup(block);
        if (group == null) return null;
        for (var e : group.entries.entrySet()) {
            if (e.getValue() == block) return e.getKey();
        }
        return null;
    }

    public static Block changeType(Block block, BlockType type) {
        BlockMaterial group = getMainGroup(block);
        if (group == null) return block;
        if (!group.entries.containsKey(type)) return block;
        return group.entries.get(type);
    }

    public static boolean hasUncoloredVariant(Block block) {
        BlockType type = getType(block);
        if (type == null) return false;
        return UNCOLORED.entries.containsKey(type);
    }

    public static BlockState recolor(BlockState state, BlockColor color) {
        List<BlockMaterial> groups = GROUP_LOOKUP.get(state.getBlock());
        if (groups == null) return state;
        if (groups.stream().noneMatch(COLOR_GROUPS::contains)) return state;
        return changeGroup(state, switch (color) {
            case UNCOLORED -> UNCOLORED;
            case WHITE -> WHITE;
            case LIGHT_GRAY -> LIGHT_GRAY;
            case GRAY -> GRAY;
            case BLACK -> BLACK;
            case BROWN -> BROWN;
            case RED -> RED;
            case ORANGE -> ORANGE;
            case YELLOW -> YELLOW;
            case LIME -> LIME;
            case GREEN -> GREEN;
            case CYAN -> CYAN;
            case LIGHT_BLUE -> LIGHT_BLUE;
            case BLUE -> BLUE;
            case PURPLE -> PURPLE;
            case MAGENTA -> MAGENTA;
            case PINK -> PINK;
        });
    }

    public boolean isColorGroup() { return COLOR_GROUPS.contains(this); }

    public static BlockColor getColor(Block block) {
        BlockMaterial group = getMainGroup(block);
        if (group == null) return null;
        return switch (group) {
            case UNCOLORED -> BlockColor.UNCOLORED;
            case WHITE -> BlockColor.WHITE;
            case LIGHT_GRAY -> BlockColor.LIGHT_GRAY;
            case GRAY -> BlockColor.GRAY;
            case BLACK -> BlockColor.BLACK;
            case BROWN -> BlockColor.BROWN;
            case RED -> BlockColor.RED;
            case ORANGE -> BlockColor.ORANGE;
            case YELLOW -> BlockColor.YELLOW;
            case LIME -> BlockColor.LIME;
            case GREEN -> BlockColor.GREEN;
            case CYAN -> BlockColor.CYAN;
            case LIGHT_BLUE -> BlockColor.LIGHT_BLUE;
            case BLUE -> BlockColor.BLUE;
            case PURPLE -> BlockColor.PURPLE;
            case MAGENTA -> BlockColor.MAGENTA;
            case PINK -> BlockColor.PINK;
            default -> null;
        };
    }

    public static BlockMaterial getMainGroup(Block block) {
        List<BlockMaterial> groups = GROUP_LOOKUP.get(block);
        return (groups == null || groups.size() == 0) ? null : groups.get(0);
    }

    public static List<BlockMaterial> getAllGroups(Block block) {
        List<BlockMaterial> groups = GROUP_LOOKUP.get(block);
        return groups == null ? List.of() : groups;
    }

    public static Set<BlockType> getApplicableTypes(Block block) {
        List<BlockMaterial> groups = GROUP_LOOKUP.get(block);
        if (groups == null) return Set.of();
        Set<BlockType> out = new HashSet<>();
        for (BlockMaterial group : groups) {
            out.addAll(group.entries.keySet());
        }
        return out;
    }

    private static <T extends Comparable<T>> BlockState convertState(BlockState from, Block to) {
        BlockState out = to.getDefaultState();
        for (var p : from.getProperties()) {
            try {
                // i dont care that this is ugly i hate wildcards i hate generics i hate java
                Property<T> property = (Property<T>) p;
                if (out.getProperties().contains(property)) {
                    T value = from.get(property);
                    out = out.with(property, value);
                }
            } catch (Exception ignored) { }
        }
        return out;
    }

    static { init(); }
    private static void init() {
        OAK.add(BlockType.FULL_BLOCK, Blocks.OAK_PLANKS);
        OAK.add(BlockType.LOG, Blocks.OAK_LOG);
        OAK.add(BlockType.WOOD, Blocks.OAK_WOOD);
        OAK.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_OAK_LOG);
        OAK.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_OAK_WOOD);
        OAK.add(BlockType.STAIRS, Blocks.OAK_STAIRS);
        OAK.add(BlockType.SLAB, Blocks.OAK_SLAB);
        OAK.add(BlockType.TRAPDOOR, Blocks.OAK_TRAPDOOR);
        OAK.add(BlockType.DOOR, Blocks.OAK_DOOR);
        OAK.add(BlockType.FENCE, Blocks.OAK_FENCE);
        OAK.add(BlockType.PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE);
        OAK.add(BlockType.BUTTON, Blocks.OAK_BUTTON);
        OAK.add(BlockType.STANDING_SIGN, Blocks.OAK_SIGN);
        OAK.add(BlockType.WALL_SIGN, Blocks.OAK_WALL_SIGN);
        OAK.add(BlockType.HANGING_SIGN, Blocks.OAK_HANGING_SIGN);
        OAK.add(BlockType.WALL_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
        OAK.add(BlockType.FENCE_GATE, Blocks.OAK_FENCE_GATE);
        OAK.add(BlockType.LEAVES, Blocks.OAK_LEAVES);
        OAK.add(BlockType.SAPLING, Blocks.OAK_SAPLING);
        OAK.add(BlockType.POTTED_PLANT, Blocks.POTTED_OAK_SAPLING);

        SPRUCE.add(BlockType.FULL_BLOCK, Blocks.SPRUCE_PLANKS);
        SPRUCE.add(BlockType.LOG, Blocks.SPRUCE_LOG);
        SPRUCE.add(BlockType.WOOD, Blocks.SPRUCE_WOOD);
        SPRUCE.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_SPRUCE_LOG);
        SPRUCE.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_SPRUCE_WOOD);
        SPRUCE.add(BlockType.STAIRS, Blocks.SPRUCE_STAIRS);
        SPRUCE.add(BlockType.SLAB, Blocks.SPRUCE_SLAB);
        SPRUCE.add(BlockType.TRAPDOOR, Blocks.SPRUCE_TRAPDOOR);
        SPRUCE.add(BlockType.DOOR, Blocks.SPRUCE_DOOR);
        SPRUCE.add(BlockType.FENCE, Blocks.SPRUCE_FENCE);
        SPRUCE.add(BlockType.PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE);
        SPRUCE.add(BlockType.BUTTON, Blocks.SPRUCE_BUTTON);
        SPRUCE.add(BlockType.STANDING_SIGN, Blocks.SPRUCE_SIGN);
        SPRUCE.add(BlockType.WALL_SIGN, Blocks.SPRUCE_WALL_SIGN);
        SPRUCE.add(BlockType.HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN);
        SPRUCE.add(BlockType.WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
        SPRUCE.add(BlockType.FENCE_GATE, Blocks.SPRUCE_FENCE_GATE);
        SPRUCE.add(BlockType.LEAVES, Blocks.SPRUCE_LEAVES);
        SPRUCE.add(BlockType.SAPLING, Blocks.SPRUCE_SAPLING);
        SPRUCE.add(BlockType.POTTED_PLANT, Blocks.POTTED_SPRUCE_SAPLING);

        BIRCH.add(BlockType.FULL_BLOCK, Blocks.BIRCH_PLANKS);
        BIRCH.add(BlockType.LOG, Blocks.BIRCH_LOG);
        BIRCH.add(BlockType.WOOD, Blocks.BIRCH_WOOD);
        BIRCH.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_BIRCH_LOG);
        BIRCH.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_BIRCH_WOOD);
        BIRCH.add(BlockType.STAIRS, Blocks.BIRCH_STAIRS);
        BIRCH.add(BlockType.SLAB, Blocks.BIRCH_SLAB);
        BIRCH.add(BlockType.TRAPDOOR, Blocks.BIRCH_TRAPDOOR);
        BIRCH.add(BlockType.DOOR, Blocks.BIRCH_DOOR);
        BIRCH.add(BlockType.FENCE, Blocks.BIRCH_FENCE);
        BIRCH.add(BlockType.PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE);
        BIRCH.add(BlockType.BUTTON, Blocks.BIRCH_BUTTON);
        BIRCH.add(BlockType.STANDING_SIGN, Blocks.BIRCH_SIGN);
        BIRCH.add(BlockType.WALL_SIGN, Blocks.BIRCH_WALL_SIGN);
        BIRCH.add(BlockType.HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN);
        BIRCH.add(BlockType.WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
        BIRCH.add(BlockType.FENCE_GATE, Blocks.BIRCH_FENCE_GATE);
        BIRCH.add(BlockType.LEAVES, Blocks.BIRCH_LEAVES);
        BIRCH.add(BlockType.SAPLING, Blocks.BIRCH_SAPLING);
        BIRCH.add(BlockType.POTTED_PLANT, Blocks.POTTED_BIRCH_SAPLING);

        JUNGLE.add(BlockType.FULL_BLOCK, Blocks.JUNGLE_PLANKS);
        JUNGLE.add(BlockType.LOG, Blocks.JUNGLE_LOG);
        JUNGLE.add(BlockType.WOOD, Blocks.JUNGLE_WOOD);
        JUNGLE.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_JUNGLE_LOG);
        JUNGLE.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_JUNGLE_WOOD);
        JUNGLE.add(BlockType.STAIRS, Blocks.JUNGLE_STAIRS);
        JUNGLE.add(BlockType.SLAB, Blocks.JUNGLE_SLAB);
        JUNGLE.add(BlockType.TRAPDOOR, Blocks.JUNGLE_TRAPDOOR);
        JUNGLE.add(BlockType.DOOR, Blocks.JUNGLE_DOOR);
        JUNGLE.add(BlockType.FENCE, Blocks.JUNGLE_FENCE);
        JUNGLE.add(BlockType.PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE);
        JUNGLE.add(BlockType.BUTTON, Blocks.JUNGLE_BUTTON);
        JUNGLE.add(BlockType.STANDING_SIGN, Blocks.JUNGLE_SIGN);
        JUNGLE.add(BlockType.WALL_SIGN, Blocks.JUNGLE_WALL_SIGN);
        JUNGLE.add(BlockType.HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN);
        JUNGLE.add(BlockType.WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
        JUNGLE.add(BlockType.FENCE_GATE, Blocks.JUNGLE_FENCE_GATE);
        JUNGLE.add(BlockType.LEAVES, Blocks.JUNGLE_LEAVES);
        JUNGLE.add(BlockType.SAPLING, Blocks.JUNGLE_SAPLING);
        JUNGLE.add(BlockType.POTTED_PLANT, Blocks.POTTED_JUNGLE_SAPLING);

        ACACIA.add(BlockType.FULL_BLOCK, Blocks.ACACIA_PLANKS);
        ACACIA.add(BlockType.LOG, Blocks.ACACIA_LOG);
        ACACIA.add(BlockType.WOOD, Blocks.ACACIA_WOOD);
        ACACIA.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_ACACIA_LOG);
        ACACIA.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_ACACIA_WOOD);
        ACACIA.add(BlockType.STAIRS, Blocks.ACACIA_STAIRS);
        ACACIA.add(BlockType.SLAB, Blocks.ACACIA_SLAB);
        ACACIA.add(BlockType.TRAPDOOR, Blocks.ACACIA_TRAPDOOR);
        ACACIA.add(BlockType.DOOR, Blocks.ACACIA_DOOR);
        ACACIA.add(BlockType.FENCE, Blocks.ACACIA_FENCE);
        ACACIA.add(BlockType.PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);
        ACACIA.add(BlockType.BUTTON, Blocks.ACACIA_BUTTON);
        ACACIA.add(BlockType.STANDING_SIGN, Blocks.ACACIA_SIGN);
        ACACIA.add(BlockType.WALL_SIGN, Blocks.ACACIA_WALL_SIGN);
        ACACIA.add(BlockType.HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN);
        ACACIA.add(BlockType.WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
        ACACIA.add(BlockType.FENCE_GATE, Blocks.ACACIA_FENCE_GATE);
        ACACIA.add(BlockType.LEAVES, Blocks.ACACIA_LEAVES);
        ACACIA.add(BlockType.SAPLING, Blocks.ACACIA_SAPLING);
        ACACIA.add(BlockType.POTTED_PLANT, Blocks.POTTED_ACACIA_SAPLING);

        DARK_OAK.add(BlockType.FULL_BLOCK, Blocks.DARK_OAK_PLANKS);
        DARK_OAK.add(BlockType.LOG, Blocks.DARK_OAK_LOG);
        DARK_OAK.add(BlockType.WOOD, Blocks.DARK_OAK_WOOD);
        DARK_OAK.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
        DARK_OAK.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD);
        DARK_OAK.add(BlockType.STAIRS, Blocks.DARK_OAK_STAIRS);
        DARK_OAK.add(BlockType.SLAB, Blocks.DARK_OAK_SLAB);
        DARK_OAK.add(BlockType.TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR);
        DARK_OAK.add(BlockType.DOOR, Blocks.DARK_OAK_DOOR);
        DARK_OAK.add(BlockType.FENCE, Blocks.DARK_OAK_FENCE);
        DARK_OAK.add(BlockType.PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE);
        DARK_OAK.add(BlockType.BUTTON, Blocks.DARK_OAK_BUTTON);
        DARK_OAK.add(BlockType.STANDING_SIGN, Blocks.DARK_OAK_SIGN);
        DARK_OAK.add(BlockType.WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN);
        DARK_OAK.add(BlockType.HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN);
        DARK_OAK.add(BlockType.WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
        DARK_OAK.add(BlockType.FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE);
        DARK_OAK.add(BlockType.LEAVES, Blocks.DARK_OAK_LEAVES);
        DARK_OAK.add(BlockType.SAPLING, Blocks.DARK_OAK_SAPLING);
        DARK_OAK.add(BlockType.POTTED_PLANT, Blocks.POTTED_DARK_OAK_SAPLING);

        MANGROVE.add(BlockType.FULL_BLOCK, Blocks.MANGROVE_PLANKS);
        MANGROVE.add(BlockType.LOG, Blocks.MANGROVE_LOG);
        MANGROVE.add(BlockType.WOOD, Blocks.MANGROVE_WOOD);
        MANGROVE.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_MANGROVE_LOG);
        MANGROVE.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_MANGROVE_WOOD);
        MANGROVE.add(BlockType.STAIRS, Blocks.MANGROVE_STAIRS);
        MANGROVE.add(BlockType.SLAB, Blocks.MANGROVE_SLAB);
        MANGROVE.add(BlockType.TRAPDOOR, Blocks.MANGROVE_TRAPDOOR);
        MANGROVE.add(BlockType.DOOR, Blocks.MANGROVE_DOOR);
        MANGROVE.add(BlockType.FENCE, Blocks.MANGROVE_FENCE);
        MANGROVE.add(BlockType.PRESSURE_PLATE, Blocks.MANGROVE_PRESSURE_PLATE);
        MANGROVE.add(BlockType.BUTTON, Blocks.MANGROVE_BUTTON);
        MANGROVE.add(BlockType.STANDING_SIGN, Blocks.MANGROVE_SIGN);
        MANGROVE.add(BlockType.WALL_SIGN, Blocks.MANGROVE_WALL_SIGN);
        MANGROVE.add(BlockType.HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN);
        MANGROVE.add(BlockType.WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
        MANGROVE.add(BlockType.FENCE_GATE, Blocks.MANGROVE_FENCE_GATE);
        MANGROVE.add(BlockType.LEAVES, Blocks.MANGROVE_LEAVES);
        MANGROVE.add(BlockType.SAPLING, Blocks.MANGROVE_PROPAGULE);
        MANGROVE.add(BlockType.POTTED_PLANT, Blocks.POTTED_MANGROVE_PROPAGULE);

        CHERRY.add(BlockType.FULL_BLOCK, Blocks.CHERRY_PLANKS);
        CHERRY.add(BlockType.LOG, Blocks.CHERRY_LOG);
        CHERRY.add(BlockType.WOOD, Blocks.CHERRY_WOOD);
        CHERRY.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_CHERRY_LOG);
        CHERRY.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_CHERRY_WOOD);
        CHERRY.add(BlockType.STAIRS, Blocks.CHERRY_STAIRS);
        CHERRY.add(BlockType.SLAB, Blocks.CHERRY_SLAB);
        CHERRY.add(BlockType.TRAPDOOR, Blocks.CHERRY_TRAPDOOR);
        CHERRY.add(BlockType.DOOR, Blocks.CHERRY_DOOR);
        CHERRY.add(BlockType.FENCE, Blocks.CHERRY_FENCE);
        CHERRY.add(BlockType.PRESSURE_PLATE, Blocks.CHERRY_PRESSURE_PLATE);
        CHERRY.add(BlockType.BUTTON, Blocks.CHERRY_BUTTON);
        CHERRY.add(BlockType.STANDING_SIGN, Blocks.CHERRY_SIGN);
        CHERRY.add(BlockType.WALL_SIGN, Blocks.CHERRY_WALL_SIGN);
        CHERRY.add(BlockType.HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN);
        CHERRY.add(BlockType.WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
        CHERRY.add(BlockType.FENCE_GATE, Blocks.CHERRY_FENCE_GATE);
        CHERRY.add(BlockType.LEAVES, Blocks.CHERRY_LEAVES);
        CHERRY.add(BlockType.SAPLING, Blocks.CHERRY_SAPLING);
        CHERRY.add(BlockType.POTTED_PLANT, Blocks.POTTED_CHERRY_SAPLING);

        BAMBOO.add(BlockType.FULL_BLOCK, Blocks.BAMBOO_PLANKS);
        BAMBOO.add(BlockType.LOG, Blocks.BAMBOO_BLOCK);
        BAMBOO.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_BAMBOO_BLOCK);
        BAMBOO.add(BlockType.STAIRS, Blocks.BAMBOO_STAIRS);
        BAMBOO.add(BlockType.SLAB, Blocks.BAMBOO_SLAB);
        BAMBOO.add(BlockType.TRAPDOOR, Blocks.BAMBOO_TRAPDOOR);
        BAMBOO.add(BlockType.DOOR, Blocks.BAMBOO_DOOR);
        BAMBOO.add(BlockType.FENCE, Blocks.BAMBOO_FENCE);
        BAMBOO.add(BlockType.PRESSURE_PLATE, Blocks.BAMBOO_PRESSURE_PLATE);
        BAMBOO.add(BlockType.BUTTON, Blocks.BAMBOO_BUTTON);
        BAMBOO.add(BlockType.STANDING_SIGN, Blocks.BAMBOO_SIGN);
        BAMBOO.add(BlockType.WALL_SIGN, Blocks.BAMBOO_WALL_SIGN);
        BAMBOO.add(BlockType.HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN);
        BAMBOO.add(BlockType.WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        BAMBOO.add(BlockType.FENCE_GATE, Blocks.BAMBOO_FENCE_GATE);
        BAMBOO.add(BlockType.CUT, Blocks.BAMBOO_MOSAIC);
        BAMBOO.add(BlockType.CUT_STAIRS, Blocks.BAMBOO_MOSAIC_STAIRS);
        BAMBOO.add(BlockType.CUT_SLAB, Blocks.BAMBOO_MOSAIC_SLAB);
        BAMBOO.add(BlockType.SAPLING, Blocks.BAMBOO_SAPLING);
        BAMBOO.add(BlockType.POTTED_PLANT, Blocks.POTTED_BAMBOO);

        AZALEA.add(BlockType.LOG, Blocks.OAK_LOG);
        AZALEA.add(BlockType.LEAVES, Blocks.AZALEA_LEAVES);
        AZALEA.add(BlockType.SAPLING, Blocks.AZALEA);
        AZALEA.add(BlockType.POTTED_PLANT, Blocks.POTTED_AZALEA_BUSH);

        FLOWERING_AZALEA.add(BlockType.LOG, Blocks.OAK_LOG);
        FLOWERING_AZALEA.add(BlockType.LEAVES, Blocks.FLOWERING_AZALEA_LEAVES);
        FLOWERING_AZALEA.add(BlockType.SAPLING, Blocks.FLOWERING_AZALEA);
        FLOWERING_AZALEA.add(BlockType.POTTED_PLANT, Blocks.POTTED_FLOWERING_AZALEA_BUSH);

        CRIMSON.add(BlockType.FULL_BLOCK, Blocks.CRIMSON_PLANKS);
        CRIMSON.add(BlockType.LOG, Blocks.CRIMSON_STEM);
        CRIMSON.add(BlockType.WOOD, Blocks.CRIMSON_HYPHAE);
        CRIMSON.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_CRIMSON_STEM);
        CRIMSON.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_CRIMSON_HYPHAE);
        CRIMSON.add(BlockType.STAIRS, Blocks.CRIMSON_STAIRS);
        CRIMSON.add(BlockType.SLAB, Blocks.CRIMSON_SLAB);
        CRIMSON.add(BlockType.TRAPDOOR, Blocks.CRIMSON_TRAPDOOR);
        CRIMSON.add(BlockType.DOOR, Blocks.CRIMSON_DOOR);
        CRIMSON.add(BlockType.FENCE, Blocks.CRIMSON_FENCE);
        CRIMSON.add(BlockType.PRESSURE_PLATE, Blocks.CRIMSON_PRESSURE_PLATE);
        CRIMSON.add(BlockType.BUTTON, Blocks.CRIMSON_BUTTON);
        CRIMSON.add(BlockType.STANDING_SIGN, Blocks.CRIMSON_SIGN);
        CRIMSON.add(BlockType.WALL_SIGN, Blocks.CRIMSON_WALL_SIGN);
        CRIMSON.add(BlockType.HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN);
        CRIMSON.add(BlockType.WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
        CRIMSON.add(BlockType.FENCE_GATE, Blocks.CRIMSON_FENCE_GATE);
        CRIMSON.add(BlockType.LEAVES, Blocks.NETHER_WART_BLOCK);
        CRIMSON.add(BlockType.SAPLING, Blocks.CRIMSON_FUNGUS);
        CRIMSON.add(BlockType.POTTED_PLANT, Blocks.POTTED_CRIMSON_FUNGUS);
        CRIMSON.add(BlockType.GRASS, Blocks.CRIMSON_ROOTS);
        CRIMSON.add(BlockType.VINES, Blocks.WEEPING_VINES);
        CRIMSON.add(BlockType.DIRT_VARIANT, Blocks.NETHERRACK);
        CRIMSON.add(BlockType.GRASS_BLOCK_VARIANT, Blocks.CRIMSON_NYLIUM);

        WARPED.add(BlockType.FULL_BLOCK, Blocks.WARPED_PLANKS);
        WARPED.add(BlockType.LOG, Blocks.WARPED_STEM);
        WARPED.add(BlockType.WOOD, Blocks.WARPED_HYPHAE);
        WARPED.add(BlockType.STRIPPED_LOG, Blocks.STRIPPED_WARPED_STEM);
        WARPED.add(BlockType.STRIPPED_WOOD, Blocks.STRIPPED_WARPED_HYPHAE);
        WARPED.add(BlockType.STAIRS, Blocks.WARPED_STAIRS);
        WARPED.add(BlockType.SLAB, Blocks.WARPED_SLAB);
        WARPED.add(BlockType.TRAPDOOR, Blocks.WARPED_TRAPDOOR);
        WARPED.add(BlockType.DOOR, Blocks.WARPED_DOOR);
        WARPED.add(BlockType.FENCE, Blocks.WARPED_FENCE);
        WARPED.add(BlockType.PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE);
        WARPED.add(BlockType.BUTTON, Blocks.WARPED_BUTTON);
        WARPED.add(BlockType.STANDING_SIGN, Blocks.WARPED_SIGN);
        WARPED.add(BlockType.WALL_SIGN, Blocks.WARPED_WALL_SIGN);
        WARPED.add(BlockType.HANGING_SIGN, Blocks.WARPED_HANGING_SIGN);
        WARPED.add(BlockType.WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
        WARPED.add(BlockType.FENCE_GATE, Blocks.WARPED_FENCE_GATE);
        WARPED.add(BlockType.LEAVES, Blocks.WARPED_WART_BLOCK);
        WARPED.add(BlockType.SAPLING, Blocks.WARPED_FUNGUS);
        WARPED.add(BlockType.POTTED_PLANT, Blocks.POTTED_WARPED_FUNGUS);
        WARPED.add(BlockType.GRASS, Blocks.WARPED_ROOTS);
        WARPED.add(BlockType.VINES, Blocks.TWISTING_VINES);
        WARPED.add(BlockType.DIRT_VARIANT, Blocks.NETHERRACK);
        WARPED.add(BlockType.GRASS_BLOCK_VARIANT, Blocks.WARPED_NYLIUM);

        OVERWORLD.add(BlockType.GRASS, Blocks.SHORT_GRASS);
        OVERWORLD.add(BlockType.VINES, Blocks.VINE);
        OVERWORLD.add(BlockType.DIRT_VARIANT, Blocks.DIRT);
        OVERWORLD.add(BlockType.GRASS_BLOCK_VARIANT, Blocks.GRASS_BLOCK);
        OVERWORLD.add(BlockType.WOOL, Blocks.MOSS_BLOCK);
        OVERWORLD.add(BlockType.CARPET, Blocks.MOSS_CARPET);

        RED_MUSHROOM.add(BlockType.LOG, Blocks.MUSHROOM_STEM);
        RED_MUSHROOM.add(BlockType.LEAVES, Blocks.RED_MUSHROOM_BLOCK);
        RED_MUSHROOM.add(BlockType.SAPLING, Blocks.RED_MUSHROOM);

        BROWN_MUSHROOM.add(BlockType.LOG, Blocks.MUSHROOM_STEM);
        BROWN_MUSHROOM.add(BlockType.LEAVES, Blocks.BROWN_MUSHROOM_BLOCK);
        BROWN_MUSHROOM.add(BlockType.SAPLING, Blocks.BROWN_MUSHROOM);

        NON_SOUL.add(BlockType.SAND_VARIANT, Blocks.SAND);
        NON_SOUL.add(BlockType.LIGHT_VARIANT, Blocks.SHROOMLIGHT);
        NON_SOUL.add(BlockType.STANDING_TORCH, Blocks.TORCH);
        NON_SOUL.add(BlockType.WALL_TORCH, Blocks.WALL_TORCH);
        NON_SOUL.add(BlockType.LANTERN, Blocks.LANTERN);
        NON_SOUL.add(BlockType.CAMPFIRE, Blocks.CAMPFIRE);

        SOUL.add(BlockType.SAND_VARIANT, Blocks.SOUL_SAND);
        SOUL.add(BlockType.LIGHT_VARIANT, Blocks.SEA_LANTERN);
        SOUL.add(BlockType.STANDING_TORCH, Blocks.SOUL_TORCH);
        SOUL.add(BlockType.WALL_TORCH, Blocks.SOUL_WALL_TORCH);
        SOUL.add(BlockType.LANTERN, Blocks.SOUL_LANTERN);
        SOUL.add(BlockType.CAMPFIRE, Blocks.SOUL_CAMPFIRE);

        IRON.add(BlockType.FULL_BLOCK, Blocks.IRON_BLOCK);
        IRON.add(BlockType.DOOR, Blocks.IRON_DOOR);
        IRON.add(BlockType.FENCE, Blocks.IRON_BARS);
        IRON.add(BlockType.PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        IRON.add(BlockType.STONE_ORE, Blocks.IRON_ORE);
        IRON.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_IRON_ORE);
        IRON.add(BlockType.RAW_ORE_BLOCK, Blocks.RAW_IRON_BLOCK);

        COAL.add(BlockType.FULL_BLOCK, Blocks.COAL_BLOCK);
        COAL.add(BlockType.STONE_ORE, Blocks.COAL_ORE);
        COAL.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_COAL_ORE);

        COPPER.add(BlockType.FULL_BLOCK, Blocks.WAXED_COPPER_BLOCK);
        COPPER.add(BlockType.STONE_ORE, Blocks.COPPER_ORE);
        COPPER.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        COPPER.add(BlockType.RAW_ORE_BLOCK, Blocks.RAW_COPPER_BLOCK);
        COPPER.add(BlockType.CUT, Blocks.WAXED_CUT_COPPER);
        COPPER.add(BlockType.CUT_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS);
        COPPER.add(BlockType.CUT_SLAB, Blocks.WAXED_CUT_COPPER_SLAB);

        GOLD.add(BlockType.FULL_BLOCK, Blocks.GOLD_BLOCK);
        GOLD.add(BlockType.PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        GOLD.add(BlockType.STONE_ORE, Blocks.GOLD_ORE);
        GOLD.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_GOLD_ORE);
        GOLD.add(BlockType.NETHER_ORE, Blocks.NETHER_GOLD_ORE);
        GOLD.add(BlockType.RAW_ORE_BLOCK, Blocks.RAW_GOLD_BLOCK);

        REDSTONE.add(BlockType.FULL_BLOCK, Blocks.REDSTONE_BLOCK);
        REDSTONE.add(BlockType.STONE_ORE, Blocks.REDSTONE_ORE);
        REDSTONE.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        REDSTONE.add(BlockType.LIGHT_VARIANT, Blocks.REDSTONE_LAMP);

        EMERALD.add(BlockType.FULL_BLOCK, Blocks.EMERALD_BLOCK);
        EMERALD.add(BlockType.STONE_ORE, Blocks.EMERALD_ORE);
        EMERALD.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_EMERALD_ORE);

        LAPIS.add(BlockType.FULL_BLOCK, Blocks.LAPIS_BLOCK);
        LAPIS.add(BlockType.STONE_ORE, Blocks.LAPIS_ORE);
        LAPIS.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_LAPIS_ORE);

        DIAMOND.add(BlockType.FULL_BLOCK, Blocks.DIAMOND_BLOCK);
        DIAMOND.add(BlockType.STONE_ORE, Blocks.DIAMOND_ORE);
        DIAMOND.add(BlockType.DEEPSLATE_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);

        QUARTZ.add(BlockType.FULL_BLOCK, Blocks.QUARTZ_BLOCK);
        QUARTZ.add(BlockType.STAIRS, Blocks.QUARTZ_STAIRS);
        QUARTZ.add(BlockType.SLAB, Blocks.QUARTZ_SLAB);
        QUARTZ.add(BlockType.NETHER_ORE, Blocks.NETHER_QUARTZ_ORE);
        QUARTZ.add(BlockType.CHISELED, Blocks.CHISELED_QUARTZ_BLOCK);
        QUARTZ.add(BlockType.CUT, Blocks.QUARTZ_BRICKS);
        QUARTZ.add(BlockType.PILLAR, Blocks.QUARTZ_PILLAR);

        NETHERITE.add(BlockType.FULL_BLOCK, Blocks.NETHERITE_BLOCK);
        NETHERITE.add(BlockType.NETHER_ORE, Blocks.ANCIENT_DEBRIS);

        STONE_BRICKS.add(BlockType.FULL_BLOCK, Blocks.STONE_BRICKS);
        STONE_BRICKS.add(BlockType.STAIRS, Blocks.STONE_BRICK_STAIRS);
        STONE_BRICKS.add(BlockType.SLAB, Blocks.STONE_BRICK_SLAB);
        STONE_BRICKS.add(BlockType.WALL, Blocks.STONE_BRICK_WALL);
        STONE_BRICKS.add(BlockType.CHISELED, Blocks.CHISELED_STONE_BRICKS);
        STONE_BRICKS.add(BlockType.CRACKED, Blocks.CRACKED_STONE_BRICKS);

        MOSSY_STONE_BRICKS.add(BlockType.FULL_BLOCK, Blocks.MOSSY_STONE_BRICKS);
        MOSSY_STONE_BRICKS.add(BlockType.STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
        MOSSY_STONE_BRICKS.add(BlockType.SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
        MOSSY_STONE_BRICKS.add(BlockType.WALL, Blocks.MOSSY_STONE_BRICK_WALL);

        DEEPSLATE_BRICKS.add(BlockType.FULL_BLOCK, Blocks.DEEPSLATE_BRICKS);
        DEEPSLATE_BRICKS.add(BlockType.STAIRS, Blocks.DEEPSLATE_BRICK_STAIRS);
        DEEPSLATE_BRICKS.add(BlockType.SLAB, Blocks.DEEPSLATE_BRICK_SLAB);
        DEEPSLATE_BRICKS.add(BlockType.WALL, Blocks.DEEPSLATE_BRICK_WALL);
        DEEPSLATE_BRICKS.add(BlockType.CHISELED, Blocks.CHISELED_DEEPSLATE);
        DEEPSLATE_BRICKS.add(BlockType.CRACKED, Blocks.CRACKED_DEEPSLATE_BRICKS);

        NETHER_BRICKS.add(BlockType.FULL_BLOCK, Blocks.NETHER_BRICKS);
        NETHER_BRICKS.add(BlockType.STAIRS, Blocks.NETHER_BRICK_STAIRS);
        NETHER_BRICKS.add(BlockType.SLAB, Blocks.NETHER_BRICK_SLAB);
        NETHER_BRICKS.add(BlockType.FENCE, Blocks.NETHER_BRICK_FENCE);
        NETHER_BRICKS.add(BlockType.WALL, Blocks.NETHER_BRICK_WALL);
        NETHER_BRICKS.add(BlockType.CHISELED, Blocks.CHISELED_NETHER_BRICKS);
        NETHER_BRICKS.add(BlockType.CRACKED, Blocks.CRACKED_NETHER_BRICKS);

        RED_NETHER_BRICKS.add(BlockType.FULL_BLOCK, Blocks.RED_NETHER_BRICKS);
        RED_NETHER_BRICKS.add(BlockType.STAIRS, Blocks.RED_NETHER_BRICK_STAIRS);
        RED_NETHER_BRICKS.add(BlockType.SLAB, Blocks.RED_NETHER_BRICK_SLAB);
        RED_NETHER_BRICKS.add(BlockType.WALL, Blocks.RED_NETHER_BRICK_WALL);

        SANDSTONE.add(BlockType.FULL_BLOCK, Blocks.SANDSTONE);
        SANDSTONE.add(BlockType.STAIRS, Blocks.SANDSTONE_STAIRS);
        SANDSTONE.add(BlockType.SLAB, Blocks.SANDSTONE_SLAB);
        SANDSTONE.add(BlockType.WALL, Blocks.SANDSTONE_WALL);
        SANDSTONE.add(BlockType.CHISELED, Blocks.CHISELED_SANDSTONE);
        SANDSTONE.add(BlockType.CUT, Blocks.CUT_SANDSTONE);
        SANDSTONE.add(BlockType.CUT_SLAB, Blocks.CUT_SANDSTONE_SLAB);
        SANDSTONE.add(BlockType.SAND_VARIANT, Blocks.SAND);

        RED_SANDSTONE.add(BlockType.FULL_BLOCK, Blocks.RED_SANDSTONE);
        RED_SANDSTONE.add(BlockType.STAIRS, Blocks.RED_SANDSTONE_STAIRS);
        RED_SANDSTONE.add(BlockType.SLAB, Blocks.RED_SANDSTONE_SLAB);
        RED_SANDSTONE.add(BlockType.WALL, Blocks.RED_SANDSTONE_WALL);
        RED_SANDSTONE.add(BlockType.CHISELED, Blocks.CHISELED_RED_SANDSTONE);
        RED_SANDSTONE.add(BlockType.CUT, Blocks.CUT_RED_SANDSTONE);
        RED_SANDSTONE.add(BlockType.CUT_SLAB, Blocks.CUT_RED_SANDSTONE_SLAB);
        RED_SANDSTONE.add(BlockType.SAND_VARIANT, Blocks.RED_SAND);

        COBBLED_DEEPSLATE.add(BlockType.FULL_BLOCK, Blocks.COBBLED_DEEPSLATE);
        COBBLED_DEEPSLATE.add(BlockType.STAIRS, Blocks.COBBLED_DEEPSLATE_STAIRS);
        COBBLED_DEEPSLATE.add(BlockType.SLAB, Blocks.COBBLED_DEEPSLATE_SLAB);
        COBBLED_DEEPSLATE.add(BlockType.WALL, Blocks.COBBLED_DEEPSLATE_WALL);

        POLISHED_DEEPSLATE.add(BlockType.FULL_BLOCK, Blocks.POLISHED_DEEPSLATE);
        POLISHED_DEEPSLATE.add(BlockType.STAIRS, Blocks.POLISHED_DEEPSLATE_STAIRS);
        POLISHED_DEEPSLATE.add(BlockType.SLAB, Blocks.POLISHED_DEEPSLATE_SLAB);
        POLISHED_DEEPSLATE.add(BlockType.WALL, Blocks.POLISHED_DEEPSLATE_WALL);

        DEEPSLATE_TILES.add(BlockType.FULL_BLOCK, Blocks.DEEPSLATE_TILES);
        DEEPSLATE_TILES.add(BlockType.STAIRS, Blocks.DEEPSLATE_TILE_STAIRS);
        DEEPSLATE_TILES.add(BlockType.SLAB, Blocks.DEEPSLATE_TILE_SLAB);
        DEEPSLATE_TILES.add(BlockType.WALL, Blocks.DEEPSLATE_TILE_WALL);
        DEEPSLATE_TILES.add(BlockType.CRACKED, Blocks.CRACKED_DEEPSLATE_TILES);

        BLACKSTONE.add(BlockType.FULL_BLOCK, Blocks.BLACKSTONE);
        BLACKSTONE.add(BlockType.STAIRS, Blocks.BLACKSTONE_STAIRS);
        BLACKSTONE.add(BlockType.SLAB, Blocks.BLACKSTONE_SLAB);
        BLACKSTONE.add(BlockType.WALL, Blocks.BLACKSTONE_WALL);
        BLACKSTONE.add(BlockType.NETHER_ORE, Blocks.GILDED_BLACKSTONE);

        POLISHED_BLACKSTONE.add(BlockType.FULL_BLOCK, Blocks.POLISHED_BLACKSTONE);
        POLISHED_BLACKSTONE.add(BlockType.STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
        POLISHED_BLACKSTONE.add(BlockType.SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        POLISHED_BLACKSTONE.add(BlockType.WALL, Blocks.POLISHED_BLACKSTONE_WALL);
        POLISHED_BLACKSTONE.add(BlockType.PRESSURE_PLATE, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        POLISHED_BLACKSTONE.add(BlockType.BUTTON, Blocks.POLISHED_BLACKSTONE_BUTTON);
        POLISHED_BLACKSTONE.add(BlockType.CHISELED, Blocks.CHISELED_POLISHED_BLACKSTONE);

        STONE.add(BlockType.FULL_BLOCK, Blocks.STONE);
        STONE.add(BlockType.STAIRS, Blocks.STONE_STAIRS);
        STONE.add(BlockType.SLAB, Blocks.STONE_SLAB);
        STONE.add(BlockType.CUT, Blocks.SMOOTH_STONE);
        STONE.add(BlockType.CUT_SLAB, Blocks.SMOOTH_STONE_SLAB);
        STONE.add(BlockType.BUTTON, Blocks.STONE_BUTTON);
        STONE.add(BlockType.PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE);

        COBBLESTONE.add(BlockType.FULL_BLOCK, Blocks.COBBLESTONE);
        COBBLESTONE.add(BlockType.STAIRS, Blocks.COBBLESTONE_STAIRS);
        COBBLESTONE.add(BlockType.SLAB, Blocks.COBBLESTONE_SLAB);
        COBBLESTONE.add(BlockType.WALL, Blocks.COBBLESTONE_WALL);

        MOSSY_COBBLESTONE.add(BlockType.FULL_BLOCK, Blocks.MOSSY_COBBLESTONE);
        MOSSY_COBBLESTONE.add(BlockType.STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS);
        MOSSY_COBBLESTONE.add(BlockType.SLAB, Blocks.MOSSY_COBBLESTONE_SLAB);
        MOSSY_COBBLESTONE.add(BlockType.WALL, Blocks.MOSSY_COBBLESTONE_WALL);

        GRANITE.add(BlockType.FULL_BLOCK, Blocks.GRANITE);
        GRANITE.add(BlockType.STAIRS, Blocks.GRANITE_STAIRS);
        GRANITE.add(BlockType.SLAB, Blocks.GRANITE_SLAB);
        GRANITE.add(BlockType.WALL, Blocks.GRANITE_WALL);
        GRANITE.add(BlockType.CUT, Blocks.POLISHED_GRANITE);
        GRANITE.add(BlockType.CUT_STAIRS, Blocks.POLISHED_GRANITE_STAIRS);
        GRANITE.add(BlockType.CUT_SLAB, Blocks.POLISHED_GRANITE_SLAB);

        DIORITE.add(BlockType.FULL_BLOCK, Blocks.DIORITE);
        DIORITE.add(BlockType.STAIRS, Blocks.DIORITE_STAIRS);
        DIORITE.add(BlockType.SLAB, Blocks.DIORITE_SLAB);
        DIORITE.add(BlockType.WALL, Blocks.DIORITE_WALL);
        DIORITE.add(BlockType.CUT, Blocks.POLISHED_DIORITE);
        DIORITE.add(BlockType.CUT_STAIRS, Blocks.POLISHED_DIORITE_STAIRS);
        DIORITE.add(BlockType.CUT_SLAB, Blocks.POLISHED_DIORITE_SLAB);

        ANDESITE.add(BlockType.FULL_BLOCK, Blocks.ANDESITE);
        ANDESITE.add(BlockType.STAIRS, Blocks.ANDESITE_STAIRS);
        ANDESITE.add(BlockType.SLAB, Blocks.ANDESITE_SLAB);
        ANDESITE.add(BlockType.WALL, Blocks.ANDESITE_WALL);
        ANDESITE.add(BlockType.CUT, Blocks.POLISHED_ANDESITE);
        ANDESITE.add(BlockType.CUT_STAIRS, Blocks.POLISHED_ANDESITE_STAIRS);
        ANDESITE.add(BlockType.CUT_SLAB, Blocks.POLISHED_ANDESITE_SLAB);

        BRICKS.add(BlockType.FULL_BLOCK, Blocks.BRICKS);
        BRICKS.add(BlockType.STAIRS, Blocks.BRICK_STAIRS);
        BRICKS.add(BlockType.SLAB, Blocks.BRICK_SLAB);
        BRICKS.add(BlockType.WALL, Blocks.BRICK_WALL);

        MUD.add(BlockType.FULL_BLOCK, Blocks.MUD_BRICKS);
        MUD.add(BlockType.STAIRS, Blocks.MUD_BRICK_STAIRS);
        MUD.add(BlockType.SLAB, Blocks.MUD_BRICK_SLAB);
        MUD.add(BlockType.WALL, Blocks.MUD_BRICK_WALL);

        SMOOTH_SANDSTONE.add(BlockType.FULL_BLOCK, Blocks.SMOOTH_SANDSTONE);
        SMOOTH_SANDSTONE.add(BlockType.STAIRS, Blocks.SMOOTH_SANDSTONE_STAIRS);
        SMOOTH_SANDSTONE.add(BlockType.SLAB, Blocks.SMOOTH_SANDSTONE_SLAB);

        SMOOTH_RED_SANDSTONE.add(BlockType.FULL_BLOCK, Blocks.SMOOTH_RED_SANDSTONE);
        SMOOTH_RED_SANDSTONE.add(BlockType.STAIRS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        SMOOTH_RED_SANDSTONE.add(BlockType.SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB);

        PRISMARINE.add(BlockType.FULL_BLOCK, Blocks.PRISMARINE);
        PRISMARINE.add(BlockType.STAIRS, Blocks.PRISMARINE_STAIRS);
        PRISMARINE.add(BlockType.SLAB, Blocks.PRISMARINE_SLAB);
        PRISMARINE.add(BlockType.WALL, Blocks.PRISMARINE_WALL);
        PRISMARINE.add(BlockType.CUT, Blocks.PRISMARINE_BRICKS);
        PRISMARINE.add(BlockType.CUT_STAIRS, Blocks.PRISMARINE_BRICK_STAIRS);
        PRISMARINE.add(BlockType.CUT_SLAB, Blocks.PRISMARINE_BRICK_SLAB);

        DARK_PRISMARINE.add(BlockType.FULL_BLOCK, Blocks.DARK_PRISMARINE);
        DARK_PRISMARINE.add(BlockType.STAIRS, Blocks.DARK_PRISMARINE_STAIRS);
        DARK_PRISMARINE.add(BlockType.SLAB, Blocks.DARK_PRISMARINE_SLAB);

        END.add(BlockType.FULL_BLOCK, Blocks.END_STONE_BRICKS);
        END.add(BlockType.STAIRS, Blocks.END_STONE_BRICK_STAIRS);
        END.add(BlockType.SLAB, Blocks.END_STONE_BRICK_SLAB);
        END.add(BlockType.WALL, Blocks.END_STONE_BRICK_WALL);

        PURPUR.add(BlockType.FULL_BLOCK, Blocks.PURPUR_BLOCK);
        PURPUR.add(BlockType.STAIRS, Blocks.PURPUR_STAIRS);
        PURPUR.add(BlockType.SLAB, Blocks.PURPUR_SLAB);
        PURPUR.add(BlockType.PILLAR, Blocks.PURPUR_PILLAR);

        BASALT.add(BlockType.FULL_BLOCK, Blocks.SMOOTH_BASALT);
        BASALT.add(BlockType.LOG, Blocks.BASALT);
        BASALT.add(BlockType.STRIPPED_LOG, Blocks.POLISHED_BASALT);

        // colored blocks

        UNCOLORED.add(BlockType.TERRACOTTA, Blocks.TERRACOTTA);
        UNCOLORED.add(BlockType.GLASS, Blocks.GLASS);
        UNCOLORED.add(BlockType.GLASS_PANE, Blocks.GLASS_PANE);
        UNCOLORED.add(BlockType.SHULKER_BOX, Blocks.SHULKER_BOX);
        UNCOLORED.add(BlockType.CANDLE, Blocks.CANDLE);

        WHITE.add(BlockType.CONCRETE, Blocks.WHITE_CONCRETE);
        WHITE.add(BlockType.CONCRETE_POWDER, Blocks.WHITE_CONCRETE_POWDER);
        WHITE.add(BlockType.WOOL, Blocks.WHITE_WOOL);
        WHITE.add(BlockType.CARPET, Blocks.WHITE_CARPET);
        WHITE.add(BlockType.TERRACOTTA, Blocks.WHITE_TERRACOTTA);
        WHITE.add(BlockType.GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA);
        WHITE.add(BlockType.GLASS, Blocks.WHITE_STAINED_GLASS);
        WHITE.add(BlockType.GLASS_PANE, Blocks.WHITE_STAINED_GLASS_PANE);
        WHITE.add(BlockType.SHULKER_BOX, Blocks.WHITE_SHULKER_BOX);
        WHITE.add(BlockType.BED, Blocks.WHITE_BED);
        WHITE.add(BlockType.CANDLE, Blocks.WHITE_CANDLE);
        WHITE.add(BlockType.STANDING_BANNER, Blocks.WHITE_BANNER);
        WHITE.add(BlockType.WALL_BANNER, Blocks.WHITE_WALL_BANNER);

        LIGHT_GRAY.add(BlockType.CONCRETE, Blocks.LIGHT_GRAY_CONCRETE);
        LIGHT_GRAY.add(BlockType.CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        LIGHT_GRAY.add(BlockType.WOOL, Blocks.LIGHT_GRAY_WOOL);
        LIGHT_GRAY.add(BlockType.CARPET, Blocks.LIGHT_GRAY_CARPET);
        LIGHT_GRAY.add(BlockType.TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA);
        LIGHT_GRAY.add(BlockType.GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        LIGHT_GRAY.add(BlockType.GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS);
        LIGHT_GRAY.add(BlockType.GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        LIGHT_GRAY.add(BlockType.SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX);
        LIGHT_GRAY.add(BlockType.BED, Blocks.LIGHT_GRAY_BED);
        LIGHT_GRAY.add(BlockType.CANDLE, Blocks.LIGHT_GRAY_CANDLE);
        LIGHT_GRAY.add(BlockType.STANDING_BANNER, Blocks.LIGHT_GRAY_BANNER);
        LIGHT_GRAY.add(BlockType.WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER);

        GRAY.add(BlockType.CONCRETE, Blocks.GRAY_CONCRETE);
        GRAY.add(BlockType.CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER);
        GRAY.add(BlockType.WOOL, Blocks.GRAY_WOOL);
        GRAY.add(BlockType.CARPET, Blocks.GRAY_CARPET);
        GRAY.add(BlockType.TERRACOTTA, Blocks.GRAY_TERRACOTTA);
        GRAY.add(BlockType.GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA);
        GRAY.add(BlockType.GLASS, Blocks.GRAY_STAINED_GLASS);
        GRAY.add(BlockType.GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE);
        GRAY.add(BlockType.SHULKER_BOX, Blocks.GRAY_SHULKER_BOX);
        GRAY.add(BlockType.BED, Blocks.GRAY_BED);
        GRAY.add(BlockType.CANDLE, Blocks.GRAY_CANDLE);
        GRAY.add(BlockType.STANDING_BANNER, Blocks.GRAY_BANNER);
        GRAY.add(BlockType.WALL_BANNER, Blocks.GRAY_WALL_BANNER);

        BLACK.add(BlockType.CONCRETE, Blocks.BLACK_CONCRETE);
        BLACK.add(BlockType.CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
        BLACK.add(BlockType.WOOL, Blocks.BLACK_WOOL);
        BLACK.add(BlockType.CARPET, Blocks.BLACK_CARPET);
        BLACK.add(BlockType.TERRACOTTA, Blocks.BLACK_TERRACOTTA);
        BLACK.add(BlockType.GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        BLACK.add(BlockType.GLASS, Blocks.BLACK_STAINED_GLASS);
        BLACK.add(BlockType.GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE);
        BLACK.add(BlockType.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        BLACK.add(BlockType.BED, Blocks.BLACK_BED);
        BLACK.add(BlockType.CANDLE, Blocks.BLACK_CANDLE);
        BLACK.add(BlockType.STANDING_BANNER, Blocks.BLACK_BANNER);
        BLACK.add(BlockType.WALL_BANNER, Blocks.BLACK_WALL_BANNER);

        BROWN.add(BlockType.CONCRETE, Blocks.BROWN_CONCRETE);
        BROWN.add(BlockType.CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER);
        BROWN.add(BlockType.WOOL, Blocks.BROWN_WOOL);
        BROWN.add(BlockType.CARPET, Blocks.BROWN_CARPET);
        BROWN.add(BlockType.TERRACOTTA, Blocks.BROWN_TERRACOTTA);
        BROWN.add(BlockType.GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA);
        BROWN.add(BlockType.GLASS, Blocks.BROWN_STAINED_GLASS);
        BROWN.add(BlockType.GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE);
        BROWN.add(BlockType.SHULKER_BOX, Blocks.BROWN_SHULKER_BOX);
        BROWN.add(BlockType.BED, Blocks.BROWN_BED);
        BROWN.add(BlockType.CANDLE, Blocks.BROWN_CANDLE);
        BROWN.add(BlockType.STANDING_BANNER, Blocks.BROWN_BANNER);
        BROWN.add(BlockType.WALL_BANNER, Blocks.BROWN_WALL_BANNER);

        RED.add(BlockType.CONCRETE, Blocks.RED_CONCRETE);
        RED.add(BlockType.CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER);
        RED.add(BlockType.WOOL, Blocks.RED_WOOL);
        RED.add(BlockType.CARPET, Blocks.RED_CARPET);
        RED.add(BlockType.TERRACOTTA, Blocks.RED_TERRACOTTA);
        RED.add(BlockType.GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA);
        RED.add(BlockType.GLASS, Blocks.RED_STAINED_GLASS);
        RED.add(BlockType.GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE);
        RED.add(BlockType.SHULKER_BOX, Blocks.RED_SHULKER_BOX);
        RED.add(BlockType.BED, Blocks.RED_BED);
        RED.add(BlockType.CANDLE, Blocks.RED_CANDLE);
        RED.add(BlockType.STANDING_BANNER, Blocks.RED_BANNER);
        RED.add(BlockType.WALL_BANNER, Blocks.RED_WALL_BANNER);

        ORANGE.add(BlockType.CONCRETE, Blocks.ORANGE_CONCRETE);
        ORANGE.add(BlockType.CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER);
        ORANGE.add(BlockType.WOOL, Blocks.ORANGE_WOOL);
        ORANGE.add(BlockType.CARPET, Blocks.ORANGE_CARPET);
        ORANGE.add(BlockType.TERRACOTTA, Blocks.ORANGE_TERRACOTTA);
        ORANGE.add(BlockType.GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA);
        ORANGE.add(BlockType.GLASS, Blocks.ORANGE_STAINED_GLASS);
        ORANGE.add(BlockType.GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE);
        ORANGE.add(BlockType.SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX);
        ORANGE.add(BlockType.BED, Blocks.ORANGE_BED);
        ORANGE.add(BlockType.CANDLE, Blocks.ORANGE_CANDLE);
        ORANGE.add(BlockType.STANDING_BANNER, Blocks.ORANGE_BANNER);
        ORANGE.add(BlockType.WALL_BANNER, Blocks.ORANGE_WALL_BANNER);

        YELLOW.add(BlockType.CONCRETE, Blocks.YELLOW_CONCRETE);
        YELLOW.add(BlockType.CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER);
        YELLOW.add(BlockType.WOOL, Blocks.YELLOW_WOOL);
        YELLOW.add(BlockType.CARPET, Blocks.YELLOW_CARPET);
        YELLOW.add(BlockType.TERRACOTTA, Blocks.YELLOW_TERRACOTTA);
        YELLOW.add(BlockType.GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA);
        YELLOW.add(BlockType.GLASS, Blocks.YELLOW_STAINED_GLASS);
        YELLOW.add(BlockType.GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE);
        YELLOW.add(BlockType.SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
        YELLOW.add(BlockType.BED, Blocks.YELLOW_BED);
        YELLOW.add(BlockType.CANDLE, Blocks.YELLOW_CANDLE);
        YELLOW.add(BlockType.STANDING_BANNER, Blocks.YELLOW_BANNER);
        YELLOW.add(BlockType.WALL_BANNER, Blocks.YELLOW_WALL_BANNER);

        LIME.add(BlockType.CONCRETE, Blocks.LIME_CONCRETE);
        LIME.add(BlockType.CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER);
        LIME.add(BlockType.WOOL, Blocks.LIME_WOOL);
        LIME.add(BlockType.CARPET, Blocks.LIME_CARPET);
        LIME.add(BlockType.TERRACOTTA, Blocks.LIME_TERRACOTTA);
        LIME.add(BlockType.GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA);
        LIME.add(BlockType.GLASS, Blocks.LIME_STAINED_GLASS);
        LIME.add(BlockType.GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE);
        LIME.add(BlockType.SHULKER_BOX, Blocks.LIME_SHULKER_BOX);
        LIME.add(BlockType.BED, Blocks.LIME_BED);
        LIME.add(BlockType.CANDLE, Blocks.LIME_CANDLE);
        LIME.add(BlockType.STANDING_BANNER, Blocks.LIME_BANNER);
        LIME.add(BlockType.WALL_BANNER, Blocks.LIME_WALL_BANNER);

        GREEN.add(BlockType.CONCRETE, Blocks.GREEN_CONCRETE);
        GREEN.add(BlockType.CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER);
        GREEN.add(BlockType.WOOL, Blocks.GREEN_WOOL);
        GREEN.add(BlockType.CARPET, Blocks.GREEN_CARPET);
        GREEN.add(BlockType.TERRACOTTA, Blocks.GREEN_TERRACOTTA);
        GREEN.add(BlockType.GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA);
        GREEN.add(BlockType.GLASS, Blocks.GREEN_STAINED_GLASS);
        GREEN.add(BlockType.GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE);
        GREEN.add(BlockType.SHULKER_BOX, Blocks.GREEN_SHULKER_BOX);
        GREEN.add(BlockType.BED, Blocks.GREEN_BED);
        GREEN.add(BlockType.CANDLE, Blocks.GREEN_CANDLE);
        GREEN.add(BlockType.STANDING_BANNER, Blocks.GREEN_BANNER);
        GREEN.add(BlockType.WALL_BANNER, Blocks.GREEN_WALL_BANNER);

        CYAN.add(BlockType.CONCRETE, Blocks.CYAN_CONCRETE);
        CYAN.add(BlockType.CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER);
        CYAN.add(BlockType.WOOL, Blocks.CYAN_WOOL);
        CYAN.add(BlockType.CARPET, Blocks.CYAN_CARPET);
        CYAN.add(BlockType.TERRACOTTA, Blocks.CYAN_TERRACOTTA);
        CYAN.add(BlockType.GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA);
        CYAN.add(BlockType.GLASS, Blocks.CYAN_STAINED_GLASS);
        CYAN.add(BlockType.GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE);
        CYAN.add(BlockType.SHULKER_BOX, Blocks.CYAN_SHULKER_BOX);
        CYAN.add(BlockType.BED, Blocks.CYAN_BED);
        CYAN.add(BlockType.CANDLE, Blocks.CYAN_CANDLE);
        CYAN.add(BlockType.STANDING_BANNER, Blocks.CYAN_BANNER);
        CYAN.add(BlockType.WALL_BANNER, Blocks.CYAN_WALL_BANNER);

        LIGHT_BLUE.add(BlockType.CONCRETE, Blocks.LIGHT_BLUE_CONCRETE);
        LIGHT_BLUE.add(BlockType.CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        LIGHT_BLUE.add(BlockType.WOOL, Blocks.LIGHT_BLUE_WOOL);
        LIGHT_BLUE.add(BlockType.CARPET, Blocks.LIGHT_BLUE_CARPET);
        LIGHT_BLUE.add(BlockType.TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA);
        LIGHT_BLUE.add(BlockType.GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        LIGHT_BLUE.add(BlockType.GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS);
        LIGHT_BLUE.add(BlockType.GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        LIGHT_BLUE.add(BlockType.SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX);
        LIGHT_BLUE.add(BlockType.BED, Blocks.LIGHT_BLUE_BED);
        LIGHT_BLUE.add(BlockType.CANDLE, Blocks.LIGHT_BLUE_CANDLE);
        LIGHT_BLUE.add(BlockType.STANDING_BANNER, Blocks.LIGHT_BLUE_BANNER);
        LIGHT_BLUE.add(BlockType.WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER);

        BLUE.add(BlockType.CONCRETE, Blocks.BLUE_CONCRETE);
        BLUE.add(BlockType.CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER);
        BLUE.add(BlockType.WOOL, Blocks.BLUE_WOOL);
        BLUE.add(BlockType.CARPET, Blocks.BLUE_CARPET);
        BLUE.add(BlockType.TERRACOTTA, Blocks.BLUE_TERRACOTTA);
        BLUE.add(BlockType.GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA);
        BLUE.add(BlockType.GLASS, Blocks.BLUE_STAINED_GLASS);
        BLUE.add(BlockType.GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE);
        BLUE.add(BlockType.SHULKER_BOX, Blocks.BLUE_SHULKER_BOX);
        BLUE.add(BlockType.BED, Blocks.BLUE_BED);
        BLUE.add(BlockType.CANDLE, Blocks.BLUE_CANDLE);
        BLUE.add(BlockType.STANDING_BANNER, Blocks.BLUE_BANNER);
        BLUE.add(BlockType.WALL_BANNER, Blocks.BLUE_WALL_BANNER);

        PURPLE.add(BlockType.CONCRETE, Blocks.PURPLE_CONCRETE);
        PURPLE.add(BlockType.CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER);
        PURPLE.add(BlockType.WOOL, Blocks.PURPLE_WOOL);
        PURPLE.add(BlockType.CARPET, Blocks.PURPLE_CARPET);
        PURPLE.add(BlockType.TERRACOTTA, Blocks.PURPLE_TERRACOTTA);
        PURPLE.add(BlockType.GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA);
        PURPLE.add(BlockType.GLASS, Blocks.PURPLE_STAINED_GLASS);
        PURPLE.add(BlockType.GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE);
        PURPLE.add(BlockType.SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX);
        PURPLE.add(BlockType.BED, Blocks.PURPLE_BED);
        PURPLE.add(BlockType.CANDLE, Blocks.PURPLE_CANDLE);
        PURPLE.add(BlockType.STANDING_BANNER, Blocks.PURPLE_BANNER);
        PURPLE.add(BlockType.WALL_BANNER, Blocks.PURPLE_WALL_BANNER);

        MAGENTA.add(BlockType.CONCRETE, Blocks.MAGENTA_CONCRETE);
        MAGENTA.add(BlockType.CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER);
        MAGENTA.add(BlockType.WOOL, Blocks.MAGENTA_WOOL);
        MAGENTA.add(BlockType.CARPET, Blocks.MAGENTA_CARPET);
        MAGENTA.add(BlockType.TERRACOTTA, Blocks.MAGENTA_TERRACOTTA);
        MAGENTA.add(BlockType.GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA);
        MAGENTA.add(BlockType.GLASS, Blocks.MAGENTA_STAINED_GLASS);
        MAGENTA.add(BlockType.GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE);
        MAGENTA.add(BlockType.SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX);
        MAGENTA.add(BlockType.BED, Blocks.MAGENTA_BED);
        MAGENTA.add(BlockType.CANDLE, Blocks.MAGENTA_CANDLE);
        MAGENTA.add(BlockType.STANDING_BANNER, Blocks.MAGENTA_BANNER);
        MAGENTA.add(BlockType.WALL_BANNER, Blocks.MAGENTA_WALL_BANNER);

        PINK.add(BlockType.CONCRETE, Blocks.PINK_CONCRETE);
        PINK.add(BlockType.CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER);
        PINK.add(BlockType.WOOL, Blocks.PINK_WOOL);
        PINK.add(BlockType.CARPET, Blocks.PINK_CARPET);
        PINK.add(BlockType.TERRACOTTA, Blocks.PINK_TERRACOTTA);
        PINK.add(BlockType.GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA);
        PINK.add(BlockType.GLASS, Blocks.PINK_STAINED_GLASS);
        PINK.add(BlockType.GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE);
        PINK.add(BlockType.SHULKER_BOX, Blocks.PINK_SHULKER_BOX);
        PINK.add(BlockType.BED, Blocks.PINK_BED);
        PINK.add(BlockType.CANDLE, Blocks.PINK_CANDLE);
        PINK.add(BlockType.STANDING_BANNER, Blocks.PINK_BANNER);
        PINK.add(BlockType.WALL_BANNER, Blocks.PINK_WALL_BANNER);

        // light blocks

        YELLOW.add(BlockType.LIGHT_VARIANT, Blocks.OCHRE_FROGLIGHT);
        LIME.add(BlockType.LIGHT_VARIANT, Blocks.VERDANT_FROGLIGHT);
        PURPLE.add(BlockType.LIGHT_VARIANT, Blocks.PEARLESCENT_FROGLIGHT);

        // corals

        RED.add(BlockType.CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK);
        RED.add(BlockType.CORAL, Blocks.FIRE_CORAL);
        RED.add(BlockType.CORAL_FAN, Blocks.FIRE_CORAL_FAN);
        RED.add(BlockType.DEAD_CORAL, Blocks.DEAD_FIRE_CORAL_BLOCK);
        RED.add(BlockType.DEAD_CORAL, Blocks.DEAD_FIRE_CORAL);
        RED.add(BlockType.DEAD_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN);

        YELLOW.add(BlockType.CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK);
        YELLOW.add(BlockType.CORAL, Blocks.HORN_CORAL);
        YELLOW.add(BlockType.CORAL_FAN, Blocks.HORN_CORAL_FAN);
        YELLOW.add(BlockType.DEAD_CORAL, Blocks.DEAD_HORN_CORAL_BLOCK);
        YELLOW.add(BlockType.DEAD_CORAL, Blocks.DEAD_HORN_CORAL);
        YELLOW.add(BlockType.DEAD_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN);

        BLUE.add(BlockType.CORAL_BLOCK, Blocks.TUBE_CORAL_BLOCK);
        BLUE.add(BlockType.CORAL, Blocks.TUBE_CORAL);
        BLUE.add(BlockType.CORAL_FAN, Blocks.TUBE_CORAL_FAN);
        BLUE.add(BlockType.DEAD_CORAL, Blocks.DEAD_TUBE_CORAL_BLOCK);
        BLUE.add(BlockType.DEAD_CORAL, Blocks.DEAD_TUBE_CORAL);
        BLUE.add(BlockType.DEAD_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN);

        MAGENTA.add(BlockType.CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK);
        MAGENTA.add(BlockType.CORAL, Blocks.BUBBLE_CORAL);
        MAGENTA.add(BlockType.CORAL_FAN, Blocks.BUBBLE_CORAL_FAN);
        MAGENTA.add(BlockType.DEAD_CORAL, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
        MAGENTA.add(BlockType.DEAD_CORAL, Blocks.DEAD_BUBBLE_CORAL);
        MAGENTA.add(BlockType.DEAD_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN);

        PINK.add(BlockType.CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK);
        PINK.add(BlockType.CORAL, Blocks.BRAIN_CORAL);
        PINK.add(BlockType.CORAL_FAN, Blocks.BRAIN_CORAL_FAN);
        PINK.add(BlockType.DEAD_CORAL, Blocks.DEAD_BRAIN_CORAL_BLOCK);
        PINK.add(BlockType.DEAD_CORAL, Blocks.DEAD_BRAIN_CORAL);
        PINK.add(BlockType.DEAD_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN);
    }

    @Override
    public String getTranslationKey() {
        return Spaetial.translationKey("block_material", name);
    }
}

package spaetial.editing.blocks;

import net.minecraft.block.Block;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;

public enum BlockColor {
    UNCOLORED(-1, -1, "uncolored", 0xf9ffff),
    WHITE(0, 0, "white", 0xf9ffff),
    LIGHT_GRAY(1, 8, "light_gray", 0x9c9d97),
    GRAY(2, 7, "gray", 0x474f52),
    BLACK(3, 15, "black", 0x1d1c21),
    BROWN(4, 12, "brown", 0x825432),
    RED(5, 14, "red", 0xb02e26),
    ORANGE(6, 1, "orange", 0xf9801d),
    YELLOW(7, 4, "yellow", 0xffd83d),
    LIME(8, 5, "lime", 0x80c71f),
    GREEN(9, 13, "green", 0x5d7c15),
    CYAN(10, 9, "cyan", 0x169c9d),
    LIGHT_BLUE(11, 3, "light_blue", 0x3ab3da),
    BLUE(12, 11, "blue", 0x3c44a9),
    PURPLE(13, 10, "purple", 0x8932b7),
    MAGENTA(14, 2, "magenta", 0xc64fbd),
    PINK(15, 6, "pink", 0xf38caa);

    private static final HashMap<String, BlockColor> NAME_LOOKUP = new HashMap<>();
    private static final HashMap<Integer, BlockColor> ID_LOOKUP = new HashMap<>();
    private static final HashMap<Integer, BlockColor> LEGACY_ID_LOOKUP = new HashMap<>();

    static {
        for (var e : BlockColor.values()) {
            NAME_LOOKUP.put(e.name, e);
            ID_LOOKUP.put(e.id, e);
            LEGACY_ID_LOOKUP.put(e.legacyId, e);
        }
    }

    public static BlockColor fromId(int id) {
        return ID_LOOKUP.get(id);
    }
    public static BlockColor fromLegacyId(int legacyId) {
        return LEGACY_ID_LOOKUP.get(legacyId);
    }
    public static BlockColor fromName(String name) {
        return NAME_LOOKUP.get(name);
    };


    public final int id;
    public final int legacyId;
    public final String name;
    public final Vector3f colorVector3f;
    public final int colorValue;
    BlockColor(int id, int legacyId, String name, int color) {
        this.id = id;
        this.legacyId = legacyId;
        this.name = name;
        this.colorValue = color;
        this.colorVector3f = new Vector3f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f);
    }

    public static BlockColor of(Block block) {
        List<BlockMaterial> groups = BlockMaterial.getAllGroups(block);
        for (BlockMaterial group : groups) {
            if (group.isColorGroup()) return switch (group) {
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
                default -> null;
            };
        }
        return null;
    }
}

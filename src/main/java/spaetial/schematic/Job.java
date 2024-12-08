package spaetial.schematic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class Job {
    private final Type type;
    private final BlockPos pos;
    private final BlockState before;
    private final BlockState after;
    private final ItemUsage itemUsage;

    public Job(Type type, BlockPos pos, BlockState before, BlockState after, ItemUsage itemUsage) {
        this.type = type;
        this.pos = pos;
        this.before = before;
        this.after = after;
        this.itemUsage = itemUsage;
    }

    public enum Type {
        BREAK_BLOCK,
        PLACE_BLOCK,
        MODIFY_BLOCKSTATE,
        PLACE_MULTI_BLOCK,
    }
}

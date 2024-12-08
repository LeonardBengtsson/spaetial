package spaetial.editing;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Filter {
    public static final PacketCodec<RegistryByteBuf, Filter> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.createEnum(Mode.class), filter -> filter.mode,
        PacketCodecsUtil.createSet(PacketCodecsUtil.BLOCK), filter -> filter.blockFilter,
        PacketCodecsUtil.createSet(PacketCodecsUtil.BLOCK_STATE), filter -> filter.blockStateFilter,
        Filter::new
    );

    public static final Filter ALLOW_ALL = new Filter(Mode.DENY, new HashSet<>(), new HashSet<>());
    public static final Filter DENY_ALL = new Filter(Mode.ALLOW, new HashSet<>(), new HashSet<>());

    private static final HashSet<Block> AIR_BLOCKS = new HashSet<>();
    static {
        AIR_BLOCKS.add(Blocks.AIR);
        AIR_BLOCKS.add(Blocks.VOID_AIR);
        AIR_BLOCKS.add(Blocks.CAVE_AIR);
        AIR_BLOCKS.add(Blocks.STRUCTURE_VOID); // TODO reconsider?
    }
    public static final Filter DENY_AIR = new Filter(Mode.DENY, AIR_BLOCKS, new HashSet<>());

    private final Mode mode;
    private final HashSet<Block> blockFilter;
    private final HashSet<BlockState> blockStateFilter;

    public boolean allowsAll() {
        return mode == Mode.DENY && blockFilter.isEmpty() && blockStateFilter.isEmpty();
    }
    public boolean deniesAll() {
        return mode == Mode.ALLOW && blockFilter.isEmpty() && blockStateFilter.isEmpty();
    }

    public boolean doesAllow(BlockState state) {
        if (mode == Mode.ALLOW) {
            return blockFilter.contains(state.getBlock()) || blockStateFilter.contains(state);
        } else {
            return !blockFilter.contains(state.getBlock()) && !blockStateFilter.contains(state);
        }
    }

    public Filter(Mode mode, HashSet<Block> blockFilter, HashSet<BlockState> blockStateFilter) {
        this.mode = mode;
        this.blockFilter = blockFilter;
        this.blockStateFilter = blockStateFilter;
    }

    /**
     * @return A filter that allows all blocks that are allowed by all the given filters, and denies all other blocks
     */
    public static Filter intersection(Filter... filters) {
        // TODO
        return null;
//        var allowedBlocks = new HashSet<Block>();
//        var allowedBlockStates = new HashSet<BlockState>();
//        var deniedBlocks = new HashSet<Block>();
//        var deniedBlockStates = new HashSet<BlockState>();
//        for (var f : filters) {
//            if (f.mode == Mode.ALLOW) {
//                allowedBlocks.addAll(f.blockFilter);
//                allowedBlockStates.addAll(f.blockStateFilter);
//            } else {
//                deniedBlocks.addAll(f.blockFilter);
//                deniedBlockStates.addAll(f.blockStateFilter);
//            }
//        }
//        if (allowedBlocks.size() == 0 && allowedBlockStates.size() == 0) {
//            return new Filter(Mode.DENY, deniedBlocks, deniedBlockStates);
//        }
//        allowedBlockStates.removeIf(state -> allowedBlocks.contains(state.getBlock()));
//        deniedBlockStates.removeIf(state -> deniedBlocks.contains(state.getBlock()));
//
    }

    /**
     * @return A filter that allows all blocks that are allowed by any of the given filters, and denies all other blocks
     */
    public static Filter union(Filter... filters) {
        // TODO
        return null;
    }

    public enum Mode {
        ALLOW, DENY
    }
}

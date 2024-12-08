package spaetial.editing.region;

import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import spaetial.editing.Filter;
import spaetial.networking.PacketCodecsUtil;
import spaetial.util.BoxUtil;

import java.util.BitSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MaskedBlockBox {
    private final BlockBox box;
    private final BitSet mask;

    public static final PacketCodec<PacketByteBuf, MaskedBlockBox> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.BLOCK_BOX, MaskedBlockBox::getBox,
        PacketCodecsUtil.BIT_SET, MaskedBlockBox::getMask,
        MaskedBlockBox::new
    );

    private MaskedBlockBox(BlockBox box, BitSet mask) {
        this.box = box;
        this.mask = mask;
    }

    public BlockBox getBox() { return box; }
    public BitSet getMask() { return mask; }

    public static int index(int x, int y, int z, int sx, int sy, int sz) {
        return
            y * sx * sz +
            x * sz +
            z;
    }

    private int index(int x, int y, int z) {
        return index(x, y, z, box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
    }

    public static MaskedBlockBox create(BlockBox box, BitSet mask) {
        assert mask.length() == box.getBlockCountX() * box.getBlockCountY() * box.getBlockCountZ();
        return new MaskedBlockBox(box, mask);
    }

    public static MaskedBlockBox create(BlockBox box, ServerWorld world, Filter filter) {
        if (filter.allowsAll()) {
            return createFilled(box);
        }

        BitSet bitSet = new BitSet(box.getBlockCountX() * box.getBlockCountY() * box.getBlockCountZ());

        int index = 0;
        for (int j = 0; j < box.getBlockCountY(); j++) {
            for (int i = 0; i < box.getBlockCountX(); i++) {
                for (int k = 0; k < box.getBlockCountZ(); k++) {
                    if (filter.doesAllow(world.getBlockState(new BlockPos(i + box.getMinX(), j + box.getMinY(), k + box.getMinZ())))) {
                        bitSet.set(index);
                    }
                    index++;
                }
            }
        }
        return new MaskedBlockBox(box, bitSet);
    }

    public MaskedBlockBox offset(Vec3i offset) {
        return new MaskedBlockBox(this.box.offset(offset.getX(), offset.getY(), offset.getZ()), this.mask);
    }

    public void removeColumn(Direction.Axis axis, int x, int y, int z) {
        switch (axis) {
            case X -> {
                for (int i = 0; i < box.getBlockCountX(); i++) {
                    removeBlock(i, y, z);
                }
            }
            case Y -> {
                for (int i = 0; i < box.getBlockCountY(); i++) {
                    removeBlock(x, i, y);
                }
            }
            case Z -> {
                int start = y * box.getBlockCountX() * box.getBlockCountZ() + x * box.getBlockCountZ();
                this.mask.clear(start, start + box.getBlockCountZ());
            }
        }
    }

    public void removeBlock(int x, int y, int z) {
        this.mask.clear(index(x, y, z));
    }

    public static MaskedBlockBox createFilled(BlockBox box) {
        var volume = box.getBlockCountX() * box.getBlockCountY() * box.getBlockCountZ();
        var mask = new BitSet(volume);
        mask.flip(0, volume);
        return new MaskedBlockBox(box, mask);
    }

    public static MaskedBlockBox createFromFilter(ServerWorld world, BlockBox box, Filter filter) {
        int sx = box.getBlockCountX(), sy = box.getBlockCountY(), sz = box.getBlockCountZ();
        var min = BoxUtil.minPos(box);
        var volume = sx * sy * sz;
        var mask = new BitSet(volume);
        int index = 0;
        for (int i = 0; i < sx; i++) {
            for (int j = 0; j < sy; j++) {
                for (int k = 0; k < sz; k++) {
                    var pos = min.add(i, j, k);
                    if (filter.doesAllow(world.getBlockState(pos))) {
                        mask.set(index);
                    }
                    index++;
                }
            }
        }
        return new MaskedBlockBox(box, mask);
    }

    public static MaskedBlockBox and(MaskedBlockBox a, MaskedBlockBox b) {
        // TODO add support for not fully overlapping boxes
        assert
            a.box.getMinX() == b.box.getMinX() &&
            a.box.getMinY() == b.box.getMinY() &&
            a.box.getMinZ() == b.box.getMinZ() &&
            a.box.getMaxX() == b.box.getMaxX() &&
            a.box.getMaxY() == b.box.getMaxY() &&
            a.box.getMaxZ() == b.box.getMaxZ();

        BitSet mask = (BitSet) a.mask.clone();
        mask.and(b.mask);
        return new MaskedBlockBox(a.box, mask);
    }

    public void iterate(Consumer<BlockPos> consumer) {
        int index = 0;
        for (int j = 0; j < box.getBlockCountY(); j++) {
            for (int i = 0; i < box.getBlockCountX(); i++) {
                for (int k = 0; k < box.getBlockCountZ(); k++) {
                    if (mask.get(index)) {
                        consumer.accept(new BlockPos(i + box.getMinX(), j + box.getMinY(), k + box.getMinZ()));
                    }
                    index++;
                }
            }
        }
    }

    public void iterate(ServerWorld world, BiConsumer<BlockPos, BlockState> consumer) {
        int index = 0;
        for (int j = 0; j < box.getBlockCountY(); j++) {
            for (int i = 0; i < box.getBlockCountX(); i++) {
                for (int k = 0; k < box.getBlockCountZ(); k++) {
                    if (mask.get(index)) {
                        BlockPos pos = new BlockPos(i + box.getMinX(), j + box.getMinY(), k + box.getMinZ());
                        consumer.accept(pos, world.getBlockState(pos));
                    }
                    index++;
                }
            }
        }
    }

    public BlockBox getOuterBounds() {
        // TODO might wanna change this depending on mask
        return box;
    }

    public int getVolume() {
        return mask.cardinality();
    }
}

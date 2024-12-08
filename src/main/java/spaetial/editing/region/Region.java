package spaetial.editing.region;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldAccess;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.Filter;
import spaetial.editing.operation.OperationUtil;
import spaetial.editing.selection.Selection;
import spaetial.editing.blocks.RightAngleOrientation;
import spaetial.util.encoding.*;

import java.util.Arrays;
import java.util.BitSet;

public class Region {
    public final int sx, sy, sz, volume;
    private final int[] blocks;
    private final NbtCompound[] blockEntities;

    private Region(int sx, int sy, int sz, int[] blocks, NbtCompound[] blockEntities) {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        this.volume = sx * sy * sz;
        this.blocks = blocks;
        this.blockEntities = blockEntities;
    }

    public static Region create(WorldAccess sourceWorld, Selection selection) {
        BlockBox bounds = selection.getOuterBounds();

        Vec3i dims = bounds.getDimensions().add(1, 1, 1);
        int volume = dims.getX() * dims.getY() * dims.getZ();
        int[] blocks = new int[volume];
        NbtCompound[] nbts = new NbtCompound[volume];

        int index = 0;
        for (int j = 0; j < dims.getY(); j++) {
            for (int i = 0; i < dims.getX(); i++) {
                for (int k = 0; k < dims.getZ(); k++) {
                    BlockPos pos = new BlockPos(i + bounds.getMinX(), j + bounds.getMinY(), k + bounds.getMinZ());

                    if (selection.isWithin(pos)) {
                        BlockState block = sourceWorld.getBlockState(pos);
                        blocks[index] = idFromBlock(block);

                        BlockEntity blockEntity = sourceWorld.getChunk(pos).getBlockEntity(pos);
                        if (blockEntity != null) {
                            var nbt = blockEntity.createNbt(sourceWorld.getRegistryManager());
                            Identifier blockEntityId = BlockEntityType.getId(blockEntity.getType());
                            // commented out because this optimization causes issues with rendering in spaetial.render.BlockUtilRender::renderRegion
                            // Identifier blockId = Registries.BLOCK.getId(block.getBlock());
                            if (blockEntityId != null /*&& !blockEntityId.equals(blockId)*/) {
                                nbt.putString("id", blockEntityId.toString());
                            }
                            nbts[index] = nbt;
                        }
                    } else {
                        blocks[index] = -1;
                    }

                    index++;
                }
            }
        }
        return new Region(dims.getX(), dims.getY(), dims.getZ(), blocks, nbts);
    }

    public static Region create(WorldAccess sourceWorld, Selection selection, Filter filter) {
        BlockBox bounds = selection.getOuterBounds();

        Vec3i dims = bounds.getDimensions().add(1, 1, 1);
        int volume = dims.getX() * dims.getY() * dims.getZ();
        int[] blocks = new int[volume];
        NbtCompound[] nbts = new NbtCompound[volume];

        int index = 0;
        for (int j = 0; j < dims.getY(); j++) {
            for (int i = 0; i < dims.getX(); i++) {
                for (int k = 0; k < dims.getZ(); k++) {
                    BlockPos pos = new BlockPos(i + bounds.getMinX(), j + bounds.getMinY(), k + bounds.getMinZ());

                    if (selection.isWithin(pos)) {
                        BlockState block = sourceWorld.getBlockState(pos);
                        if (filter.doesAllow(block)) {
                            blocks[index] = idFromBlock(block);

                            BlockEntity blockEntity = sourceWorld.getChunk(pos).getBlockEntity(pos);
                            if (blockEntity != null) {
                                var nbt = blockEntity.createNbt(sourceWorld.getRegistryManager());
                                Identifier blockEntityId = BlockEntityType.getId(blockEntity.getType());
                                // commented out because this optimization causes issues with rendering in spaetial.render.BlockUtilRender::renderRegion
                                // Identifier blockId = Registries.BLOCK.getId(block.getBlock());
                                if (blockEntityId != null /*&& !blockEntityId.equals(blockId)*/) {
                                    nbt.putString("id", blockEntityId.toString());
                                }
                                nbts[index] = nbt;
                            }
                        } else {
                            blocks[index] = -1;
                        }
                    } else {
                        blocks[index] = -1;
                    }

                    index++;
                }
            }
        }
        return new Region(dims.getX(), dims.getY(), dims.getZ(), blocks, nbts);
    }

    public void place(ServerWorld world, BlockPos minPos, boolean suppressUpdates) {
        iterate(
            minPos,
            (pos, block, nbt) -> {
                OperationUtil.setBlockState(world, pos, block, suppressUpdates);
                if (nbt != null) {
                    nbt = nbt.copy();

                    world.removeBlockEntity(pos);

                    nbt.putInt("x", pos.getX());
                    nbt.putInt("y", pos.getY());
                    nbt.putInt("z", pos.getZ());

                    BlockEntity blockEntity = BlockEntity.createFromNbt(pos, block, nbt, world.getRegistryManager());

                    assert blockEntity != null;
                    world.addBlockEntity(blockEntity);
                }
            }
        );
    }

    public void place(ServerWorld world, BlockPos minPos, boolean suppressUpdates, Filter filter) {
        iterate(
            minPos,
            (pos, block, nbt) -> {
                if (filter.doesAllow(world.getBlockState(pos))) {
                    OperationUtil.setBlockState(world, pos, block, suppressUpdates);
                    if (nbt != null) {
                        nbt = nbt.copy();

                        world.removeBlockEntity(pos);

                        nbt.putInt("x", pos.getX());
                        nbt.putInt("y", pos.getY());
                        nbt.putInt("z", pos.getZ());

                        BlockEntity blockEntity = BlockEntity.createFromNbt(pos, block, nbt, world.getRegistryManager());

                        assert blockEntity != null;
                        world.addBlockEntity(blockEntity);
                    }
                }
            }
        );
    }

    private int getIndex(int x, int y, int z) {
        return y * this.sx * this.sz + x * this.sz + z;
    }

    public static int idFromBlock(BlockState block) {
        return Block.getRawIdFromState(block);
    }

    public static BlockState blockFromId(int id) {
        return Block.getStateFromRawId(id);
    }

    public BlockState getBlock(int x, int y, int z) {
        int blockId = this.blocks[getIndex(x, y, z)];
        return blockId < 0 ? null : blockFromId(blockId);
    }

    @Nullable
    public NbtCompound getBlockEntity(int x, int y, int z) {
        return blockEntities[getIndex(x, y, z)];
    }

    public boolean hasSize(Vec3i size) {
        return sx == size.getX() && sy == size.getY() && sz == size.getZ();
    }

    public Vec3i dimensions() { return new Vec3i(sx, sy, sz); }

    public BitSet createMask() {
        var bitSet = new BitSet(volume);
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] >= 0) bitSet.set(i);
        }
        return bitSet;
    }

    public Region cloneWithFilter(Filter filter) {
        var newBlocks = new int[volume];
        var newBlockEntities = new NbtCompound[volume];
        for (int i = 0; i < volume; i++) {
            int block = blocks[i];
            if (filter.doesAllow(blockFromId(block))) {
                newBlocks[i] = block;
                newBlockEntities[i] = blockEntities[i];
            } else {
                newBlocks[i] = -1;
            }
        }
        return new Region(sx, sy, sz, newBlocks, newBlockEntities);
    }

    public Region cloneWithNewRightAngleOrientation(RightAngleOrientation orientation) {
        // TODO
        return this;
    }

    public void iterate(BlockPos offset, TriConsumer<BlockPos, BlockState, @Nullable NbtCompound> consumer) {
        for (int j = 0; j < sy; j++) {
            for (int i = 0; i < sx; i++) {
                for (int k = 0; k < sz; k++) {
                    var block = this.getBlock(i, j, k);
                    if (block != null) {
                        var pos = offset.add(i, j, k);
                        var nbt = this.getBlockEntity(i, j, k);
                        consumer.accept(pos, block, nbt);
                    }
                }
            }
        }
    }

    public byte[] encode() {
        // TODO develop a better encoding algorithm
        // https://stackoverflow.com/questions/1086054/how-to-convert-int-to-byte

        var writer = new ByteArrayWriter();
        writer.writeInt(sx);
        writer.writeInt(sy);
        writer.writeInt(sz);
        for (int block : blocks) {
            writer.writeInt(block);
        }

        int index = 0;
        for (var nbt : blockEntities) {
            if (nbt != null) {
                BitInfo bi = ByteCompression.compressString(nbt.toString(), ByteCompression.CompressionFormat.NBT_OPTIMIZED);
                byte[] bytes = bi.getArray();
                int size = bytes.length;
                byte mod = (byte) (bi.length() % 8);

                writer.writeInt(index);
                writer.writeInt(size);
                writer.write(mod);
                writer.write(bytes);

                index = 1;
            } else {
                index++;
            }
        }

        return writer.get();
    }

    public static Region decode(byte[] data) throws ByteDecoderException {
        return decode(new ByteArrayReader(data));
    }

    public static Region decode(ByteArrayReader reader) throws ByteDecoderException {
        if (!reader.canRead(12)) throw new ByteDecoderException("Invalid region byte data: length < 12");

        int sx = reader.readInt();
        int sy = reader.readInt();
        int sz = reader.readInt();
        int volume = sx * sy * sz;

        if (!reader.canRead(4 * volume)) throw new ByteDecoderException("Invalid region byte data: length < 12 + volume");
        int[] blocks = new int[volume];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = reader.readInt();
        }

        var blockEntities = new NbtCompound[volume];
        int index = 0;
        while (reader.canRead(9)) {
            index += reader.readInt();
            int size = reader.readInt();
            byte mod = reader.read();
            if (!reader.canRead(size)) throw new ByteDecoderException("Invalid region byte data: nbt data size doesn't match array length");
            byte[] bytes = reader.readArray(size);

            String nbtString = ByteCompression.decompressString(BitInfo.fromBytes(bytes, mod), ByteCompression.CompressionFormat.NBT_OPTIMIZED);

            NbtCompound nbt;
            try {
                nbt = StringNbtReader.parse(nbtString);
            } catch (CommandSyntaxException e) {
                throw new ByteDecoderException("Invalid nbt: " + nbtString);
            }
            if (nbt == null) throw new ByteDecoderException("Invalid nbt: " + nbtString);
            blockEntities[index] = nbt;
        }

        return new Region(sx, sy, sz, blocks, blockEntities);
    }

    public String debug() {
        return Arrays.toString(blocks);
    }
}

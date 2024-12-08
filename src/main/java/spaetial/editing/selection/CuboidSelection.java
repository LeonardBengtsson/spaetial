package spaetial.editing.selection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.networking.PacketCodecsUtil;
import spaetial.util.BoxUtil;

public class CuboidSelection extends Selection {
    private BlockBox blockBox;

    public static final PacketCodec<PacketByteBuf, CuboidSelection> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.BLOCK_BOX, selection -> selection.blockBox,
        CuboidSelection::new
    );

    public CuboidSelection(BlockBox blockBox) {
        this.blockBox = blockBox;
    }

    public BlockBox getBlockBox() { return blockBox; }

    @Override
    public void move(Vec3i offset) {
        blockBox = blockBox.offset(offset.getX(), offset.getY(), offset.getZ());
    }

    @Override
    public BlockBox getOuterBounds() {
        return blockBox;
    }

    @Override
    public MaskedBlockBox getMaskedBlockBox() {
        return MaskedBlockBox.createFilled(blockBox);
    }

    @Override
    public boolean isWithin(BlockPos pos) {
        // TODO double check
        return blockBox.contains(pos);
    }

    @Override
    public Selection copyAndOffset(Vec3i offset) {
        return new CuboidSelection(BoxUtil.offset(blockBox, offset));
    }

    @Override
    public int getVolume() {
        return blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
    }

    @Override
    public SelectionType getType() { return SelectionType.CUBOID; }
}

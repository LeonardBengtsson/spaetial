package spaetial.editing.selection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.util.BoxUtil;

public class MaskedCuboidSelection extends Selection {
    private MaskedBlockBox maskedBlockBox;

    public static final PacketCodec<PacketByteBuf, MaskedCuboidSelection> PACKET_CODEC = PacketCodec.tuple(
        MaskedBlockBox.PACKET_CODEC, selection -> selection.maskedBlockBox,
        MaskedCuboidSelection::new
    );

    public MaskedCuboidSelection(CuboidSelection selection) {
        this.maskedBlockBox = MaskedBlockBox.createFilled(selection.getBlockBox());
    }
    public MaskedCuboidSelection(MaskedBlockBox maskedBlockBox) {
        this.maskedBlockBox = maskedBlockBox;
    }

    @Override
    public void move(Vec3i offset) {
        maskedBlockBox = maskedBlockBox.offset(offset);
    }

    @Override
    public BlockBox getOuterBounds() {
        return maskedBlockBox.getOuterBounds();
    }

    @Override
    public MaskedBlockBox getMaskedBlockBox() {
        return maskedBlockBox;
    }

    @Override
    public boolean isWithin(BlockPos pos) {
        var delta = pos.subtract(BoxUtil.minPos(maskedBlockBox.getBox()));
        int dx = delta.getX();
        int dy = delta.getY();
        int dz = delta.getZ();
        if (dx < 0 || dy < 0 || dz < 0) return false;
        int sx = maskedBlockBox.getBox().getBlockCountX();
        int sy = maskedBlockBox.getBox().getBlockCountY();
        int sz = maskedBlockBox.getBox().getBlockCountZ();
        if (dx >= sx || dy >= sy || dz >= sz) return false;
        int index = MaskedBlockBox.index(dx, dy, dz, sx, sy, sz);
        if (index > sx * sy * sz) return false;
        return maskedBlockBox.getMask().get(index);
    }

    @Override
    public Selection copyAndOffset(Vec3i offset) {
        return new MaskedCuboidSelection(maskedBlockBox.offset(offset));
    }

    @Override
    public int getVolume() {
        return maskedBlockBox.getVolume();
    }

    @Override
    public SelectionType getType() { return SelectionType.MASKED_CUBOID; }
}

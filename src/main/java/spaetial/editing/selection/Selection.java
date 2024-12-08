package spaetial.editing.selection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.networking.VariantPacketCodec;
import spaetial.util.BoxUtil;
import spaetial.util.functional.TypeSupplier;

public abstract class Selection implements TypeSupplier<SelectionType> {
    public abstract void move(Vec3i offset);

    public abstract BlockBox getOuterBounds();
    public abstract MaskedBlockBox getMaskedBlockBox();
    public abstract boolean isWithin(BlockPos pos);

    /**
     * This should not be an expensive operation
     */
    public abstract Selection copyAndOffset(Vec3i offset);
    public void moveToMinPos(BlockPos min) {
        move(min.subtract(BoxUtil.minPos(getOuterBounds())));
    }
    public void moveToBottomCenter(BlockPos bottomCenter) {
        var outerBounds = getOuterBounds();
        move(bottomCenter.subtract(outerBounds.getCenter().withY(outerBounds.getMinY())));
    }
    public abstract int getVolume();

    public static final PacketCodec<PacketByteBuf, Selection> PACKET_CODEC = new VariantPacketCodec<>(SelectionType.class);
}

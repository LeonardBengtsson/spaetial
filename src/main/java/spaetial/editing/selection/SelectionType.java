package spaetial.editing.selection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecSupplier;

public enum SelectionType implements PacketCodecSupplier<PacketByteBuf, Selection> {
    CUBOID, MASKED_CUBOID;

    @Override
    public PacketCodec<PacketByteBuf, ? extends Selection> getCodec() {
        return switch (this) {
            case CUBOID -> CuboidSelection.PACKET_CODEC;
            case MASKED_CUBOID -> MaskedCuboidSelection.PACKET_CODEC;
        };
    }
}

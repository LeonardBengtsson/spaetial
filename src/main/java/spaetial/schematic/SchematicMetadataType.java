package spaetial.schematic;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecSupplier;
import spaetial.networking.PacketCodecsUtil;

public enum SchematicMetadataType implements PacketCodecSupplier<PacketByteBuf, SchematicMetadata> {
    SCHEMATIC, CLIPBOARD, REGION;

    @Override
    public PacketCodec<PacketByteBuf, SchematicMetadata> getCodec() {
        return switch (this) {
            case SCHEMATIC -> SchematicMetadata.FULL_SCHEMATIC_INFO_PACKET_CODEC;
            case CLIPBOARD -> SchematicMetadata.CLIPBOARD_INFO_PACKET_CODEC;
            case REGION -> SchematicMetadata.REGION_INFO_PACKET_CODEC;
        };
    }
}

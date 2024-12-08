package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record SchematicUploadPartC2SPacket(UUID uploadId, int totalPacketCount, int packetIndex, byte[] data) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<SchematicUploadPartC2SPacket> ID = new Id<>(Spaetial.id("schematic_upload_part"));
    public static final PacketCodec<RegistryByteBuf, SchematicUploadPartC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SchematicUploadPartC2SPacket::uploadId,
        PacketCodecs.INTEGER, SchematicUploadPartC2SPacket::totalPacketCount,
        PacketCodecs.INTEGER, SchematicUploadPartC2SPacket::packetIndex,
        PacketCodecs.BYTE_ARRAY, SchematicUploadPartC2SPacket::data,
        SchematicUploadPartC2SPacket::new
    );
}

package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record RegionPartS2CPacket(UUID requestId, int totalPacketCount, int packetIndex, byte[] data) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RegionPartS2CPacket> ID = new Id<>(Spaetial.id("region_part"));
    public static final PacketCodec<RegistryByteBuf, RegionPartS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RegionPartS2CPacket::requestId,
        PacketCodecs.INTEGER, RegionPartS2CPacket::totalPacketCount,
        PacketCodecs.INTEGER, RegionPartS2CPacket::packetIndex,
        PacketCodecs.BYTE_ARRAY, RegionPartS2CPacket::data,
        RegionPartS2CPacket::new
    );
}

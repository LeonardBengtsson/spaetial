package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3i;
import spaetial.Spaetial;
import spaetial.networking.PacketCodecsUtil;

import java.util.UUID;

public record RegionDimensionsS2CPacket(UUID requestId, Vec3i dimensions) implements CustomPayload {
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final CustomPayload.Id<RegionDimensionsS2CPacket> ID = new CustomPayload.Id<>(Spaetial.id("region_dimensions"));
    public static final PacketCodec<RegistryByteBuf, RegionDimensionsS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RegionDimensionsS2CPacket::requestId,
        PacketCodecsUtil.VEC3, RegionDimensionsS2CPacket::dimensions,
        RegionDimensionsS2CPacket::new
    );
}

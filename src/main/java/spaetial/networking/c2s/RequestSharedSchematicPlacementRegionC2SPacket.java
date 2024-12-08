package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record RequestSharedSchematicPlacementRegionC2SPacket(UUID requestId, UUID placementId) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestSharedSchematicPlacementRegionC2SPacket> ID = new Id<>(Spaetial.id("request_shared_schematic_placement_region"));
    public static final PacketCodec<RegistryByteBuf, RequestSharedSchematicPlacementRegionC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RequestSharedSchematicPlacementRegionC2SPacket::requestId,
        Uuids.PACKET_CODEC, RequestSharedSchematicPlacementRegionC2SPacket::placementId,
        RequestSharedSchematicPlacementRegionC2SPacket::new
    );
}

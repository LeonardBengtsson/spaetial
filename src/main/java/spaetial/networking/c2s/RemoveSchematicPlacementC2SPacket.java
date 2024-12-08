package spaetial.networking.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record RemoveSchematicPlacementC2SPacket(UUID placementId) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final CustomPayload.Id<RemoveSchematicPlacementC2SPacket> ID = new Id<>(Spaetial.id("remove_schematic_placement"));
    public static final PacketCodec<PacketByteBuf, RemoveSchematicPlacementC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RemoveSchematicPlacementC2SPacket::placementId,
        RemoveSchematicPlacementC2SPacket::new
    );
}

package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record ToggleSharingSchematicPlacementC2SPacket(UUID placementId, boolean toggle) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<ToggleSharingSchematicPlacementC2SPacket> ID = new Id<>(Spaetial.id("toggle_sharing_schematic_placement"));
    public static final PacketCodec<RegistryByteBuf, ToggleSharingSchematicPlacementC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, ToggleSharingSchematicPlacementC2SPacket::placementId,
        PacketCodecs.BOOLEAN, ToggleSharingSchematicPlacementC2SPacket::toggle,
        ToggleSharingSchematicPlacementC2SPacket::new
    );
}

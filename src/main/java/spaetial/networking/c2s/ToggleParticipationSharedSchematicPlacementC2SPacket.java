package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record ToggleParticipationSharedSchematicPlacementC2SPacket(UUID placementId, boolean toggle) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<ToggleParticipationSharedSchematicPlacementC2SPacket> ID = new Id<>(Spaetial.id("toggle_participation_shared_schematic_placement"));
    public static final PacketCodec<RegistryByteBuf, ToggleParticipationSharedSchematicPlacementC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, ToggleParticipationSharedSchematicPlacementC2SPacket::placementId,
        PacketCodecs.BOOLEAN, ToggleParticipationSharedSchematicPlacementC2SPacket::toggle,
        ToggleParticipationSharedSchematicPlacementC2SPacket::new
    );
}

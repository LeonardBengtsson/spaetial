package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;
import spaetial.schematic.SharedSchematicPlacementInfo;

import java.util.UUID;

public record SharedSchematicPlacementAddedS2CPacket(UUID placementId, SharedSchematicPlacementInfo info) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<SharedSchematicPlacementAddedS2CPacket> ID = new Id<>(Spaetial.id("shared_schematic_placement_added"));
    public static final PacketCodec<RegistryByteBuf, SharedSchematicPlacementAddedS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SharedSchematicPlacementAddedS2CPacket::placementId,
        SharedSchematicPlacementInfo.PACKET_CODEC, SharedSchematicPlacementAddedS2CPacket::info,
        SharedSchematicPlacementAddedS2CPacket::new
    );
}

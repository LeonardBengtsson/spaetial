package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record SharedSchematicPlacementRemovedS2CPacket(UUID placementId) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<SharedSchematicPlacementRemovedS2CPacket> ID = new Id<>(Spaetial.id("shared_schematic_placement_removed"));
    public static final PacketCodec<RegistryByteBuf, SharedSchematicPlacementRemovedS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SharedSchematicPlacementRemovedS2CPacket::placementId,
        SharedSchematicPlacementRemovedS2CPacket::new
    );
}

package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.Spaetial;

import java.util.UUID;

public record MoveSchematicPlacementC2SPacket(UUID placementId, BlockPos minPos, RegistryKey<World> dim) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<MoveSchematicPlacementC2SPacket> ID = new Id<>(Spaetial.id("move_schematic_placement"));
    public static final PacketCodec<RegistryByteBuf, MoveSchematicPlacementC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, MoveSchematicPlacementC2SPacket::placementId,
        BlockPos.PACKET_CODEC, MoveSchematicPlacementC2SPacket::minPos,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), MoveSchematicPlacementC2SPacket::dim,
        MoveSchematicPlacementC2SPacket::new
    );
}

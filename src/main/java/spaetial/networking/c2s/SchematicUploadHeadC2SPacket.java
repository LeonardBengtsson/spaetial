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
import spaetial.schematic.SchematicMetadata;

import java.util.UUID;

public record SchematicUploadHeadC2SPacket(UUID uploadId, UUID schematicId, BlockPos minPos, RegistryKey<World> dim, SchematicMetadata meta) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<SchematicUploadHeadC2SPacket> ID = new Id<>(Spaetial.id("schematic_upload_head"));
    public static final PacketCodec<RegistryByteBuf, SchematicUploadHeadC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SchematicUploadHeadC2SPacket::uploadId,
        Uuids.PACKET_CODEC, SchematicUploadHeadC2SPacket::schematicId,
        BlockPos.PACKET_CODEC, SchematicUploadHeadC2SPacket::minPos,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), SchematicUploadHeadC2SPacket::dim,
        SchematicMetadata.PACKET_CODEC, SchematicUploadHeadC2SPacket::meta,
        SchematicUploadHeadC2SPacket::new
    );
}

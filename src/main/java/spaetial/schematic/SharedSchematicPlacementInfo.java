package spaetial.schematic;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;
import spaetial.networking.PacketCodecsUtil;

import java.util.HashSet;
import java.util.UUID;

public record SharedSchematicPlacementInfo(
    UUID owner,
    HashSet<UUID> participants,
    BlockBox box,
    RegistryKey<World> dim,
    SchematicMetadata metadata
) {
    public static final PacketCodec<RegistryByteBuf, SharedSchematicPlacementInfo> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SharedSchematicPlacementInfo::owner,
        PacketCodecsUtil.createSet(PacketCodecsUtil.createBufMap(Uuids.PACKET_CODEC)), SharedSchematicPlacementInfo::participants,
        PacketCodecsUtil.BLOCK_BOX, SharedSchematicPlacementInfo::box,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), SharedSchematicPlacementInfo::dim,
        SchematicMetadata.PACKET_CODEC, SharedSchematicPlacementInfo::metadata,
        SharedSchematicPlacementInfo::new
    );

    public static SharedSchematicPlacementInfo create(SharedSchematicPlacement sharedSchematicPlacement) {
        return new SharedSchematicPlacementInfo(sharedSchematicPlacement.owner, sharedSchematicPlacement.participants, sharedSchematicPlacement.schematicPlacement.box, sharedSchematicPlacement.schematicPlacement.dim, sharedSchematicPlacement.schematicPlacement.schematic.metadata());
    }
}

package spaetial.schematic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record FullSchematicInfo(UUID id, String name, UUID authorId, String authorName) {
    static final String DEFAULT_NAME = "Unnamed";

    public static final PacketCodec<PacketByteBuf, FullSchematicInfo> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, FullSchematicInfo::id,
        PacketCodecs.STRING, FullSchematicInfo::name,
        Uuids.PACKET_CODEC, FullSchematicInfo::authorId,
        PacketCodecs.STRING, FullSchematicInfo::authorName,
        FullSchematicInfo::new
    );

    public static FullSchematicInfo create(PlayerEntity author) {
        return new FullSchematicInfo(UUID.randomUUID(), DEFAULT_NAME, author.getUuid(), author.getGameProfile().getName());
    }
}

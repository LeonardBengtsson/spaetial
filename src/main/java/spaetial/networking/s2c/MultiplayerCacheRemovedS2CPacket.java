package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record MultiplayerCacheRemovedS2CPacket(UUID playerId) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<MultiplayerCacheRemovedS2CPacket> ID = new Id<>(Spaetial.id("multiplayer_cache_removed"));
    public static final PacketCodec<RegistryByteBuf, MultiplayerCacheRemovedS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, MultiplayerCacheRemovedS2CPacket::playerId,
        MultiplayerCacheRemovedS2CPacket::new
    );
}

package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;
import spaetial.editing.state.common.CommonEditingState;

import java.util.UUID;

public record MultiplayerCacheUpdatedS2CPacket(UUID playerId, CommonEditingState state) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<MultiplayerCacheUpdatedS2CPacket> ID = new Id<>(Spaetial.id("multiplayer_cache_updated"));
    public static final PacketCodec<RegistryByteBuf, MultiplayerCacheUpdatedS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, MultiplayerCacheUpdatedS2CPacket::playerId,
        CommonEditingState.PACKET_CODEC, MultiplayerCacheUpdatedS2CPacket::state,
        MultiplayerCacheUpdatedS2CPacket::new
    );
}

package spaetial.networking.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;
import spaetial.networking.PacketCodecsUtil;

public record RequestMultiplayerCacheUpdateC2SPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestMultiplayerCacheUpdateC2SPacket> ID = new Id<>(Spaetial.id("request_multiplayer_cache_update"));
    public static final PacketCodec<PacketByteBuf, RequestMultiplayerCacheUpdateC2SPacket> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new RequestMultiplayerCacheUpdateC2SPacket());
}

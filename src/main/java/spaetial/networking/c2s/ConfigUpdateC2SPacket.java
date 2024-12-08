package spaetial.networking.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;
import spaetial.networking.PacketCodecsUtil;
import spaetial.server.networking.ServerSideClientConfig;

public record ConfigUpdateC2SPacket(ServerSideClientConfig config) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final CustomPayload.Id<ConfigUpdateC2SPacket> ID = new Id<>(Spaetial.id("config_update"));
    public static final PacketCodec<PacketByteBuf, ConfigUpdateC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        ServerSideClientConfig.PACKET_CODEC, ConfigUpdateC2SPacket::config,
        ConfigUpdateC2SPacket::new
    );
}

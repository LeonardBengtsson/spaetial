package spaetial.server.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import spaetial.networking.PacketCodecsUtil;

public record ServerSideClientConfig(
    boolean participatesInMultiplayerCache,
    boolean suppressOperationUpdates,
    boolean suppressPlayerUpdates,
    boolean forcedBlockPlacement,
    boolean noClip,
    boolean farReach,
    double farReachDistance,
    double flySpeed
) {
    public static final ServerSideClientConfig DEFAULT = new ServerSideClientConfig(
        true,
        true,
        false,
        false,
        false,
        false,
        5.0,
        1.0
    );

    public static final PacketCodec<PacketByteBuf, ServerSideClientConfig> PACKET_CODEC = PacketCodecsUtil.tuple(
        PacketCodecs.BOOLEAN, ServerSideClientConfig::participatesInMultiplayerCache,
        PacketCodecs.BOOLEAN, ServerSideClientConfig::suppressOperationUpdates,
        PacketCodecs.BOOLEAN, ServerSideClientConfig::suppressPlayerUpdates,
        PacketCodecs.BOOLEAN, ServerSideClientConfig::forcedBlockPlacement,
        PacketCodecs.BOOLEAN, ServerSideClientConfig::suppressPlayerUpdates,
        PacketCodecs.BOOLEAN, ServerSideClientConfig::farReach,
        PacketCodecs.DOUBLE, ServerSideClientConfig::farReachDistance,
        PacketCodecs.DOUBLE, ServerSideClientConfig::flySpeed,
        ServerSideClientConfig::new
    );
}

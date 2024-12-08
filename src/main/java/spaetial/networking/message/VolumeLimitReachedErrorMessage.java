package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class VolumeLimitReachedErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, VolumeLimitReachedErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new VolumeLimitReachedErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.VOLUME_LIMIT_REACHED_ERROR;
    }
}

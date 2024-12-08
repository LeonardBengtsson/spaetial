package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class OutdatedRequestErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, OutdatedRequestErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new OutdatedRequestErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.OUTDATED_REQUEST_ERROR;
    }
}

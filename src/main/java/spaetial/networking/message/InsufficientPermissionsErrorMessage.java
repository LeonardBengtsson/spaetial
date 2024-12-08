package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class InsufficientPermissionsErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, InsufficientPermissionsErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new InsufficientPermissionsErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.INSUFFICIENT_PERMISSIONS_ERROR;
    }
}

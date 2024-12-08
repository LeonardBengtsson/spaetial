package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class NothingToRedoErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, NothingToRedoErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new NothingToRedoErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.NOTHING_TO_REDO_ERROR;
    }
}

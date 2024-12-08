package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class NothingToUndoErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, NothingToUndoErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new NothingToUndoErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.NOTHING_TO_UNDO_ERROR;
    }
}

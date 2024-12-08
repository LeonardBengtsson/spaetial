package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class NoClipboardErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, NoClipboardErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new NoClipboardErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.NO_CLIPBOARD_ERROR;
    }
}

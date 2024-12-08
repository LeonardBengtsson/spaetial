package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class RegionRequestTimedOutErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, RegionRequestTimedOutErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new RegionRequestTimedOutErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.REGION_REQUEST_TIMED_OUT_ERROR;
    }
}

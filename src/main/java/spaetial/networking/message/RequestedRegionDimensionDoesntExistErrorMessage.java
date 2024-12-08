package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class RequestedRegionDimensionDoesntExistErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, RequestedRegionDimensionDoesntExistErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new RequestedRegionDimensionDoesntExistErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.REQUESTED_REGION_DIMENSION_DOESNT_EXIST_ERROR;
    }
}

package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.networking.PacketCodecsUtil;

public class SchematicDoesntExistErrorMessage implements Message {
    public static final PacketCodec<PacketByteBuf, SchematicDoesntExistErrorMessage> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new SchematicDoesntExistErrorMessage());

    @Override
    public MessageType getType() {
        return MessageType.SCHEMATIC_DOESNT_EXIST_ERROR;
    }
}

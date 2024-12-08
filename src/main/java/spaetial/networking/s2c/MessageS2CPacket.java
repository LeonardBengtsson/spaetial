package spaetial.networking.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;
import spaetial.networking.message.Message;

public record MessageS2CPacket(Message message) implements CustomPayload {
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final CustomPayload.Id<MessageS2CPacket> ID = new CustomPayload.Id<>(Spaetial.id("message"));
    public static final PacketCodec<PacketByteBuf, MessageS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Message.PACKET_CODEC, MessageS2CPacket::message,
        MessageS2CPacket::new
    );
}

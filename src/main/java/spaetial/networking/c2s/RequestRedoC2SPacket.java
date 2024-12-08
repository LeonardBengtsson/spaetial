package spaetial.networking.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;
import spaetial.networking.PacketCodecsUtil;

public record RequestRedoC2SPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestRedoC2SPacket> ID = new Id<>(Spaetial.id("request_redo"));
    public static final PacketCodec<PacketByteBuf, RequestRedoC2SPacket> PACKET_CODEC = PacketCodecsUtil.createStatic(new RequestRedoC2SPacket());
}

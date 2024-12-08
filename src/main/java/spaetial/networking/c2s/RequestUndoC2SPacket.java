package spaetial.networking.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;
import spaetial.networking.PacketCodecsUtil;

public record RequestUndoC2SPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestUndoC2SPacket> ID = new Id<>(Spaetial.id("request_undo"));
    public static final PacketCodec<PacketByteBuf, RequestUndoC2SPacket> PACKET_CODEC = PacketCodecsUtil.createStatic(new RequestUndoC2SPacket());
}

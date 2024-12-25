package spaetial.networking.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;

public record ConfirmOperationActionC2SPacket(boolean skipHistory) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<ConfirmOperationActionC2SPacket> ID = new Id<>(Spaetial.id("confirm_operation_action"));
    public static final PacketCodec<PacketByteBuf, ConfirmOperationActionC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.BOOLEAN, ConfirmOperationActionC2SPacket::skipHistory,
        ConfirmOperationActionC2SPacket::new
    );
}

package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

public record RequestClipboardRegionC2SPacket(UUID requestId) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestClipboardRegionC2SPacket> ID = new Id<>(Spaetial.id("request_clipboard_region"));
    public static final PacketCodec<RegistryByteBuf, RequestClipboardRegionC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RequestClipboardRegionC2SPacket::requestId,
        RequestClipboardRegionC2SPacket::new
    );
}

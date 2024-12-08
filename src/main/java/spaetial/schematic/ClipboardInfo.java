package spaetial.schematic;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record ClipboardInfo(UUID authorId) {
    public static final PacketCodec<PacketByteBuf, ClipboardInfo> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, ClipboardInfo::authorId,
        ClipboardInfo::new
    );
}

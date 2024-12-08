package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.Spaetial;

import java.util.UUID;

public record PasteClipboardC2SPacket(UUID placementId, BlockPos minPos, RegistryKey<World> dim) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<PasteClipboardC2SPacket> ID = new Id<>(Spaetial.id("paste_clipboard"));
    public static final PacketCodec<RegistryByteBuf, PasteClipboardC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, PasteClipboardC2SPacket::placementId,
        BlockPos.PACKET_CODEC, PasteClipboardC2SPacket::minPos,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), PasteClipboardC2SPacket::dim,
        PasteClipboardC2SPacket::new
    );
}

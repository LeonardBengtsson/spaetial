package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import spaetial.Spaetial;
import spaetial.editing.selection.Selection;

import java.util.UUID;

public record RequestRegionC2SPacket(UUID requestId, Selection selection, RegistryKey<World> dim) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestRegionC2SPacket> ID = new Id<>(Spaetial.id("request_region"));
    public static final PacketCodec<RegistryByteBuf, RequestRegionC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RequestRegionC2SPacket::requestId,
        Selection.PACKET_CODEC, RequestRegionC2SPacket::selection,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestRegionC2SPacket::dim,
        RequestRegionC2SPacket::new
    );
}

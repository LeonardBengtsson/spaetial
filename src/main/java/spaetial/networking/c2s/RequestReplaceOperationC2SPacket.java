package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import spaetial.Spaetial;
import spaetial.editing.Filter;
import spaetial.editing.Material;
import spaetial.editing.selection.Selection;

public record RequestReplaceOperationC2SPacket(Selection selection, RegistryKey<World> dim, Filter filter, Material material) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestReplaceOperationC2SPacket> ID = new Id<>(Spaetial.id("request_replace_operation"));
    public static final PacketCodec<RegistryByteBuf, RequestReplaceOperationC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Selection.PACKET_CODEC, RequestReplaceOperationC2SPacket::selection,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestReplaceOperationC2SPacket::dim,
        Filter.PACKET_CODEC, RequestReplaceOperationC2SPacket::filter,
        Material.PACKET_CODEC, RequestReplaceOperationC2SPacket::material,
        RequestReplaceOperationC2SPacket::new
    );
}

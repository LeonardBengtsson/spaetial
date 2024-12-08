package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.Spaetial;
import spaetial.editing.Filter;
import spaetial.editing.selection.Selection;
import spaetial.networking.PacketCodecsUtil;

public record RequestCloneOperationC2SPacket(Selection selection, RegistryKey<World> sourceDim, RegistryKey<World> destDim, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestCloneOperationC2SPacket> ID = new Id<>(Spaetial.id("request_clone_operation"));
    public static final PacketCodec<RegistryByteBuf, RequestCloneOperationC2SPacket> PACKET_CODEC = PacketCodecsUtil.tuple(
        Selection.PACKET_CODEC, RequestCloneOperationC2SPacket::selection,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestCloneOperationC2SPacket::sourceDim,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestCloneOperationC2SPacket::destDim,
        BlockPos.PACKET_CODEC, RequestCloneOperationC2SPacket::delta,
        PacketCodecs.BOOL, RequestCloneOperationC2SPacket::move,
        Filter.PACKET_CODEC, RequestCloneOperationC2SPacket::sourceFilter,
        Filter.PACKET_CODEC, RequestCloneOperationC2SPacket::destinationFilter,
        RequestCloneOperationC2SPacket::new
    );
}

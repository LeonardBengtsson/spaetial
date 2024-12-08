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

public record RequestLineStackOperationC2SPacket(Selection selection, RegistryKey<World> sourceDim, RegistryKey<World> destDim, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter, int stackSize, BlockPos spacing) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestLineStackOperationC2SPacket> ID = new Id<>(Spaetial.id("request_line_stack_operation"));
    public static final PacketCodec<RegistryByteBuf, RequestLineStackOperationC2SPacket> PACKET_CODEC = PacketCodecsUtil.tuple(
        Selection.PACKET_CODEC, RequestLineStackOperationC2SPacket::selection,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestLineStackOperationC2SPacket::sourceDim,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestLineStackOperationC2SPacket::destDim,
        BlockPos.PACKET_CODEC, RequestLineStackOperationC2SPacket::delta,
        PacketCodecs.BOOL, RequestLineStackOperationC2SPacket::move,
        Filter.PACKET_CODEC, RequestLineStackOperationC2SPacket::sourceFilter,
        Filter.PACKET_CODEC, RequestLineStackOperationC2SPacket::destinationFilter,
        PacketCodecs.INTEGER, RequestLineStackOperationC2SPacket::stackSize,
        BlockPos.PACKET_CODEC, RequestLineStackOperationC2SPacket::spacing,
        RequestLineStackOperationC2SPacket::new
    );
}

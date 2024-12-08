package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import spaetial.Spaetial;
import spaetial.editing.Filter;
import spaetial.editing.selection.Selection;
import spaetial.networking.PacketCodecsUtil;

public record RequestVolumeStackOperationC2SPacket(Selection selection, RegistryKey<World> sourceDim, RegistryKey<World> destDim, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter, Vec3i stackSize, BlockPos spacing) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestVolumeStackOperationC2SPacket> ID = new Id<>(Spaetial.id("request_volume_stack_operation"));
    public static final PacketCodec<RegistryByteBuf, RequestVolumeStackOperationC2SPacket> PACKET_CODEC = PacketCodecsUtil.tuple(
        Selection.PACKET_CODEC, RequestVolumeStackOperationC2SPacket::selection,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestVolumeStackOperationC2SPacket::sourceDim,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestVolumeStackOperationC2SPacket::destDim,
        BlockPos.PACKET_CODEC, RequestVolumeStackOperationC2SPacket::delta,
        PacketCodecs.BOOL, RequestVolumeStackOperationC2SPacket::move,
        Filter.PACKET_CODEC, RequestVolumeStackOperationC2SPacket::sourceFilter,
        Filter.PACKET_CODEC, RequestVolumeStackOperationC2SPacket::destinationFilter,
        PacketCodecsUtil.VEC3, RequestVolumeStackOperationC2SPacket::stackSize,
        BlockPos.PACKET_CODEC, RequestVolumeStackOperationC2SPacket::spacing,
        RequestVolumeStackOperationC2SPacket::new
    );
}

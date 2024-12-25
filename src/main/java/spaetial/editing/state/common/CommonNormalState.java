package spaetial.editing.state.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.editing.state.EditingStateType;
import spaetial.networking.PacketCodecsUtil;

public record CommonNormalState(RegistryKey<World> quickSetDim, BlockPos quickSetPos1, BlockPos quickSetPos2, boolean quickSetReplace) implements CommonEditingState {
    @Override
    public EditingStateType getType() { return EditingStateType.NORMAL; }
    public static final PacketCodec<PacketByteBuf, CommonNormalState> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.createNullable(PacketCodecsUtil.createBufMap(RegistryKey.createPacketCodec(RegistryKeys.WORLD))), CommonNormalState::quickSetDim,
        PacketCodecsUtil.createNullable(PacketCodecsUtil.createBufMap(BlockPos.PACKET_CODEC)), CommonNormalState::quickSetPos1,
        PacketCodecsUtil.createNullable(PacketCodecsUtil.createBufMap(BlockPos.PACKET_CODEC)), CommonNormalState::quickSetPos2,
        PacketCodecs.BOOLEAN, state -> state.quickSetReplace,
        CommonNormalState::new
    );
}

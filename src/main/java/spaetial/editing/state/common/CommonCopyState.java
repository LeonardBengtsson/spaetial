package spaetial.editing.state.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import spaetial.editing.selection.Selection;
import spaetial.editing.state.CopyStateMode;
import spaetial.editing.state.EditingStateType;
import spaetial.networking.PacketCodecsUtil;

public record CommonCopyState(
    RegistryKey<World> sourceDim,
    Selection selection,
    CopyStateMode mode,
    boolean isCutMode,
    RegistryKey<World> destDim,
    BlockPos delta,
    int lineStackSize,
    BlockPos lineStackSpacing,
    Vec3i volumeStackSize,
    BlockPos volumeStackSpacing
)
    implements CommonEditingState
{
    @Override
    public EditingStateType getType() { return EditingStateType.COPY; }
    public static final PacketCodec<PacketByteBuf, CommonEditingState> PACKET_CODEC = PacketCodecsUtil.tuple(
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), state -> ((CommonCopyState) state).sourceDim,
        Selection.PACKET_CODEC, state -> ((CommonCopyState) state).selection,
        PacketCodecsUtil.createEnum(CopyStateMode.class), state -> ((CommonCopyState) state).mode,
        PacketCodecs.BOOLEAN, state -> ((CommonCopyState) state).isCutMode,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), state -> ((CommonCopyState) state).destDim,
        BlockPos.PACKET_CODEC, state -> ((CommonCopyState) state).delta,
        PacketCodecs.INTEGER, state -> ((CommonCopyState) state).lineStackSize,
        BlockPos.PACKET_CODEC, state -> ((CommonCopyState) state).lineStackSpacing,
        PacketCodecsUtil.VEC3, state -> ((CommonCopyState) state).volumeStackSize,
        BlockPos.PACKET_CODEC, state -> ((CommonCopyState) state).volumeStackSpacing,
        CommonCopyState::new
    );
}

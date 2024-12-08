package spaetial.editing.state.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockBox;
import spaetial.editing.state.EditingStateType;
import spaetial.networking.PacketCodecsUtil;

public record CommonCuboidSelectionState(BlockBox bounds) implements CommonEditingState {
    @Override
    public EditingStateType getType() { return EditingStateType.CUBOID_SELECTION; }
    public static final PacketCodec<PacketByteBuf, CommonEditingState> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.BLOCK_BOX, state -> ((CommonCuboidSelectionState) state).bounds,
        CommonCuboidSelectionState::new
    );
}

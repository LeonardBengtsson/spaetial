package spaetial.editing.state.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Uuids;
import spaetial.editing.state.EditingStateType;

import java.util.UUID;

public record CommonActiveSchematicPlacementState(UUID placementId) implements CommonEditingState {
    @Override
    public EditingStateType getType() { return EditingStateType.ACTIVE_SCHEMATIC_PLACEMENT; }
    public static final PacketCodec<PacketByteBuf, CommonEditingState> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, state -> ((CommonActiveSchematicPlacementState) state).placementId,
        CommonActiveSchematicPlacementState::new
    );
}

package spaetial.editing.state.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.editing.state.EditingStateType;
import spaetial.networking.PacketCodecsUtil;

public record CommonTurnedOffState() implements CommonEditingState {
    @Override
    public EditingStateType getType() { return EditingStateType.TURNED_OFF; }
    public static final PacketCodec<PacketByteBuf, CommonTurnedOffState> PACKET_CODEC
        = PacketCodecsUtil.createStatic(new CommonTurnedOffState());
}

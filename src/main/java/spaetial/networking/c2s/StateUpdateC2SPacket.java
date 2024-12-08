package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import spaetial.Spaetial;
import spaetial.editing.state.common.CommonEditingState;

public record StateUpdateC2SPacket(CommonEditingState state) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<StateUpdateC2SPacket> ID = new Id<>(Spaetial.id("state_update"));
    public static final PacketCodec<RegistryByteBuf, StateUpdateC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        CommonEditingState.PACKET_CODEC, StateUpdateC2SPacket::state,
        StateUpdateC2SPacket::new
    );
}

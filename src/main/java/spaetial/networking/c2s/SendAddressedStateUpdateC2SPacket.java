package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;
import spaetial.editing.state.common.CommonEditingState;

import java.util.UUID;

@Deprecated
public record SendAddressedStateUpdateC2SPacket(UUID addressee, CommonEditingState state) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<SendAddressedStateUpdateC2SPacket> ID = new Id<>(Spaetial.id("send_addressed_state_update"));
    public static final PacketCodec<RegistryByteBuf, SendAddressedStateUpdateC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SendAddressedStateUpdateC2SPacket::addressee,
        CommonEditingState.PACKET_CODEC, SendAddressedStateUpdateC2SPacket::state,
        SendAddressedStateUpdateC2SPacket::new
    );
}

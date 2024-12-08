package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;

import java.util.UUID;

@Deprecated
public record RequestAddressedStateUpdateS2CPacket(UUID addressee) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestAddressedStateUpdateS2CPacket> ID = new Id<>(Spaetial.id("request_addressed_state_update"));
    public static final PacketCodec<RegistryByteBuf, RequestAddressedStateUpdateS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RequestAddressedStateUpdateS2CPacket::addressee,
        RequestAddressedStateUpdateS2CPacket::new
    );
}

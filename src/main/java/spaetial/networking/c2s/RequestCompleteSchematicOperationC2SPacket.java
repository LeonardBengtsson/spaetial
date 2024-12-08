package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import spaetial.Spaetial;
import spaetial.editing.Filter;

import java.util.UUID;

public record RequestCompleteSchematicOperationC2SPacket(UUID placementId, Filter filter) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<RequestCompleteSchematicOperationC2SPacket> ID = new Id<>(Spaetial.id("request_complete_schematic_operation"));
    public static final PacketCodec<RegistryByteBuf, RequestCompleteSchematicOperationC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RequestCompleteSchematicOperationC2SPacket::placementId,
        Filter.PACKET_CODEC, RequestCompleteSchematicOperationC2SPacket::filter,
        RequestCompleteSchematicOperationC2SPacket::new
    );
}

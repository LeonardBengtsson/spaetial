package spaetial.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;
import spaetial.editing.Filter;
import spaetial.editing.selection.Selection;
import spaetial.networking.PacketCodecsUtil;

import java.util.UUID;

public record RequestCopyOperationC2SPacket(Selection selection, RegistryKey<World> dim, boolean cut, Filter filter, @Nullable UUID regionRequestId) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final CustomPayload.Id<RequestCopyOperationC2SPacket> ID = new CustomPayload.Id<>(Spaetial.id("request_copy_operation"));
    public static final PacketCodec<RegistryByteBuf, RequestCopyOperationC2SPacket> PACKET_CODEC = PacketCodec.<RegistryByteBuf, RequestCopyOperationC2SPacket, Selection, RegistryKey<World>, Boolean, Filter, UUID>tuple(
        Selection.PACKET_CODEC, RequestCopyOperationC2SPacket::selection,
        RegistryKey.createPacketCodec(RegistryKeys.WORLD), RequestCopyOperationC2SPacket::dim,
        PacketCodecs.BOOLEAN, RequestCopyOperationC2SPacket::cut,
        Filter.PACKET_CODEC, RequestCopyOperationC2SPacket::filter,
        PacketCodecsUtil.createNullable(PacketCodecsUtil.createBufMap(Uuids.PACKET_CODEC)), RequestCopyOperationC2SPacket::regionRequestId,
        RequestCopyOperationC2SPacket::new
    );
}

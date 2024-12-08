package spaetial.editing.state.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.editing.state.EditingStateType;
import spaetial.networking.VariantPacketCodec;
import spaetial.util.functional.TypeSupplier;

public interface CommonEditingState extends TypeSupplier<EditingStateType> {
    PacketCodec<PacketByteBuf, CommonEditingState> PACKET_CODEC = new VariantPacketCodec<>(EditingStateType.class);
}

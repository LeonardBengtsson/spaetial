package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.Spaetial;
import spaetial.networking.VariantPacketCodec;
import spaetial.util.Translatable;
import spaetial.util.functional.TypeSupplier;

public interface Message extends Translatable, TypeSupplier<MessageType> {
    PacketCodec<PacketByteBuf, Message> PACKET_CODEC = new VariantPacketCodec<>(MessageType.class);
    @Override
    default String getTranslationKey() {
        return Spaetial.translationKey(null, "error", getType().name, "message");
    }
}

package spaetial.networking;

import net.minecraft.network.codec.PacketCodec;

public interface PacketCodecSupplier<B, V> {
    PacketCodec<B, ? extends V> getCodec();
}

package spaetial.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.util.functional.TypeSupplier;

public class VariantPacketCodec
<
    B extends PacketByteBuf,
    V extends TypeSupplier<T>,
    T extends Enum<T> & PacketCodecSupplier<B, V>
>
    implements PacketCodec<B, V>
{
    private final Class<T> enumClass;

    public VariantPacketCodec(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public V decode(B buf) {
        var type = buf.readEnumConstant(enumClass);
        return type.getCodec().decode(buf);
    }

    @Override
    public void encode(B buf, V value) {
        buf.writeEnumConstant(value.getType());
        // assert that codec is of type PacketCodec<B, T extends V>, where value is an instance of T
        PacketCodec<B, V> codec = (PacketCodec<B, V>) value.getType().getCodec();
        codec.encode(buf, value);
    }
}

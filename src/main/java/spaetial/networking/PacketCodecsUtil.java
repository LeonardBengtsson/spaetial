package spaetial.networking;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.selection.Selection;
import spaetial.editing.state.common.CommonEditingState;
import spaetial.util.BoxUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PacketCodecsUtil {
    private PacketCodecsUtil() {}

    public static <V1, V2> PacketCodec<PacketByteBuf, V2> createValueMap(
        PacketCodec<PacketByteBuf, V1> codec,
        Function<V1, V2> decode,
        Function<V2, V1> encode) {
        return new PacketCodec<>() {
            @Override public V2 decode(PacketByteBuf buf) { return decode.apply(codec.decode(buf)); }
            @Override public void encode(PacketByteBuf buf, V2 value) { codec.encode(buf, encode.apply(value)); }
        };
    }

    public static <B1, B2 extends B1, V> PacketCodec<B2, V> createBufMap(PacketCodec<B1, V> codec) {
        return new PacketCodec<>() {
            @Override public V decode(B2 buf) { return codec.decode(buf); }
            @Override public void encode(B2 buf, V value) { codec.encode(buf, value); }
        };
    }

    public static <V> PacketCodec<PacketByteBuf, @Nullable V> createNullable(PacketCodec<PacketByteBuf, V> codec) {
        return new PacketCodec<>() {
            @Override public V decode(PacketByteBuf buf) {
                if (buf.readBoolean()) {
                    return codec.decode(buf);
                } else {
                    return null;
                }
            }
            @Override public void encode(PacketByteBuf buf, V value) {
                if (value == null) {
                    buf.writeBoolean(false);
                } else {
                    buf.writeBoolean(true);
                    codec.encode(buf, value);
                }
            }
        };
    }

    public static <V> PacketCodec<PacketByteBuf, V> createStatic(final V value) {
        return new PacketCodec<>() {
            @Override public V decode(PacketByteBuf buf) { return value; }
            @Override public void encode(PacketByteBuf buf, V value) { }
        };
    }

    public static <V> PacketCodec<PacketByteBuf, V> createSupplier(Supplier<V> supplier) {
        return new PacketCodec<>() {
            @Override public V decode(PacketByteBuf buf) { return supplier.get(); }
            @Override public void encode(PacketByteBuf buf, V value) { }
        };
    }

    public static <V extends Enum<V>> PacketCodec<PacketByteBuf, V> createEnum(Class<V> enumClass) {
        return new PacketCodec<>() {
            @Override public V decode(PacketByteBuf buf) { return buf.readEnumConstant(enumClass); }
            @Override public void encode(PacketByteBuf buf, V value) { buf.writeEnumConstant(value); }
        };
    }

    public static <V> PacketCodec<PacketByteBuf, List<V>> createList(PacketCodec<PacketByteBuf, V> codec) {
        return new PacketCodec<>() {
            @Override public List<V> decode(PacketByteBuf buf) {
                try {
                    int size = buf.readInt();
                    var list = new ArrayList<V>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(codec.decode(buf));
                    }
                    return list;
                } catch (Throwable e) {
                    throw new BufDecoderException(e);
                }
            }
            @Override public void encode(PacketByteBuf buf, List<V> value) {
                buf.writeInt(value.size());
                value.forEach(v -> codec.encode(buf, v));
            }
        };
    }

    public static <V> PacketCodec<PacketByteBuf, HashSet<V>> createSet(PacketCodec<PacketByteBuf, V> codec) {
        return createValueMap(PacketCodecsUtil.createList(codec), HashSet::new, set -> set.stream().toList());
    }

    public static final PacketCodec<PacketByteBuf, Block> BLOCK = new PacketCodec<>() {
        @Override
        public Block decode(PacketByteBuf buf) {
            var identifier = buf.readIdentifier();
            var block = Registries.BLOCK.get(identifier);
            assert block != Registries.BLOCK.getDefaultEntry().orElseThrow().value() || identifier == Registries.BLOCK.getDefaultId();
            return block;
        }

        @Override
        public void encode(PacketByteBuf buf, Block value) {
            var id = Registries.BLOCK.getEntry(value).getKey().map(RegistryKey::getValue).orElseThrow();
            buf.writeIdentifier(id);
        }
    };

    public static final PacketCodec<PacketByteBuf, Void> EMPTY = createStatic(null);

    public static final PacketCodec<PacketByteBuf, BlockState> BLOCK_STATE = new PacketCodec<>() {
        @Override
        public BlockState decode(PacketByteBuf buf) {
            return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), Objects.requireNonNull(buf.readNbt()));
        }

        @Override
        public void encode(PacketByteBuf buf, BlockState value) {
            buf.writeNbt(NbtHelper.fromBlockState(value));
        }
    };

    public static final PacketCodec<PacketByteBuf, Vec3i> VEC3 = new PacketCodec<>() {
        @Override
        public Vec3i decode(PacketByteBuf buf) {
            try {
                int x = buf.readInt(), y = buf.readInt(), z = buf.readInt();
                return new Vec3i(x, y, z);
            } catch (Throwable e) {
                throw new BufDecoderException(e);
            }
        }

        @Override
        public void encode(PacketByteBuf buf, Vec3i value) {
            buf.writeInt(value.getX());
            buf.writeInt(value.getY());
            buf.writeInt(value.getZ());
        }
    };

    public static final PacketCodec<PacketByteBuf, BlockBox> BLOCK_BOX = new PacketCodec<>() {
        @Override
        public BlockBox decode(PacketByteBuf buf) {
            try {
                BlockPos min = buf.readBlockPos(), max = buf.readBlockPos();
                return BoxUtil.fromMinAndMax(min, max);
            } catch (Throwable e) {
                throw new BufDecoderException(e);
            }
        }

        @Override
        public void encode(PacketByteBuf buf, BlockBox value) {
            buf.writeBlockPos(BoxUtil.minPos(value));
            buf.writeBlockPos(BoxUtil.maxPos(value));
        }
    };

    public static final PacketCodec<PacketByteBuf, BitSet> BIT_SET = new PacketCodec<>() {
        @Override public BitSet decode(PacketByteBuf buf) { return buf.readBitSet(); }
        @Override public void encode(PacketByteBuf buf, BitSet value) { buf.writeBitSet(value); }
    };

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> PacketCodec<B, C> tuple(
        final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1,
        final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2,
        final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3,
        final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4,
        final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5,
        final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6,
        final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7,
        final Function7<T1, T2, T3, T4, T5, T6, T7, C> to)
    {
        return new PacketCodec<>(){
            @Override
            public C decode(B object) {
                T1 value1 = codec1.decode(object);
                T2 value2 = codec2.decode(object);
                T3 value3 = codec3.decode(object);
                T4 value4 = codec4.decode(object);
                T5 value5 = codec5.decode(object);
                T6 value6 = codec6.decode(object);
                T7 value7 = codec7.decode(object);
                return to.apply(value1, value2, value3, value4, value5, value6, value7);
            }

            @Override
            public void encode(B buf, C value) {
                codec1.encode(buf, from1.apply(value));
                codec2.encode(buf, from2.apply(value));
                codec3.encode(buf, from3.apply(value));
                codec4.encode(buf, from4.apply(value));
                codec5.encode(buf, from5.apply(value));
                codec6.encode(buf, from6.apply(value));
                codec7.encode(buf, from7.apply(value));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> PacketCodec<B, C> tuple(
        final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1,
        final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2,
        final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3,
        final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4,
        final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5,
        final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6,
        final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7,
        final PacketCodec<? super B, T8> codec8, final Function<C, T8> from8,
        final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> to)
    {
        return new PacketCodec<>(){
            @Override
            public C decode(B object) {
                T1 value1 = codec1.decode(object);
                T2 value2 = codec2.decode(object);
                T3 value3 = codec3.decode(object);
                T4 value4 = codec4.decode(object);
                T5 value5 = codec5.decode(object);
                T6 value6 = codec6.decode(object);
                T7 value7 = codec7.decode(object);
                T8 value8 = codec8.decode(object);
                return to.apply(value1, value2, value3, value4, value5, value6, value7, value8);
            }

            @Override
            public void encode(B buf, C value) {
                codec1.encode(buf, from1.apply(value));
                codec2.encode(buf, from2.apply(value));
                codec3.encode(buf, from3.apply(value));
                codec4.encode(buf, from4.apply(value));
                codec5.encode(buf, from5.apply(value));
                codec6.encode(buf, from6.apply(value));
                codec7.encode(buf, from7.apply(value));
                codec8.encode(buf, from8.apply(value));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> PacketCodec<B, C> tuple(
        final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1,
        final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2,
        final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3,
        final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4,
        final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5,
        final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6,
        final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7,
        final PacketCodec<? super B, T8> codec8, final Function<C, T8> from8,
        final PacketCodec<? super B, T9> codec9, final Function<C, T9> from9,
        final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> to)
    {
        return new PacketCodec<>(){
            @Override
            public C decode(B object) {
                T1 value1 = codec1.decode(object);
                T2 value2 = codec2.decode(object);
                T3 value3 = codec3.decode(object);
                T4 value4 = codec4.decode(object);
                T5 value5 = codec5.decode(object);
                T6 value6 = codec6.decode(object);
                T7 value7 = codec7.decode(object);
                T8 value8 = codec8.decode(object);
                T9 value9 = codec9.decode(object);
                return to.apply(value1, value2, value3, value4, value5, value6, value7, value8, value9);
            }

            @Override
            public void encode(B buf, C value) {
                codec1.encode(buf, from1.apply(value));
                codec2.encode(buf, from2.apply(value));
                codec3.encode(buf, from3.apply(value));
                codec4.encode(buf, from4.apply(value));
                codec5.encode(buf, from5.apply(value));
                codec6.encode(buf, from6.apply(value));
                codec7.encode(buf, from7.apply(value));
                codec8.encode(buf, from8.apply(value));
                codec9.encode(buf, from9.apply(value));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> PacketCodec<B, C> tuple(
        final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1,
        final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2,
        final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3,
        final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4,
        final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5,
        final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6,
        final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7,
        final PacketCodec<? super B, T8> codec8, final Function<C, T8> from8,
        final PacketCodec<? super B, T9> codec9, final Function<C, T9> from9,
        final PacketCodec<? super B, T10> codec10, final Function<C, T10> from10,
        final Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> to)
    {
        return new PacketCodec<>(){
            @Override
            public C decode(B object) {
                T1 value1 = codec1.decode(object);
                T2 value2 = codec2.decode(object);
                T3 value3 = codec3.decode(object);
                T4 value4 = codec4.decode(object);
                T5 value5 = codec5.decode(object);
                T6 value6 = codec6.decode(object);
                T7 value7 = codec7.decode(object);
                T8 value8 = codec8.decode(object);
                T9 value9 = codec9.decode(object);
                T10 value10 = codec10.decode(object);
                return to.apply(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
            }

            @Override
            public void encode(B buf, C value) {
                codec1.encode(buf, from1.apply(value));
                codec2.encode(buf, from2.apply(value));
                codec3.encode(buf, from3.apply(value));
                codec4.encode(buf, from4.apply(value));
                codec5.encode(buf, from5.apply(value));
                codec6.encode(buf, from6.apply(value));
                codec7.encode(buf, from7.apply(value));
                codec8.encode(buf, from8.apply(value));
                codec9.encode(buf, from9.apply(value));
                codec10.encode(buf, from10.apply(value));
            }
        };
    }
}

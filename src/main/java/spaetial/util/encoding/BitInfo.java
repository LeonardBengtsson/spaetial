package spaetial.util.encoding;

import java.util.BitSet;

public record BitInfo(BitSet bitSet, int length) {
    public static BitInfo fromBytes(byte[] bytes, byte mod) {
        assert mod >= 0 && mod < 8;
        int bitLength = 8 * bytes.length + mod - (mod == 0 ? 0 : 8);
        BitSet bits = new BitSet(bitLength);
        for (int i = 0; i < bitLength; i++) {
            if (((bytes[i / 8] >> (i % 8)) & 0x1) == 1) bits.set(i);
        }
        return new BitInfo(bits, bitLength);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (int i = 0; i < length; i++) {
            builder.append(bitSet.get(i) ? '1' : '0');
        }
        builder.append(')');
        return builder.toString();
    }

    public byte[] getArray() {
        byte[] out = new byte[length / 8 + (length % 8 == 0 ? 0 : 1)];
        for (int i = 0; i < out.length; i++) {
            out[i] = getByte(bitSet.get(i * 8, i * 8 + 8));
        }
        return out;
    }

    private static byte getByte(BitSet set) {
        return (byte) ((set.get(0) ? 0x1 : 0)  | (set.get(1) ? 0x2 : 0)  | (set.get(2) ? 0x4 : 0)  | (set.get(3) ? 0x8 : 0)
                     | (set.get(4) ? 0x10 : 0) | (set.get(5) ? 0x20 : 0) | (set.get(6) ? 0x40 : 0) | (set.get(7) ? 0x80 : 0));
    }
}

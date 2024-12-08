package spaetial.util.encoding;

import java.util.Arrays;
import java.util.BitSet;

public final class ByteCompression {
    private ByteCompression() {}

    public static BitInfo compressBytes(byte[] bytes, int bitsPerByte) {
        // example:
        // 00100111,00001110,00101010,00111111,00001100, 6 bits per byte
        // 1     2      3      4      5
        // 10011100,11101010,10111111,001100__, size: 4, mod: 6

        assert bitsPerByte > 0 && bitsPerByte <= 8;

        int length = bytes.length * bitsPerByte;
        BitSet bits = new BitSet(length);
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bitsPerByte; j++) {
                int index = i * bitsPerByte + j;
                boolean set = ((bytes[i] >> j) & 1) == 1;

                if (set) bits.set(index);
            }
        }
        return new BitInfo(bits, length);
    }

    public static byte[] decompressBytes(BitInfo bi, int bitsPerByte) {
        assert bitsPerByte > 0 && bitsPerByte <= 8;

        byte[] bytes = new byte[bi.length() / bitsPerByte];
        for (int i = 0; i < bytes.length; i++) {
            byte b = 0;
            for (int j = 0; j < bitsPerByte; j++) {
                int index = i * bitsPerByte + j;
                byte set = (byte) (bi.bitSet().get(index) ? 1 : 0);

                b |= set << j;
            }
            bytes[i] = b;
        }
        return bytes;
    }

    public static BitInfo compressString(String string, CompressionFormat format) throws IllegalArgumentException {
        char[] chars = new char[string.length() * 2];
        int charCount = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            int escapeIndex = format.escapedLookup.indexOf(c);
            if (escapeIndex >= 0) {
                chars[charCount] = '\\';
                chars[charCount + 1] = format.lookup.charAt(escapeIndex);
                charCount += 2;
            } else {
                chars[charCount] = c;
                charCount++;
            }
        }
        chars = Arrays.copyOfRange(chars, 0, charCount);

        byte[] bytes = new byte[chars.length * (1 + format.payloadBytes)];
        int used = 0;
        for (int c : chars) {
            if (c < 128) {
                byte val = format.reverseLookup[c];
                if (val >= 0) {
                    bytes[used] = val;
                    used++;
                    continue;
                }
            }
            bytes[used] = format.highBits;
            for (int i = 0; i < format.payloadBytes; i++) {
                bytes[used + 1 + i] = (byte) ((c >>> (format.bitSize * i)) & format.highBits);
            }
            used += format.payloadBytes + 1;
        }
        return compressBytes(Arrays.copyOfRange(bytes, 0, used), format.bitSize);
    }

    public static String decompressString(BitInfo bi, CompressionFormat format) throws IllegalArgumentException {
        byte[] bytes = decompressBytes(bi, format.bitSize);

        char[] chars = new char[bytes.length];
        int used = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b == format.highBits) {
                if (i >= bytes.length - format.payloadBytes) throw new IllegalArgumentException("Invalid byte array length.");

                int v = 0;
                for (int j = 0; j < format.payloadBytes; j++) {
                    v |= bytes[i + 1 + j] << (format.bitSize * j);
                }

                if (v > 65536) throw new IllegalArgumentException("Invalid bytes: " + Integer.toHexString(v));
                chars[used] = (char) v;

                i += format.payloadBytes;
            } else {
                char c = format.lookup.charAt(b);
                if (c == '\\') {
                    chars[used] = format.escapedLookup.charAt(bytes[i + 1]);
                    i++;
                } else {
                    chars[used] = c;
                }
            }
            used++;
        }
        return new String(Arrays.copyOfRange(chars, 0, used));
    }

    public enum CompressionFormat {
        TEXT(
                6,
                "abcdefghijklmnopqrstuvwxyz0123456789 _.,:\"'()[]{}+-*/=@#!?~^<>\\",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ§£¤%&|;°±µ·«»¡¿\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\uE000\\"
        ),
        BLOCK_STATE_OPTIMIZED(
                4,
                "esiarntolcd_=,\\",
                "ugpmhbyfvkw[]:\\"
        ),
        NBT_OPTIMIZED(
                5,
                "etranicosmfldpbgukhwyxzv0_\":, \\",
                "qj1395427FB6T8IDCGLOAXHN{}[].-\\"
        );
        public final String lookup;
        public final String escapedLookup;
        public final byte[] reverseLookup;
        public final byte[] reverseEscapedLookup;
        public final int bitSize;
        public final int payloadBytes;
        public final byte highBits;
        CompressionFormat(int size, String normal, String escape) {
            assert size > 0 && size <= 7;
            int s = (int) (Math.pow(2, size) - 1);
            assert normal.length() == s;
            assert escape.length() == s;

            lookup = normal;
            escapedLookup = escape;
            bitSize = size;
            payloadBytes = (int) Math.ceil((double) 16 / size);
            highBits = switch (size) {
                case 1 -> 0x1;
                case 2 -> 0x3;
                case 3 -> 0x7;
                case 4 -> 0xf;
                case 5 -> 0x1f;
                case 6 -> 0x3f;
                case 7 -> 0x7f;
                default -> 0;
            };

            reverseLookup = new byte[128];
            for (int i = 0; i < 128; i++) {
                reverseLookup[i] = (byte) lookup.indexOf((char) i);
            }

            reverseEscapedLookup = new byte[128];
            for (int i = 0; i < 128; i++) {
                reverseEscapedLookup[i] = (byte) escapedLookup.indexOf((char) i);
            }
        }
    }
}

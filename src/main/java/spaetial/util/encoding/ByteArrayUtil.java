package spaetial.util.encoding;

import java.util.List;

public final class ByteArrayUtil {
    private ByteArrayUtil() {}

    public static byte[] flattenListOfByteArrays(List<byte[]> list, int extraSpaceBefore) {
        int length = extraSpaceBefore;
        for (byte[] b : list) length += b.length;
        byte[] out = new byte[length];
        int index = extraSpaceBefore;
        for (byte[] b : list) {
            System.arraycopy(b, 0, out, index, b.length);
            index += b.length;
        }
        return out;
    }

    public static byte[] flattenArrayOfByteArrays(byte[][] array, int extraSpaceBefore) {
        int length = extraSpaceBefore;
        for (byte[] b : array) length += b.length;
        byte[] out = new byte[length];
        int index = extraSpaceBefore;
        for (byte[] b : array) {
            System.arraycopy(b, 0, out, index, b.length);
            index += b.length;
        }
        return out;
    }
}

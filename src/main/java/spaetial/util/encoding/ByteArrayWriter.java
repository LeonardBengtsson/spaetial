package spaetial.util.encoding;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteArrayWriter {
    private byte[] bytes;
    private int index;

    public ByteArrayWriter() {
        bytes = new byte[1];
        index = 0;
    }

    public ByteArrayWriter(int initialCapacity) {
        assert initialCapacity >= 0;
        bytes = new byte[initialCapacity];
        index = initialCapacity;
    }

    private void makeSpace(int byteCount) {
        if (bytes.length - index < byteCount) {
            int newSize = 1 << (32 - Integer.numberOfLeadingZeros(index + byteCount));
            var newArray = new byte[newSize];
            System.arraycopy(bytes, 0, newArray, 0, bytes.length);
            bytes = newArray;
        }
    }

    public void write(byte b) {
        makeSpace(1);
        bytes[index] = b;
        index++;
    }

    public void write(byte[] b) {
        makeSpace(b.length);
        System.arraycopy(b, 0, bytes, index, b.length);
        index += b.length;
    }

    public void writeShort(short s) {
        makeSpace(2);
        bytes[index] = (byte) ((s >> 8) & 0xff);
        bytes[index + 1] = (byte) (s & 0xff);
        index += 2;
    }

    public void writeInt(int i) {
        makeSpace(4);
        bytes[index] = (byte) ((i >> 24) & 0xff);
        bytes[index + 1] = (byte) ((i >> 16) & 0xff);
        bytes[index + 2] = (byte) ((i >> 8) & 0xff);
        bytes[index + 3] = (byte) (i & 0xff);
        index += 4;
    }

    public void writeLong(long l) {
        makeSpace(8);
        bytes[index] = (byte) ((l >> 56) & 0xff);
        bytes[index + 1] = (byte) ((l >> 48) & 0xff);
        bytes[index + 2] = (byte) ((l >> 40) & 0xff);
        bytes[index + 3] = (byte) ((l >> 32) & 0xff);
        bytes[index + 4] = (byte) ((l >> 24) & 0xff);
        bytes[index + 5] = (byte) ((l >> 16) & 0xff);
        bytes[index + 6] = (byte) ((l >> 8) & 0xff);
        bytes[index + 7] = (byte) (l & 0xff);
        index += 8;
    }

    public void writeLongReverse(long l) {
        makeSpace(8);
        bytes[index] = (byte) (l & 0xff);
        bytes[index + 1] = (byte) ((l >> 8) & 0xff);
        bytes[index + 2] = (byte) ((l >> 16) & 0xff);
        bytes[index + 3] = (byte) ((l >> 24) & 0xff);
        bytes[index + 4] = (byte) ((l >> 32) & 0xff);
        bytes[index + 5] = (byte) ((l >> 40) & 0xff);
        bytes[index + 6] = (byte) ((l >> 48) & 0xff);
        bytes[index + 7] = (byte) ((l >> 56) & 0xff);
        index += 8;
    }

    public void writeVec3i(Vec3i pos) {
        makeSpace(12);
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        bytes[index] = (byte) ((x >> 24) & 0xff);
        bytes[index + 1] = (byte) ((x >> 16) & 0xff);
        bytes[index + 2] = (byte) ((x >> 8) & 0xff);
        bytes[index + 3] = (byte) (x & 0xff);
        bytes[index + 4] = (byte) ((y >> 24) & 0xff);
        bytes[index + 5] = (byte) ((y >> 16) & 0xff);
        bytes[index + 6] = (byte) ((y >> 8) & 0xff);
        bytes[index + 7] = (byte) (y & 0xff);
        bytes[index + 8] = (byte) ((z >> 24) & 0xff);
        bytes[index + 9] = (byte) ((z >> 16) & 0xff);
        bytes[index + 10] = (byte) ((z >> 8) & 0xff);
        bytes[index + 11] = (byte) (z & 0xff);
        index += 12;
    }

    public void writeUuid(UUID uuid) {
        makeSpace(16);
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        bytes[index] = (byte) (most & 0xff);
        bytes[index + 1] = (byte) ((most >> 8) & 0xff);
        bytes[index + 2] = (byte) ((most >> 16) & 0xff);
        bytes[index + 3] = (byte) ((most >> 24) & 0xff);
        bytes[index + 4] = (byte) ((most >> 32) & 0xff);
        bytes[index + 5] = (byte) ((most >> 40) & 0xff);
        bytes[index + 6] = (byte) ((most >> 48) & 0xff);
        bytes[index + 7] = (byte) ((most >> 56) & 0xff);
        bytes[index + 8] = (byte) (least & 0xff);
        bytes[index + 9] = (byte) ((least >> 8) & 0xff);
        bytes[index + 10] = (byte) ((least >> 16) & 0xff);
        bytes[index + 11] = (byte) ((least >> 24) & 0xff);
        bytes[index + 12] = (byte) ((least >> 32) & 0xff);
        bytes[index + 13] = (byte) ((least >> 40) & 0xff);
        bytes[index + 14] = (byte) ((least >> 48) & 0xff);
        bytes[index + 15] = (byte) ((least >> 56) & 0xff);
        index += 16;
    }

    public void writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        int dataLength = 4 + length;
        makeSpace(dataLength);
        bytes[index] = (byte) ((length >> 24) & 0xff);
        bytes[index + 1] = (byte) ((length >> 16) & 0xff);
        bytes[index + 2] = (byte) ((length >> 8) & 0xff);
        bytes[index + 3] = (byte) (length & 0xff);
        System.arraycopy(bytes, 0, bytes, index + 4, bytes.length);
        index += dataLength;
    }

    public byte[] get() {
        var out = new byte[index];
        System.arraycopy(bytes, 0, out, 0, index);
        return out;
    }
}

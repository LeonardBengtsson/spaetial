package spaetial.util.encoding;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class ByteArrayReader {
    private final byte[] bytes;
    private int index = 0;
    public ByteArrayReader(byte[] bytes) { this.bytes = bytes; }

    public int getIndex() {
        return index;
    }

    public int totalLength() { return bytes.length; }

    public int bytesLeft() { return bytes.length - index - 1; }

    public boolean canRead() {
        return index < bytes.length;
    }

    public boolean canRead(int indices) {
        return index + indices <= bytes.length;
    }

    public byte read() throws IndexOutOfBoundsException {
        if (index >= bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 1) + " is out of bounds for array of length " + bytes.length);
        return bytes[index++];
    }

    public byte[] readArray(int count) throws IndexOutOfBoundsException {
        if (index + count > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + count) + " is out of bounds for array of length " + bytes.length);
        byte[] out = Arrays.copyOfRange(bytes, index, index + count);
        index += count;
        return out;
    }

    public short readShort() throws IndexOutOfBoundsException {
        if (index + 2 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 2) + " is out of bounds for array of length " + bytes.length);
        short out = (short) ((bytes[index]&0xff) << 8);
        out |= (bytes[index + 1]&0xff);
        index += 2;
        return out;
    }

    public int readInt() throws IndexOutOfBoundsException {
        if (index + 4 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 4) + " is out of bounds for array of length " + bytes.length);
        int out = (bytes[index]&0xff) << 24;
        out |= (bytes[index + 1]&0xff) << 16;
        out |= (bytes[index + 2]&0xff) << 8;
        out |= (bytes[index + 3]&0xff);
        index += 4;
        return out;
    }

    public long readLong() throws IndexOutOfBoundsException {
        if (index + 8 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 8) + " is out of bounds for array of length " + bytes.length);
        long out = (long) (bytes[index]&0xff) << 56;
        out |= (long) (bytes[index + 1]&0xff) << 48;
        out |= (long) (bytes[index + 2]&0xff) << 40;
        out |= (long) (bytes[index + 3]&0xff) << 32;
        out |= (long) (bytes[index + 4]&0xff) << 24;
        out |= (long) (bytes[index + 5]&0xff) << 16;
        out |= (long) (bytes[index + 6]&0xff) << 8;
        out |= (bytes[index + 7]&0xff);
        index += 8;
        return out;
    }

    public long readLongReverse() throws IndexOutOfBoundsException {
        if (index + 8 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 8) + " is out of bounds for array of length " + bytes.length);
        long out = (bytes[index]&0xff);
        out |= (long) (bytes[index + 1]&0xff) << 8;
        out |= (long) (bytes[index + 2]&0xff) << 16;
        out |= (long) (bytes[index + 3]&0xff) << 24;
        out |= (long) (bytes[index + 4]&0xff) << 32;
        out |= (long) (bytes[index + 5]&0xff) << 40;
        out |= (long) (bytes[index + 6]&0xff) << 48;
        out |= (long) (bytes[index + 7]&0xff) << 56;
        index += 8;
        return out;
    }

    public BlockPos readBlockPos() throws IndexOutOfBoundsException {
        if (index + 12 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 8) + " is out of bounds for array of length " + bytes.length);
        int x = readInt(), y = readInt(), z = readInt();
        return new BlockPos(x, y, z);
    }

    public Vec3i readVec3i() throws IndexOutOfBoundsException {
        if (index + 12 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 8) + " is out of bounds for array of length " + bytes.length);
        int x = readInt(), y = readInt(), z = readInt();
        return new Vec3i(x, y, z);
    }

    public UUID readUuid() throws IndexOutOfBoundsException {
        if (index + 16 > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 8) + " is out of bounds for array of length " + bytes.length);
        long most = readLong(), least = readLong();
        return new UUID(most, least);
    }

    public String readString() throws IndexOutOfBoundsException {
        var length = readInt();
        var bytes = readArray(length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte peek() throws IndexOutOfBoundsException {
        if (index >= bytes.length) throw new IndexOutOfBoundsException("Index " + (index + 1) + " is out of bounds for array of length " + bytes.length);
        return bytes[index];
    }

    public byte[] peekArray(int count) throws IndexOutOfBoundsException {
        if (index + count > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + count) + " is out of bounds for array of length " + bytes.length);
        byte[] out = Arrays.copyOfRange(bytes, index, index + count);
        return out;
    }

    public byte[] peekAll() {
        return Arrays.copyOfRange(bytes, index, bytes.length);
    }

    public void skip(int count) throws IndexOutOfBoundsException {
        if (index + count > bytes.length) throw new IndexOutOfBoundsException("Index " + (index + count) + " is out of bounds for array of length " + bytes.length);
        index += count;
    }
}

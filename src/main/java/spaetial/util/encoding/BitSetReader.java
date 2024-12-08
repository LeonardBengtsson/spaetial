package spaetial.util.encoding;

public class BitSetReader {
    private final BitInfo bits;
    private int index = 0;
    public BitSetReader(BitInfo bits) { this.bits = bits; }

    public int getIndex() { return index; }

    public boolean cantRead() {
        return index >= bits.length();
    }

    public boolean cantRead(int indices) {
        return index + indices > bits.length();
    }

    public boolean read() {
        return bits.bitSet().get(index++);
    }

    public boolean moveIfMatches(boolean... bools) {
        if (cantRead(bools.length)) return false;
        int i = index;
        for (boolean b : bools) {
            if (b ^ bits.bitSet().get(i++)) return false;
        }
        index = i;
        return true;
    }

    public boolean expect(boolean... bools) throws IndexOutOfBoundsException {
        if (cantRead(bools.length)) throw new IndexOutOfBoundsException("Ran out of index reading bits at index " + index);
        for (boolean b : bools) {
            if (b ^ read()) return false;
        }
        return true;
    }

    public short readShort(int bits) {
        assert bits > 0 && bits <= 16;
        short out = 0;
        for (int i = bits - 1; i >= 0; i--) {
            if (read()) out |= 1 << i;
        }
        return out;
    }
}

package com.priyakdey;

public class PrimitiveLongHashSet {
    private static final byte EMPTY = 0;
    private static final byte FULL = 1;

    private long[] table;
    private byte[] states;

    private int size;
    private int mask;
    private int resizeAt;

    private final float loadFactor;

    public PrimitiveLongHashSet(int expectedSize) {
        this(expectedSize, 0.65f);
    }

    public PrimitiveLongHashSet(int expectedSize, float loadFactor) {
        if (!(loadFactor > 0.0f && loadFactor < 1.0f)) {
            throw new IllegalArgumentException("loadFactor must be in (0,1)");
        }
        this.loadFactor = loadFactor;
        int cap = tableSizeFor((int) Math.ceil(expectedSize / loadFactor));
        init(cap);
    }

    public boolean add(long key) {
        if (size >= resizeAt) rehash(table.length << 1);

        int idx = findSlot(key);
        if (states[idx] == FULL) return false;

        table[idx] = key;
        states[idx] = FULL;
        size++;
        return true;
    }


    public boolean contains(long key) {
        int idx = findSlot(key);
        return states[idx] == FULL;
    }

    public int size() {
        return size;
    }

    private void init(int capacity) {
        table = new long[capacity];
        states = new byte[capacity];
        mask = capacity - 1;
        size = 0;
        resizeAt = (int) (capacity * loadFactor);
        if (resizeAt == 0) resizeAt = 1;
    }

    private int findSlot(long key) {
        int idx = mix64to32(key) & mask;

        while (true) {
            byte st = states[idx];
            if (st == EMPTY) return idx;
            if (table[idx] == key) return idx;
            idx = (idx + 1) & mask;
        }
    }

    private void rehash(int newCapacity) {
        long[] oldTable = table;
        byte[] oldStates = states;

        init(newCapacity);

        for (int i = 0; i < oldTable.length; i++) {
            if (oldStates[i] == FULL) {
                long k = oldTable[i];
                int idx = findSlot(k);
                table[idx] = k;
                states[idx] = FULL;
                size++;
            }
        }
    }

    // Hash mixing (MurmurHash3 finalizer style)
    private static int mix64to32(long z) {
        z ^= (z >>> 33);
        z *= 0xff51afd7ed558ccdL;
        z ^= (z >>> 33);
        z *= 0xc4ceb9fe1a85ec53L;
        z ^= (z >>> 33);
        return (int) z;
    }

    // Next power-of-two >= cap (min 2)
    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        int res = (n < 2) ? 2 : n + 1;
        if (res < 0) res = 1 << 30;
        return res;
    }
}
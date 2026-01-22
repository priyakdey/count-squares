package com.priyakdey;

/**
 * A minimal, allocation-free hash set for primitive {@code long} keys.
 * <p>
 * Designed for hot paths (e.g., geometric point lookups in CountSquares) where
 * {@code HashSet<Long>} would allocate/box. This implementation uses:
 * <ul>
 *   <li><b>Open addressing</b> with <b>linear probing</b></li>
 *   <li>A parallel {@code states[]} array to mark slots as {@code EMPTY} or {@code FULL}</li>
 *   <li>Power-of-two table size with {@code mask = capacity - 1} for fast indexing</li>
 * </ul>
 * <p>
 * Keys are considered present if an identical {@code long} value exists in the table.
 * This set does not support removal (no {@code DELETED} tombstone state).
 * <p>
 * <strong>Complexity:</strong> expected {@code O(1)} for {@link #add(long)} and
 * {@link #contains(long)} under a reasonable load factor; worst-case {@code O(n)}.
 *
 * @author Priyak Dey
 */
public class PrimitiveLongHashSet {
    private static final byte EMPTY = 0;
    private static final byte FULL = 1;

    private long[] table;
    private byte[] states;

    private int size;
    private int mask;
    private int resizeAt;

    private final float loadFactor;


    /**
     * Creates a set sized for the given expected number of distinct keys
     * using a default load factor of {@code 0.65}.
     *
     * @param expectedSize expected number of keys to store
     */
    public PrimitiveLongHashSet(int expectedSize) {
        this(expectedSize, 0.65f);
    }


    /**
     * Creates a set sized for the given expected number of distinct keys and load factor.
     * <p>
     * The internal capacity is rounded up to the next power of two such that
     * {@code capacity * loadFactor >= expectedSize}.
     *
     * @param expectedSize expected number of keys to store
     * @param loadFactor   load factor threshold for resizing; must be in {@code (0, 1)}
     * @throws IllegalArgumentException if {@code loadFactor} is not in {@code (0, 1)}
     */
    public PrimitiveLongHashSet(int expectedSize, float loadFactor) {
        if (!(loadFactor > 0.0f && loadFactor < 1.0f)) {
            throw new IllegalArgumentException("loadFactor must be in (0,1)");
        }
        this.loadFactor = loadFactor;
        int cap = tableSizeFor((int) Math.ceil(expectedSize / loadFactor));
        init(cap);
    }

    /**
     * Adds {@code key} to the set if not already present.
     * <p>
     * If the set reaches its resize threshold, the table is rehashed into a new
     * table of double the current capacity.
     *
     * @param key the key to add
     * @return {@code true} if the key was added, {@code false} if it was already present
     */
    public boolean add(long key) {
        if (size >= resizeAt) rehash(table.length << 1);

        int idx = findSlot(key);
        if (states[idx] == FULL) return false;

        table[idx] = key;
        states[idx] = FULL;
        size++;
        return true;
    }


    /**
     * Checks whether {@code key} is present in the set.
     *
     * @param key the key to test
     * @return {@code true} if present, {@code false} otherwise
     */
    public boolean contains(long key) {
        int idx = findSlot(key);
        return states[idx] == FULL;
    }

    /**
     * Returns the number of keys currently stored in the set.
     *
     * @return current size of the set
     */
    public int size() {
        return size;
    }

    /**
     * Initializes internal arrays and derived fields for the given capacity.
     * <p>
     * Capacity must be a power of two. Resizing is triggered when {@code size >= resizeAt},
     * where {@code resizeAt = floor(capacity * loadFactor)} (minimum 1).
     *
     * @param capacity new capacity (power of two)
     */
    private void init(int capacity) {
        table = new long[capacity];
        states = new byte[capacity];
        mask = capacity - 1;
        size = 0;
        resizeAt = (int) (capacity * loadFactor);
        if (resizeAt == 0) resizeAt = 1;
    }

    /**
     * Finds the slot index where {@code key} resides or should be inserted.
     * <p>
     * Uses {@link #mix64to32(long)} to compute an initial index and then probes linearly
     * until it finds either an {@code EMPTY} slot (not present) or a slot containing
     * the key (present).
     * <p>
     * Since removals are not supported, encountering {@code EMPTY} implies the key
     * is not in the set.
     *
     * @param key key to locate
     * @return index of the matching key slot or the first empty insertion slot
     */
    private int findSlot(long key) {
        int idx = mix64to32(key) & mask;

        while (true) {
            byte st = states[idx];
            if (st == EMPTY) return idx;
            if (table[idx] == key) return idx;
            idx = (idx + 1) & mask;
        }
    }

    /**
     * Rehashes all existing keys into a new table of {@code newCapacity}.
     * <p>
     * New capacity should be a power of two. This method re-inserts all {@code FULL}
     * keys from the old table into the new table.
     *
     * @param newCapacity new table capacity (power of two)
     */
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


    /**
     * Mixes a 64-bit key into a 32-bit hash suitable for indexing.
     * <p>
     * This is a MurmurHash3-style finalizer mix to spread entropy from high bits
     * into low bits, improving distribution when the table size is a power of two.
     *
     * @param z 64-bit input value
     * @return mixed 32-bit hash
     */
    private static int mix64to32(long z) {
        z ^= (z >>> 33);
        z *= 0xff51afd7ed558ccdL;
        z ^= (z >>> 33);
        z *= 0xc4ceb9fe1a85ec53L;
        z ^= (z >>> 33);
        return (int) z;
    }

    /**
     * Returns the next power-of-two capacity greater than or equal to {@code cap},
     * with a minimum of {@code 2}.
     * <p>
     * This ensures {@code mask = capacity - 1} works correctly for fast modulo.
     *
     * @param cap requested minimum capacity
     * @return power-of-two capacity {@code >= cap}
     */
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

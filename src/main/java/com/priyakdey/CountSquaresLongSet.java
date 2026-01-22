package com.priyakdey;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Priyak Dey
 */
public class CountSquaresLongSet {

    /**
     * Counts the number of (axis-aligned or rotated) squares that can be formed
     * from a set of 2D integer points.
     * <p>
     * <strong>Idea:</strong> Identical to {@code CountSquaresPointSet}, but replaces
     * object-based points with a packed {@code long} representation to reduce
     * allocation, improve cache locality, and speed up hash lookups.
     * <p>
     * Each point {@code (x, y)} is scaled by {@code 2} and packed into a single
     * {@code long} key. All computations are performed using integer arithmetic,
     * avoiding floating-point operations.
     * <p>
     * Every unordered pair of points is treated as a potential diagonal of a square.
     * If the two remaining rotated points exist in the set, a square is counted.
     * Each square is discovered twice (once per diagonal orientation), so the final
     * count is divided by {@code 2}.
     * <p>
     * <strong>Time Complexity:</strong> {@code O(n^2)} expected.<br>
     * <strong>Space Complexity:</strong> {@code O(n)}.
     *
     * @param points array of points where {@code points[i] = {x, y}}
     * @return number of distinct squares that can be formed
     */
    public int countSquares(int[][] points) {
        int length = points.length;

        Set<Long> set = HashSet.newHashSet(2 * length);
        for (int[] point : points) {
            long key = key(2L * point[0], 2L * point[1]);
            set.add(key);
        }

        long count = 0;

        for (int i = 0; i < points.length - 1; i++) {
            long x1 = points[i][0], y1 = points[i][1];
            for (int j = i + 1; j < points.length; j++) {
                long x2 = points[j][0], y2 = points[j][1];

                long x3 = ((x1 + x2) + (y1 - y2));
                long y3 = ((y1 + y2) + (x2 - x1));

                long x4 = ((x1 + x2) - (y1 - y2));
                long y4 = ((y1 + y2) - (x2 - x1));

                long p3 = key(x3, y3);
                long p4 = key(x4, y4);

                if (set.contains(p3) && set.contains(p4)) {
                    count++;
                }
            }
        }

        return (int) (count / 2);
    }


    /**
     * Packs two signed 32-bit integer coordinates into a single 64-bit key.
     * <p>
     * The high 32 bits store {@code x}, and the low 32 bits store {@code y}.
     * This encoding preserves equality and enables efficient hash-based lookups
     * without object allocation.
     *
     * @param x x-coordinate (scaled)
     * @param y y-coordinate (scaled)
     * @return packed {@code long} key representing the point
     */
    private long key(long x, long y) {
        return (x << 32) ^ (y & 0xFFFFFFFFL);
    }

}

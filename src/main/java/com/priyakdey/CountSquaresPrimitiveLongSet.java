package com.priyakdey;

/**
 * Counts the number of (axis-aligned or rotated) squares that can be formed
 * from a set of 2D integer points.
 * <p>
 * This implementation is the same diagonal-based {@code O(n^2)} approach as
 * {@link CountSquaresLongSet}, but uses a custom primitive hash set
 * ({@link PrimitiveLongHashSet}) to avoid {@code Long} boxing and reduce
 * GC pressure.
 * <p>
 * <strong>Core idea:</strong> Treat every unordered pair of points as a
 * potential diagonal. Given diagonal endpoints {@code P1(x1, y1)} and
 * {@code P2(x2, y2)}, the other two corners can be derived by rotating the
 * diagonal vector by ±90° about the midpoint.
 * <p>
 * To keep all computations in integer arithmetic, each input point is scaled
 * by {@code 2} (stored as {@code (2x, 2y)}). This eliminates division by 2 when
 * computing midpoint-based rotated coordinates.
 * <p>
 * Each square is discovered twice (once per diagonal orientation), so the final
 * result is divided by {@code 2}.
 * <p>
 * <strong>Time Complexity:</strong> {@code O(n^2)} expected (hash lookups).<br>
 * <strong>Space Complexity:</strong> {@code O(n)}.
 *
 * @author Priyak Dey
 */
public class CountSquaresPrimitiveLongSet {

    /**
     * Returns the number of distinct squares that can be formed using the
     * provided points.
     *
     * @param points array of points where {@code points[i] = {x, y}}
     * @return number of distinct squares
     */
    public int countSquares(int[][] points) {
        int length = points.length;

        PrimitiveLongHashSet set = new PrimitiveLongHashSet(length * 2);

        for (int[] p : points) {
            set.add(key(2L * p[0], 2L * p[1]));
        }

        long count = 0;

        for (int i = 0; i < length - 1; i++) {
            long x1 = points[i][0], y1 = points[i][1];
            for (int j = i + 1; j < length; j++) {
                long x2 = points[j][0], y2 = points[j][1];

                long x3 = (x1 + x2) + (y1 - y2);
                long y3 = (y1 + y2) + (x2 - x1);

                long x4 = (x1 + x2) - (y1 - y2);
                long y4 = (y1 + y2) - (x2 - x1);

                long p3 = key(x3, y3);
                long p4 = key(x4, y4);

                if (set.contains(p3) && set.contains(p4)) count++;
            }
        }

        return (int) (count / 2);
    }

    /**
     * Packs two signed 32-bit integer coordinates into a single 64-bit key.
     * <p>
     * The high 32 bits store {@code x}, and the low 32 bits store {@code y}.
     * Coordinates are expected to already be scaled
     * (typically {@code 2 × original}).
     *
     * @param x x-coordinate (scaled)
     * @param y y-coordinate (scaled)
     * @return packed {@code long} key representing the point
     */
    private static long key(long x, long y) {
        return (x << 32) ^ (y & 0xFFFFFFFFL);
    }
}

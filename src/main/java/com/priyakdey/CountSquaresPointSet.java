package com.priyakdey;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Priyak Dey
 */
public class CountSquaresPointSet {

    /**
     * Counts the number of (axis-aligned or rotated) squares that can be formed
     * from a set of 2D integer points.
     * <p>
     * <strong>Idea:</strong> Treat every unordered pair of points as a potential diagonal of a square.
     * If {@code P1(x1, y1)} and {@code P2(x2, y2)} are opposite corners (a diagonal), the other two
     * corners are obtained by rotating the diagonal vector by ±90° about the midpoint.
     * <p>
     * The remaining corners are:
     * <pre>{@code
     * P3 = ( (x1 + x2 + (y1 - y2)) / 2 , (y1 + y2 + (x2 - x1)) / 2 )
     * P4 = ( (x1 + x2 - (y1 - y2)) / 2 , (y1 + y2 - (x2 - x1)) / 2 )
     * }</pre>
     * <p>
     * To avoid division and floating-point arithmetic, this implementation stores all input points
     * scaled by 2 (i.e., {@code (2x, 2y)}). With that scaling, the computed {@code P3} and {@code P4}
     * coordinates become integers directly and can be looked up in a hash set.
     * <p>
     * Each square is found twice (once per diagonal orientation), so the final count is divided by 2.
     * <p>
     * <strong>Time Complexity:</strong> {@code O(n^2)} expected (hash lookups).<br>
     * <strong>Space Complexity:</strong> {@code O(n)} for the point set.
     *
     * @param points array of points where {@code points[i] = {x, y}}
     * @return number of distinct squares that can be formed
     */
    public int countSquares(int[][] points) {
        int length = points.length;
        Set<Point> set = HashSet.newHashSet(2 * length);

        for (int[] point : points) {
            // NOTE: we double the values to avoid divide by 2 and
            // avoid floating arithmetic
            set.add(new Point(2 * point[0], 2 * point[1]));
        }

        long count = 0;

        for (int i = 0; i < points.length - 1; i++) {
            int x1 = points[i][0], y1 = points[i][1];
            for (int j = i + 1; j < points.length; j++) {
                int x2 = points[j][0], y2 = points[j][1];

                int x3 = ((x1 + x2) + (y1 - y2));
                int y3 = ((y1 + y2) + (x2 - x1));
                Point p3 = new Point(x3, y3);

                if (!set.contains(p3)) continue;

                int x4 = ((x1 + x2) - (y1 - y2));
                int y4 = ((y1 + y2) - (x2 - x1));
                Point p4 = new Point(x4, y4);

                if (set.contains(p4)) count++;
            }
        }

        return (int) (count / 2);
    }

    /**
     * Immutable 2D point used for hash-based lookups.
     * <p>
     * Coordinates are stored in a scaled form (typically {@code 2 × original})
     * to allow midpoint and rotation computations to be expressed using integer
     * arithmetic only, avoiding division and floating-point precision issues.
     * <p>
     * Being a {@code record}, this type provides value-based equality and
     * hashing, making it suitable for use as a key in hash-based collections.
     *
     * @param x x-coordinate (scaled)
     * @param y y-coordinate (scaled)
     */
    private record Point(int x, int y) {
    }

}

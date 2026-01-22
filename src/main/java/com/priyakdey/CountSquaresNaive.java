package com.priyakdey;

import java.util.Arrays;

/**
 * @author Priyak Dey
 */
public class CountSquaresNaive {

    /**
     * Counts the number of squares that can be formed from a given set of
     * 2D integer points.
     * <p>
     * A square is defined by four distinct points with:
     * <ul>
     *   <li>Four equal non-zero side lengths</li>
     *   <li>Two equal diagonals</li>
     *   <li>Diagonal length squared equal to {@code 2 × side length squared}</li>
     * </ul>
     * <p>
     * <strong>Algorithm:</strong>
     * Enumerates all 4-point subsets and checks whether the six pairwise squared
     * distances satisfy square invariants.
     * <p>
     * <strong>Time Complexity:</strong> {@code O(n^4)}
     * <strong>Space Complexity:</strong> {@code O(1)} (constant auxiliary space)
     *
     * @param points array of points where {@code points[i] = {x, y}}
     * @return number of distinct squares that can be formed
     */
    public int countSquares(int[][] points) {
        long count = 0;
        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 1; k++) {
                    for (int l = k + 1; l < points.length; l++) {
                        if (isSquare(points[i], points[j], points[k],
                                points[l])) {
                            count++;
                        }
                    }
                }
            }
        }

        return (int) (count / 2);
    }

    /**
     * Determines whether four points form a valid square.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @param p4 fourth point
     * @return {@code true} if the points form a square, {@code false} otherwise
     */
    private boolean isSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        // NOTE: allocation can be made O(1), by sharing the buffer across
        // invocations. But that makes it thread-unsafe.
        // Making it thread-safe would increase the runtime - another classic
        // time-memory tradeoff.
        final long[] dist = new long[6];

        dist[0] = sqDist(p1, p2);
        dist[1] = sqDist(p1, p3);
        dist[2] = sqDist(p1, p4);
        dist[3] = sqDist(p2, p3);
        dist[4] = sqDist(p2, p4);
        dist[5] = sqDist(p3, p4);

        // NOTE: This adds to the runtime, but is neglible. In a bigger picture
        // this add O(1) complexity because of fixed size.
        Arrays.sort(dist);

        long side = dist[0];
        long diag = dist[4];
        if (side == 0) return false;

        boolean fourSidesEqual = dist[0] == dist[1]
                && dist[1] == dist[2] && dist[2] == dist[3];

        boolean twoDiagsEqual = dist[4] == dist[5];

        boolean correctRatio = diag == 2 * side;

        return fourSidesEqual && twoDiagsEqual && correctRatio;
    }

    /**
     * Computes the squared Euclidean distance between two points.
     * <p>
     * Squared distance is used to avoid floating-point arithmetic.
     *
     * @param p first point
     * @param q second point
     * @return squared distance between {@code p} and {@code q}
     */
    private long sqDist(int[] p, int[] q) {
        long dx = (long) p[0] - q[0];
        long dy = (long) p[1] - q[1];
        return dx * dx + dy * dy;
    }

}

package com.priyakdey;

import java.util.Arrays;

/**
 * @author Priyak Dey
 */
public class CountSquaresNaive {

    /**
     * Returns the number of squares possible to generate with the
     * given set of points as input.
     * <p>
     * This is a naive O(n^4) solution: check every 4-point subset
     * and test if it forms a square.
     *
     * @param points int[][]
     * @return count of squares.
     */
    public int countSquares(int[][] points) {
        int count = 0;
        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 1; k++) {
                    for (int l = k + 1; l < points.length; l++) {
                        if (isSquare(points[i], points[j], points[k], points[l])) {
                            count++;
                        }
                    }
                }
            }
        }

        return count;
    }

    private boolean isSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        final long[] dist = new long[6];

        dist[0] = sqDist(p1, p2);
        dist[1] = sqDist(p1, p3);
        dist[2] = sqDist(p1, p4);
        dist[3] = sqDist(p2, p3);
        dist[4] = sqDist(p2, p4);
        dist[5] = sqDist(p3, p4);

        // NOTE: this should be negligible, and O(1) since for any inputs
        // the size is always 6.
        Arrays.sort(dist);

        long side = dist[0];
        if (side == 0) return false;

        boolean fourSidesEqual = dist[0] == dist[1]
                && dist[1] == dist[2] && dist[2] == dist[3];

        boolean twoDiagsEqual = dist[4] == dist[5];

        boolean correctRatio = dist[4] == 2 * side;

        return fourSidesEqual && twoDiagsEqual && correctRatio;
    }

    private long sqDist(int[] p, int[] q) {
        long dx = (long) p[0] - q[0];
        long dy = (long) p[1] - q[1];
        return dx * dx + dy * dy;
    }

}

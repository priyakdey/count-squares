package com.priyakdey;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Priyak Dey
 */
public class CountSquaresPointSet {

    /**
     * Returns the number of squares possible to generate with the
     * given set of points as input.
     * <p>
     * This is a faster algorithm than naive solution {@link CountSquaresNaive}
     * and runs in O(n^2) solution. We use a little bit of geometry knowledge
     * here and work with diagonals. Using a {@link java.util.HashSet} to
     * speed up the lookups.
     * <p>
     * If {@code P1(x1, y1) and P2(x2, y2)} are the two diagonal points, the
     * other two points on the second diagonal of the square are defined as
     * below:
     * <pre>
     * {@code P3 = ((x1 + x2 + (y1 - y2)) / 2 , (y1 + y2 + (x2 - x1)) / 2)}
     * {@code P4 = ((x1 + x2 - (y1 - y2)) / 2 , (y1 + y2 - (x2 - x1)) / 2)}
     * </pre>
     *
     * @param points int[][]
     * @return count of squares.
     */
    public int countSquares(int[][] points) {
        int length = points.length;
        Set<Point> set = HashSet.newHashSet(2 * length);

        for (int[] point : points) {
            // NOTE: we double the values to avoid divide by 2 and
            // avoid floating arithmetic
            set.add(new Point(2 * point[0], 2 * point[1]));
        }

        int count = 0;

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

        return count / 2;
    }

    private record Point(int x, int y) {
    }

}

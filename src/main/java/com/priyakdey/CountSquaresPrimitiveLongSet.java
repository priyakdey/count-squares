package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class CountSquaresPrimitiveLongSet {

    public int countSquares(int[][] points) {
        int length = points.length;

        PrimitiveLongHashSet set = new PrimitiveLongHashSet(length * 2);

        for (int[] p : points) {
            set.add(key(2L * p[0], 2L * p[1]));
        }

        int count = 0;

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

        return count / 2;
    }

    private static long key(long x, long y) {
        return (x << 32) ^ (y & 0xFFFFFFFFL);
    }
}

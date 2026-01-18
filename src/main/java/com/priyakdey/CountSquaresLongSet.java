package com.priyakdey;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Priyak Dey
 */
public class CountSquaresLongSet {

    public int countSquares(int[][] points) {
        int length = points.length;

        Set<Long> set = HashSet.newHashSet(2 * length);
        for (int[] point : points) {
            long key = key(2L * point[0], 2L * point[1]);
            set.add(key);
        }

        int count = 0;

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

        return count / 2;
    }

    /**
     * Pack two integers(i32) into a single long(i64) as a key
     */
    private long key(long x, long y) {
        return (x << 32) ^ (y & 0xFFFFFFFFL);
    }

}

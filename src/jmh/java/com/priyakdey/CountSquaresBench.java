package com.priyakdey;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
@Threads(1)
public class CountSquaresBench {

    @Param({"24", "32", "40", "48", "56", "64", "128", "256", "512", "1024",
            "2048", "4096", "8192", "16384"})
    public int n;

    @Param({"RANDOM", "GRID"})
    public String dist;

    private int[][] points;

    private final CountSquaresNaive naive = new CountSquaresNaive();
    private final CountSquaresPointSet pointSet = new CountSquaresPointSet();
    private final CountSquaresLongSet longSet = new CountSquaresLongSet();
    private final CountSquaresPrimitiveLongSet primitiveLongSet =
            new CountSquaresPrimitiveLongSet();

    @Setup(Level.Trial)
    public void setup() {
        points = "GRID".equals(dist) ? genGrid(n) :
                genRandomUnique(n, 100, 42);
    }

    // @Benchmark
    public int naive() {
        return naive.countSquares(points);
    }

    @Benchmark
    public int pointSet() {
        return pointSet.countSquares(points);
    }

    @Benchmark
    public int longSet() {
        return longSet.countSquares(points);
    }

    @Benchmark
    public int primitiveLongSet() {
        return primitiveLongSet.countSquares(points);
    }

    static int[][] genGrid(int n) {
        int side = (int) Math.ceil(Math.sqrt(n));
        int[][] pts = new int[n][2];
        int idx = 0;
        for (int i = 0; i < side && idx < n; i++) {
            for (int j = 0; j < side && idx < n; j++) {
                pts[idx][0] = i;
                pts[idx][1] = j;
                idx++;
            }
        }
        return pts;
    }

    static int[][] genRandomUnique(int n, int bound, long seed) {
        int range = 2 * bound + 1;
        int max = range * range;
        if (n > max) {
            throw new IllegalArgumentException("n too large for unique points");
        }

        Random r = new Random(seed);

        boolean[] used = new boolean[max];
        int[][] pts = new int[n][2];
        int idx = 0;
        while (idx < n) {
            int x = r.nextInt(range) - bound;
            int y = r.nextInt(range) - bound;
            int flat = (x + bound) * range + (y + bound);
            if (used[flat]) continue;
            used[flat] = true;
            pts[idx][0] = x;
            pts[idx][1] = y;
            idx++;
        }
        return pts;
    }
}

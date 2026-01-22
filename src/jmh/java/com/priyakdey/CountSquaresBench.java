package com.priyakdey;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Priyak Dey
 */
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

    /**
     * Generates a deterministic grid of points laid out in row-major order.
     * <p>
     * Points are placed on an integer lattice starting from {@code (0,0)} and
     * expanding to a square grid of side {@code ceil(sqrt(n))}, truncated after
     * {@code n} points.
     * <p>
     * <strong>Why GRID?</strong>
     * <ul>
     *   <li>Maximizes geometric structure and symmetry</li>
     *   <li>Produces a high density of squares and overlapping diagonals</li>
     *   <li>Acts as a worst-case dataset for square-counting algorithms</li>
     * </ul>
     * This dataset stresses:
     * <ul>
     *   <li>Algorithmic constant factors</li>
     *   <li>Hash-set pressure due to frequent positive lookups</li>
     *   <li>Overcounting paths (many valid diagonals)</li>
     * </ul>
     *
     * @param n number of points to generate
     * @return {@code n} points arranged on an integer grid
     */
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

    /**
     * Generates {@code n} unique random points within a bounded square region.
     * <p>
     * Points are sampled uniformly from {@code [-bound, bound] × [-bound, bound]},
     * with uniqueness enforced via a flat boolean occupancy array.
     * <p>
     * <strong>Why RANDOM?</strong>
     * <ul>
     *   <li>Minimizes geometric structure and symmetry</li>
     *   <li>Produces very few squares in practice</li>
     *   <li>Models real-world or adversarially unstructured input</li>
     * </ul>
     * This dataset stresses:
     * <ul>
     *   <li>Negative hash lookups (most diagonal checks fail)</li>
     *   <li>Branch prediction behavior</li>
     *   <li>Baseline algorithmic overhead independent of geometry</li>
     * </ul>
     * <p>
     * Using a fixed seed ensures reproducibility across benchmark runs.
     *
     * @param n     number of points to generate
     * @param bound coordinate bound; points lie in {@code [-bound, bound]}
     * @param seed  random seed for reproducibility
     * @return {@code n} unique random points within the bounded region
     * @throws IllegalArgumentException if {@code n} exceeds the number of unique
     *                                  points possible in the region
     */
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

/*
Solutions for Code Jam 2017.
Copyright 2017-2018 Salvatore ISAJA. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED THE AUTHOR ``AS IS'' AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package codejam2017.finals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Teleporters problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p5
 * Large dataset version.
 *
 * Looking carefully at how "bubbles" centered on teleporters grow in my
 * solution for small datasets (see TeleportersSmall), I've found a way
 * to cope with the large dataset without a full-blown breadth first search.
 * 
 * After each teleportation, the minimum and maximum radii of a bubble can,
 * respectively, decrease and increase. Thus, to find the size of bubbles
 * (one for teleporter) of the next iteration, we can just apply the formula of
 * the distance between the bubble of each teleporter from the previous
 * iteration and every other teleporters.
 * This is O(n^2) for each teleportation, with n teleporters.
 * 
 * Still, if teleporters are very close to each other, bubbles grow very slowly,
 * and filling the large space of the large dataset can take a very high
 * number of iterations.
 * 
 * Fortunately, after the sizes of bubbles start to be relatively large
 * compared with the distance between teleporters, the ratio of growth
 * will converge to a pair of fixed values for each teleporters.
 * My interpretation is that the distance of one teleporter dominates the
 * teleportation from another teleporter: we basically start to be bounced
 * between the two. Thus, we can use this pair of fixed step sizes to know
 * how many steps we would need to expand each bubble to touch Care-a-lot.
 * Either that, or we will reach Care-a-lot before the ratio of growth
 * converges.
 * 
 * With this algorithm, a pool of 4 threads is able to process the large
 * dataset in a little more than 2 minutes on my i5-3570K.
 * 
 * @author Salvo Isaja
 */
public class TeleportersLarge {

    /** A point with integer coordinates in taxicab geometry. */
    private static class Point {
        public final long x;
        public final long y;
        public final long z;

        public Point(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public long distance(Point other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
        }

        @Override
        public String toString() {
            return "Point{" + x + ", " + y + ", " + z + '}';
        }
    }

    private static class Test implements Callable<String> {
        private final int testIndex;
        private final Point thundera;
        private final Point careALot;
        private final List<Point> teleporterPoints;

        public Test(int testIndex, Point thundera, Point careALot, List<Point> teleporterPoints) {
            this.testIndex = testIndex;
            this.thundera = thundera;
            this.careALot = careALot;
            this.teleporterPoints = teleporterPoints;
        }

        @Override
        public String call() {
            debugPrint("Case #" + testIndex + ", " + teleporterPoints.size() + " teleporters... ");
            Expander e = new Expander(this);
            long teleportations = e.fillSpace();
            return "Case #" + testIndex + ": " + (teleportations > 0 ? teleportations : "IMPOSSIBLE");
        }
    }

    private static class Expander {
        private final Test test;
        private final long[][] teleporterDistances;
        private final long[] careALotDistances;
        private long[] maxRadii;
        private long[] prevMaxRadii;
        private long[] minRadii;
        private long[] prevMinRadii;
        private final long[][] minSteps;
        private final long[][] maxSteps;
        private int ringPos;

        Expander(Test test) {
            this.test = test;
            int s = test.teleporterPoints.size();
            teleporterDistances = new long[s][s];
            careALotDistances = new long[s];
            maxRadii = new long[s];
            minRadii = new long[s];
            prevMaxRadii = new long[s];
            prevMinRadii = new long[s];
            minSteps = new long[4][s];
            maxSteps = new long[4][s];
        }

        private long extrapolateRadii() {
            long deltaTeleportations = Long.MAX_VALUE;
            for (int i = 0; i < teleporterDistances.length; i++) {
                long contractingSteps = 0;
                if (minRadii[i] > careALotDistances[i]) {
                    long lastStep = minSteps[ringPos][i];
                    long secondLastStep = minSteps[(ringPos - 1) & 3][i];
                    long doubleStep = lastStep + secondLastStep;
                    long doubleStepsLeft = (minRadii[i] - careALotDistances[i] + doubleStep - 1) / doubleStep;
                    contractingSteps = doubleStepsLeft * 2;
                    assert minRadii[i] - doubleStepsLeft * doubleStep <= careALotDistances[i];
                    if (minRadii[i] - doubleStepsLeft * doubleStep + lastStep <= careALotDistances[i]) contractingSteps--;
                }
                long expandingSteps = 0;
                if (careALotDistances[i] > maxRadii[i]) {
                    long lastStep = maxSteps[ringPos][i];
                    long secondLastStep = maxSteps[(ringPos - 1) & 3][i];
                    long doubleStep = lastStep + secondLastStep;
                    long doubleStepsLeft = (careALotDistances[i] - maxRadii[i] + doubleStep - 1) / doubleStep;
                    assert maxRadii[i] + doubleStepsLeft * doubleStep >= careALotDistances[i];
                    expandingSteps = doubleStepsLeft * 2;
                    if (maxRadii[i] + doubleStepsLeft * doubleStep - lastStep >= careALotDistances[i]) expandingSteps--;
                }
                long steps = Math.max(contractingSteps, expandingSteps);
                if (steps < deltaTeleportations) deltaTeleportations = steps;
            }
            return deltaTeleportations;
        }
        
        private long fillSpace() {
            for (int i = 0; i < teleporterDistances.length; i++) {
                Point p = test.teleporterPoints.get(i);
                careALotDistances[i] = p.distance(test.careALot);
                for (int j = 0; j < teleporterDistances.length; j++) {
                    Point q = test.teleporterPoints.get(j);
                    teleporterDistances[i][j] = p.distance(q);
                }
            }
            for (int i = 0; i < test.teleporterPoints.size(); i++) {
                prevMinRadii[i] = test.thundera.distance(test.teleporterPoints.get(i));
                prevMaxRadii[i] = test.thundera.distance(test.teleporterPoints.get(i));
                if (prevMaxRadii[i] == careALotDistances[i]) {
                    debugPrint("Reachable after 1 teleportation.\n");
                    return 1;
                }
            }
            if (test.teleporterPoints.size() == 1) return Integer.MIN_VALUE;
            for (int teleportation = 2; teleportation < 4000000; teleportation++) {
                boolean converged = true;
                // Give the JVM a pattern to recognize it can do array bound
                // check elimination, especially on the inner loop.
                for (int i = 0; i < teleporterDistances.length; i++) {
                    long max = 0;
                    long min = Long.MAX_VALUE;
                    long[] ithTeleporterDistances = teleporterDistances[i];
                    for (int j = 0; j < ithTeleporterDistances.length; j++) {
                        if (j == i) continue;
                        long prevMinRadius = prevMinRadii[j];
                        long prevMaxRadius = prevMaxRadii[j];
                        long cd = teleporterDistances[i][j];
                        long minRadius = cd > prevMaxRadius ? cd - prevMaxRadius
                                : cd < prevMinRadius ? prevMinRadius - cd
                                : 0;
                        if (minRadius < min) min = minRadius;
                        long maxRadius = cd + prevMaxRadius;
                        if (maxRadius > max) max = maxRadius;
                    }
                    if (min <= careALotDistances[i] && max >= careALotDistances[i]) {
                        debugPrint("Reachable after " + teleportation + " teleportations.\n");
                        return teleportation;
                    }
                    minRadii[i] = min;
                    maxRadii[i] = max;
                    minSteps[ringPos][i] = prevMinRadii[i] - minRadii[i];
                    maxSteps[ringPos][i] = maxRadii[i] - prevMaxRadii[i];
                    if (minSteps[ringPos][i] != minSteps[(ringPos - 2) & 3][i]
                            || minSteps[(ringPos - 1) & 3][i] != minSteps[(ringPos - 3) & 3][i]
                            || maxSteps[ringPos][i] != maxSteps[(ringPos - 2) & 3][i]
                            || maxSteps[(ringPos - 1) & 3][i] != maxSteps[(ringPos - 3) & 3][i]) converged = false;
                    //debugPrint(minSteps[pos][i] + "\t");
                }
                //debugPrint("\n");
                if (converged) {
                    debugPrint("Converged after " + teleportation + " teleportations.\n");
                    return teleportation + extrapolateRadii();
                }
                ringPos = (ringPos + 1) & 3;
                long[] temp = prevMaxRadii;
                prevMaxRadii = maxRadii;
                maxRadii = temp;
                temp = prevMinRadii;
                prevMinRadii = minRadii;
                minRadii = temp;
            }
            return 0;
        }
    }

    private static final boolean DEBUG = false;

    private static void debugPrint(String s) {
        if (true) System.err.print(s);
    }

    private void scanTests(InputStream is) throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newFixedThreadPool(DEBUG ? 1 : Runtime.getRuntime().availableProcessors());
        List<Test> tests = new ArrayList<>();
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                int teleporterCount = scanner.nextInt();
                Point thundera = new Point(scanner.nextLong(), scanner.nextLong(), scanner.nextLong());
                Point careALot = new Point(scanner.nextLong(), scanner.nextLong(), scanner.nextLong());
                List<Point> teleporterPoints = new ArrayList<>(teleporterCount);
                for (int i = 0; i < teleporterCount; i++) {
                    teleporterPoints.add(new Point(scanner.nextLong(), scanner.nextLong(), scanner.nextLong()));
                }
                tests.add(new Test(t, thundera, careALot, teleporterPoints));
            }
        }
        List<Future<String>> results = threadPool.invokeAll(tests);
        for (Future<String> f : results) {
            System.out.println(f.get());
        }
        threadPool.shutdown();
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {
        long t = System.nanoTime();
        TeleportersLarge sm = new TeleportersLarge();
        sm.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/F-large-practice.in") : System.in);
        System.err.println("TeleportersLarge done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

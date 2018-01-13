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
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Omnicircumnavigation problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p3
 *
 * In order to form an omnicircumnavigation, the given points must cross all
 * meridians and at least once the equator, wherever the poles are set.
 * Thus, we must find out whether an equator exists such that some points
 * lie above its plane and some below its plane. If no point lie on a side
 * of any equator, we can be almost sure of the negative response.
 * A special case is when three or more points lie exactly on the equator,
 * because they could form an omnicircumnavigation themselves. In this case
 * we repeat the check, looking for a meridian such that some points lie
 * on its left and some on its right. If no point lie on a side of a meridian,
 * the response is negative.
 * 
 * This algorithm uses a brute force search of all possible equators by scanning
 * all planes formed by each pair of points. Then, for each plane, we check
 * if every other point is "above" or "below", where "above" is the direction
 * of the normal (the cross product of the pair of points).
 * Due to the limits, and being cross products and dot products the only
 * operations involved, we can do everything with long integer arithmetic.
 * 
 * The algorithm is potentially O(n^3), but it usually runs much faster
 * because it stops as soon as it finds an equator with points on both sides.
 * The first version scanned pair of points sequentially, and processing the
 * large dataset took about 7 minutes on my i5-3570K without parallelization
 * and about 2 minutes when using a pool of 4 threads (as many as CPU cores).
 * To help further speed-up the search, it is now picking as the first point
 * of the next pair the "farthest" point found in the previous iteration,
 * since closer pairs share close neighbors likely processed over and over.
 * With this change, the large dataset is completed in less than 30 seconds.
 * 
 * @author Salvo Isaja
 */
public class Omnicircumnavigation {

    private static class Point {
        public final long x;
        public final long y;
        public final long z;

        public Point(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point crossProduct(Point other) {
            return new Point(
                    this.y * other.z - this.z * other.y,
                    this.z * other.x - this.x * other.z,
                    this.x * other.y - this.y * other.x);
        }

        public long dotProduct(Point other) {
            return this.x * other.x + this.y * other.y + this.z * other.z;
        }
        
        public boolean isEquivalent(Point other) {
            if (other == this) return true;
            if (Long.signum(this.x) != Long.signum(other.x)
                    || Long.signum(this.y) != Long.signum(other.y)
                    || Long.signum(this.z) != Long.signum(other.z)) return false;
            Point cp = this.crossProduct(other);
            return cp.x == 0 && cp.y == 0 && cp.z == 0;
        }

        @Override
        public String toString() {
            return "Point{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
        }
    }

    private static class Test implements Callable<String> {
        private final int testIndex;
        private final List<Point> points;

        private static boolean scanPlanes(List<Point> points) {
            Set<Point> unvisitedPoints = new HashSet<>(points);
            Point mostDistantPoint = null;
            // Loop across all pairs of points, each defining an equator to test.
            // Here there was the usual nested loops for i from 0 to n-1 and
            // for j from i+1 to n.
            while (!unvisitedPoints.isEmpty()) {
                Point pi = mostDistantPoint != null && unvisitedPoints.contains(mostDistantPoint) ? mostDistantPoint : unvisitedPoints.iterator().next();
                long maxDistance = 0;
                mostDistantPoint = null;
                for (Point pj : unvisitedPoints) {
                    if (pj == pi) continue;
                    Point normal = pi.crossProduct(pj);
                    if (normal.x == 0 && normal.y == 0 && normal.z == 0) continue;
                    //debugPrint("Plane for " + pi + " and " + pj + " with normal " + normal);
                    // Check whether there are points above or below the equator
                    List<Point> pointsOnPlane = new ArrayList<>();
                    boolean hasPointsAbove = false;
                    boolean hasPointsBelow = false;
                    for (Point other : points) {
                        if (hasPointsAbove && hasPointsBelow) break;
                        if (other == pi || other == pj) continue;
                        long d = normal.dotProduct(other);
                        if (d < 0) hasPointsBelow = true;
                        if (d > 0) hasPointsAbove = true;
                        if (d == 0) pointsOnPlane.add(other);
                        d = Math.abs(d);
                        if (d > maxDistance) {
                            maxDistance = d;
                            mostDistantPoint = other;
                        }
                    }
                    if (hasPointsAbove && hasPointsBelow) continue;
                    if (pointsOnPlane.isEmpty()) return false;
                    // Here we have at least three points on a great circle
                    // and have basically the same problem in 2D
                    pointsOnPlane.add(pi);
                    pointsOnPlane.add(pj);
                    for (Point pk : pointsOnPlane) {
                        //debugPrint("On plane: " + pk);
                        boolean hasPointsOnLeft = false;
                        boolean hasPointsOnRight = false;
                        for (Point pl : pointsOnPlane) {
                            // Use the right hand rule to find out if pl is on
                            // the left or on the right of pk.
                            // We know pkl will be collinear with the normal
                            // of the equator, and we can check its sign.
                            Point pkl = pk.crossProduct(pl);
                            if (pkl.x == 0 && pkl.y == 0 && pkl.z == 0) {
                                if (pl.dotProduct(pk) < 0) return true; // antipodal
                                continue;
                            }
                            if (Long.signum(normal.x) == Long.signum(pkl.x)
                                    && Long.signum(normal.y) == Long.signum(pkl.y)
                                    && Long.signum(normal.z) == Long.signum(pkl.z)) {
                                hasPointsOnLeft = true;
                            } else {
                                hasPointsOnRight = true;
                            }
                        }
                        if (!hasPointsOnLeft || !hasPointsOnRight) return false;
                    }
                }
                unvisitedPoints.remove(pi);
            }
            //debugPrint("Points: " + points.size() + " count: " + count);
            return true;
        }

        public Test(int testIndex, List<Point> points) {
            this.testIndex = testIndex;
            this.points = points;
        }

        @Override
        public String call() {
            System.err.println("Case #" + testIndex + ", " + points.size() + " points");
            boolean b = scanPlanes(points);
            return "Case #" + testIndex + ": " + (b ? "YES" : "NO");
        }
    }

    private static final boolean DEBUG = false;

    private static void debugPrint(String s) {
        if (DEBUG) System.out.println(s);
    }

    private static void scanTests(InputStream is) throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newFixedThreadPool(DEBUG ? 1 : Runtime.getRuntime().availableProcessors());
        List<Test> tests = new ArrayList<>();
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                int pointCount = scanner.nextInt();
                List<Point> points = new ArrayList<>(pointCount);
                for (int i = 0; i < pointCount; i++) {
                    long x = scanner.nextLong();
                    long y = scanner.nextLong();
                    long z = scanner.nextLong();
                    Point np = new Point(x, y, z);
                    // As a micro-optimization, remove equivalent points before processing
                    for (Point p : points) {
                        if (p.isEquivalent(np)) {
                            np = null;
                            break;
                        }
                    }
                    if (np != null) points.add(np);
                }
                tests.add(new Test(t, points));
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
        scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/D-large-practice.in") : System.in);
        System.err.println("Omnicircumnavigation done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Teleporters problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p5
 * Small dataset version.
 * 
 * Each teleportation can carry ourselves to "spheres", which are actually
 * diamond-shaped in taxicab geometry, centered on teleporters.
 * Due to the shape of these spheres, the distance of any given point in space
 * to any point of a sphere ranges from the distance between the point and
 * the center *minus* the radius and the distance between the point and the
 * center *plus* the radius.
 * 
 * Thus, the set of possible spheres reachable after a teleportation form
 * a "bubble" of spheres with radii between a specified interval.
 * The 3D problem actually turns to a 1D problem, considering only distances
 * and radii.
 * 
 * Starting from Thundera, that we consider as a bubble with minimus and maximum
 * radii of zero, we can use a breadth first search, using each teleporter as
 * neighbor, expanding the set of visited bubbles. We stop as soon as we found
 * a bubble touching Care-a-lot.
 * We care a lot about already visited spheres, however, so that we don't
 * iterate over these points of space over and over. For each teleporter,
 * a map (inspired from an address space allocator) keeps track of radii already
 * visited for each count of teleportations. Conceptually, this is the closed
 * set of our breath first search. For efficiency, the algorithm actually
 * keeps track of "unvisited bubbles", much like an allocator keeps track
 * of free/unallocated space.
 * 
 * This is effective for the small dataset, but fails dramatically for the
 * large dataset, especially if teleporters are very close to each other
 * and bubbles grow very slowly to fill the space.
 * Looking at how bubbles grow, however, allowed me to find a strategy
 * for the large dataset (see TeleportersLarge).
 * 
 * @author Salvo Isaja
 */
public class TeleportersSmall {

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

    /**
     * A set of spheres with the same center and with radii within a half-closed interval.
     * The distance from a point in space to the bubble is:
     * - if the point is inside the bubble, the distance from the innermost sphere;
     * - if the point if outside the bubble, the distance from the outermost sphere;
     * - if the point is between the innermost and the outermost sphere,
     *   it touches the bubble, thus its distance is zero.
     * A new bubble can be constructed by specifying its center and the bubble
     * we were in the previous step, before teleportation.
     */
    private static class Bubble {
        public final Point center;
        public final long minRadius; // minimum radius of the set of spheres
        public final long supRadius; // one past the maximum radius of the set of spheres
        public final long teleportations;
        public final Bubble prevBubble;

        /** Creates the next bubble in the path. */
        public static Bubble fromPrevBubble(Point center, Bubble prevBubble) {
            long cd = prevBubble.center.distance(center);
            long minRadius = cd >= prevBubble.supRadius ? cd - prevBubble.supRadius + 1
                    : cd < prevBubble.minRadius ? prevBubble.minRadius - cd
                    : 0;
            long supRadius = cd + prevBubble.supRadius;
            assert supRadius > minRadius;
            return new Bubble(center, minRadius, supRadius, prevBubble.teleportations + 1, prevBubble);
        }

        /** Creates a new bubble by cloning the specified bubble but with restricted radii. */
        public static Bubble fromLargerBubble(Bubble bubble, long minRadius, long supRadius) {
            return new Bubble(bubble.center, minRadius, supRadius, bubble.teleportations, bubble.prevBubble);
        }

        /** The I-know-what-I'm-doing constructor. */
        public Bubble(Point center, long minRadius, long supRadius, long teleportations, Bubble prevBubble) {
            this.center = center;
            this.minRadius = minRadius;
            this.supRadius = supRadius;
            this.teleportations = teleportations;
            this.prevBubble = prevBubble;
        }

        public boolean touches(Point p) {
            long d = center.distance(p);
            return d >= minRadius && d < supRadius;
        }

        public long distance(Point p) {
            long d = center.distance(p);
            if (d >= supRadius) return d - supRadius + 1;
            if (d < minRadius) return minRadius - d;
            return 0;
        }

        @Override
        public String toString() {
            return "Bubble{" + center.x + ", " + center.y + ", " + center.z + ", r[" + minRadius + "," + supRadius + ")}";
        }
    }

    private static class Teleporter {
        final Point point;
        final TreeMap<Long, Bubble> bubbles; // Non-overlapping bubbles centered on the teleporter, keyed by minRadius.
                                             // Not needed for bubble "allocation", but useful for debugging/studying
        final TreeMap<Long, Bubble> emptyBubbles; // Same as above, but for regions of space not yet visited

        public Teleporter(Point point) {
            this.point = point;
            bubbles = new TreeMap<>();
            emptyBubbles = new TreeMap<>();
            emptyBubbles.put(0L, new Bubble(point, 0, Long.MAX_VALUE, 0, null));
        }
    }

    private static class Test implements Callable<String> {
        private final int testIndex;
        private final Point thundera;
        private final Point careALot;
        private final Collection<Point> teleporterPoints;

        public Test(int testIndex, Point thundera, Point careALot, Collection<Point> teleporterPoints) {
            this.testIndex = testIndex;
            this.thundera = thundera;
            this.careALot = careALot;
            this.teleporterPoints = teleporterPoints;
        }

        /**
         * Do a breath first search for a bubble touching Care-a-lot.
         * Our closed set is conceptually given by regions of spaces already
         * represented by the bubbles map of each teleporter.
         * The inner loop is basically an allocator, very similar to what
         * would be done to reserve address space segments with mmap.
         */
        private Bubble fillSpace() {
            // We create the collection of Teleporters here instead of as a class
            // variable to minimize the amount of garbage held by the thread pool.
            Collection<Teleporter> teleporters = new ArrayList<>(teleporterPoints.size());
            for (Point p : teleporterPoints) teleporters.add(new Teleporter(p));
            Queue<Bubble> openSet = new LinkedList<>();
            Bubble start = new Bubble(thundera, 0, 1, 0, null);
            openSet.add(start);
            while (!openSet.isEmpty()) {
                Bubble current = openSet.poll();
                if (current.touches(careALot)) {
                    return current;
                }
                for (Teleporter t : teleporters) {
                    Bubble next = Bubble.fromPrevBubble(t.point, current);
                    if (next.touches(careALot)) {
                        return next;
                    }
                    long minRadius = next.minRadius;
                    Map.Entry<Long, Bubble> emptyEntry = t.emptyBubbles.floorEntry(minRadius);
                    while (minRadius <= next.supRadius) {
                        if (emptyEntry != null && minRadius <= emptyEntry.getValue().supRadius) {
                            Bubble b = t.emptyBubbles.remove(emptyEntry.getKey());
                            long supRadius = Math.min(next.supRadius, b.supRadius);
                            if (b.minRadius < minRadius) {
                                t.emptyBubbles.put(b.minRadius, Bubble.fromLargerBubble(b, b.minRadius, minRadius - b.minRadius));
                            }
                            if (b.supRadius > supRadius) {
                                t.emptyBubbles.put(supRadius, Bubble.fromLargerBubble(b, supRadius, b.supRadius - supRadius));
                            }
                            if (supRadius > minRadius) {
                                b = Bubble.fromLargerBubble(next, minRadius, supRadius);
                                t.bubbles.put(minRadius, b);
                                openSet.add(b);
                            }
                        }
                        emptyEntry = t.emptyBubbles.higherEntry(minRadius);
                        if (emptyEntry == null) break;
                        minRadius = emptyEntry.getValue().minRadius;
                    }
                }
            }
            return null;
        }

        @Override
        public String call() {
            System.err.println("Case #" + testIndex + ", " + teleporterPoints.size() + " teleporters");
            Bubble lastBubble = fillSpace();
            return "Case #" + testIndex + ": " + (lastBubble != null ? lastBubble.teleportations : "IMPOSSIBLE");
        }
    }
    
    private static final boolean DEBUG = false;

    private static void debugPrint(String s) {
        if (DEBUG) System.out.println(s);
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
        TeleportersSmall sm = new TeleportersSmall();
        sm.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/F-small-practice.in") : System.in);
        System.err.println("TeleportersSmall done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

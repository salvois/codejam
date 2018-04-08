/*
Solutions for Code Jam 2018.
Copyright 2018 Salvatore ISAJA. All rights reserved.

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
package codejam2018.qualification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Cubic UFO problem from Google Code Jam Qualification 2018.
 * https://codejam.withgoogle.com/2018/challenges/00000000000000cb/dashboard/00000000000079cc
 * 
 * The algorithm uses a binary search for rotation angles along the z or x
 * axes in order to find the target area.
 * Rotating along z is sufficient for the small dataset (rectangular shadow),
 * while a further rotation along x is required for the large (hexagonal shadow).
 * The area is evaluated as specified in the problem statement, by projecting
 * the cube vertices on the xz plane and then computing the area of the
 * resulting convex hull. Hopefully this should match what the judge expects.
 * While I came to a constant-time solution, at least for the small dataset,
 * I was unsure if the outputs computed via inverse trigonometric functions
 * would satisfy the tolerances expected by the judge.
 * Special thanks to the cubic candle lying in my kitchen!
 * 
 * @author Salvo Isaja
 */
public class CubicUfo {

    private static class Point2d {
        public final double x;
        public final double y;

        public Point2d(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Point2d neg() {
            return new Point2d(-x, -y);
        }

        public Point2d add(Point2d p) {
            return new Point2d(x + p.x, y + p.y);
        }

        public Point2d sub(Point2d p) {
            return new Point2d(x - p.x, y - p.y);
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }
    
    private static class Point3d {
        public final double x;
        public final double y;
        public final double z;

        public Point3d(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point2d projectOnY() {
            return new Point2d(x, z);
        }
        
        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    private static final boolean DEBUG = true;
    
    /**
     * Returns the orientation of the specified ordered triple of points.
     * Courtesy https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
     * @return 0 if points are collinear, >0 for clockwise orientation, <0 for counterclockwise orientation.
     */
    private static double orientation(Point2d p, Point2d q, Point2d r) {
        return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
    }
    
    /**
     * Returns the convex hull for the specified points.
     * Courtesy https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
     */
    private static List<Point2d> createConvexHull(Point2d[] points) {
        List<Point2d> hull = new ArrayList<>();
        // Find the leftmost point
        int leftmost = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i].x < points[leftmost].x) {
                leftmost = i;
            }
        }
        int p = leftmost;
        do {
            hull.add(points[p]);
            int q = p + 1;
            if (q >= points.length) q -= points.length;
            for (int i = 0; i < points.length; i++) {
                double o = orientation(points[p], points[i], points[q]);
                if (o < 0) {
                    q = i;
                }
            }
            p = q;
        } while (p != leftmost);
        return hull;
    }

    /**
     * Project the three centers to the xz plane, create the convex hull of
     * the resulting 8 vertices and compute its area with the Shoelace formula.
     */
    private static double computeArea(Point3d[] centers3d) {
        assert centers3d.length == 3;
        Point2d[] centers = new Point2d[] {
            centers3d[0].projectOnY(),
            centers3d[1].projectOnY(),
            centers3d[2].projectOnY()
        };
        Point2d[] vertices = new Point2d[] {
            centers[0].add(centers[1]).add(centers[2]),
            centers[0].add(centers[1]).sub(centers[2]),
            centers[0].sub(centers[1]).add(centers[2]),
            centers[0].sub(centers[1]).sub(centers[2]),
            centers[0].neg().add(centers[1]).add(centers[2]),
            centers[0].neg().add(centers[1]).sub(centers[2]),
            centers[0].neg().sub(centers[1]).add(centers[2]),
            centers[0].neg().sub(centers[1]).sub(centers[2])
        };
        List<Point2d> hull = createConvexHull(vertices);
        double area = 0;
        for (int i = 0; i < hull.size() - 1; i++) {
            area += hull.get(i).x * hull.get(i + 1).y;
        }
        area += hull.get(hull.size() - 1).x * hull.get(0).y;
        for (int i = 0; i < hull.size() - 1; i++) {
            area -= hull.get(i).y * hull.get(i + 1).x;
        }
        area -= hull.get(hull.size() - 1).y * hull.get(0).x;
        area *= 0.5;
        return area;
    }

    /**
     * Binary search for a rotation angle along the specified axis to match the target area.
     * 3D rotation formulas courtesy of https://en.wikipedia.org/wiki/Rotation_matrix
     * @return true if the target area has been matched, false if not.
     */
    private static boolean searchRotationAngle(Point3d[] centers, int axis, double maxAngle, double targetArea) {
        Point3d[] origCenters = new Point3d[3];
        for (int i = 0; i < centers.length; i++) origCenters[i] = new Point3d(centers[i].x, centers[i].y, centers[i].z);
        double from = 0;
        double to = maxAngle;
        while (true) {
            double mid = (from + to) / 2;
            switch (axis) {
                case 0:
                    for (int i = 0; i < centers.length; i++) {
                        centers[i] = new Point3d(
                                origCenters[i].x,
                                origCenters[i].y * Math.cos(mid) - origCenters[i].z * Math.sin(mid),
                                origCenters[i].y * Math.sin(mid) + origCenters[i].z * Math.cos(mid));
                    }
                    break;
                case 1:
                    for (int i = 0; i < centers.length; i++) {
                        centers[i] = new Point3d(
                                origCenters[i].x * Math.cos(mid) - origCenters[i].z * Math.sin(mid),
                                origCenters[i].y,
                                origCenters[i].x * Math.sin(mid) + origCenters[i].z * Math.cos(mid));
                    }
                    break;
                case 2:
                    for (int i = 0; i < centers.length; i++) {
                        centers[i] = new Point3d(
                                origCenters[i].x * Math.cos(mid) - origCenters[i].y * Math.sin(mid),
                                origCenters[i].x * Math.sin(mid) + origCenters[i].y * Math.cos(mid),
                                origCenters[i].z);
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
            double currentArea = computeArea(centers);
            if (Math.abs(currentArea - targetArea) < 1E-6) {
                System.err.println("Angle (degrees): " + (mid * 180.0 / Math.PI) + " on axis " + axis + " computer area: " + currentArea + " from " + from + " to " + to);
                return true;
            } else if (currentArea < targetArea) {
                if (from >= to - 1E-7) return false;
                from = mid;
            } else {
                to = mid;
            }
        }
    }

    /**
     * Binary search for a rotation angle that matches the target area.
     * @return The three face center coordinates required by the problem.
     */
    private static Point3d[] solve(double targetArea) {
        // First rotate along the z axis, from 0 degrees to 45 degrees for areas from 1 to sqrt(2)
        Point3d[] centers = new Point3d[] {
            new Point3d(0.5, 0, 0),
            new Point3d(0, 0.5, 0),
            new Point3d(0, 0, 0.5)
        };
        if (searchRotationAngle(centers, 2, Math.PI / 4, targetArea)) return centers;
        // For areas greater than sqrt(2), keep the cube rotated by 45 degrees along
        // the z axis and rotate from 0 degrees to ~35.26 degrees along the x axis.
        // The latter angle is the one required to align vertically the top
        // and bottom opposite vertices.
        if (searchRotationAngle(centers, 0, Math.PI / 2 - Math.atan(Math.sqrt(2)), targetArea)) return centers;
        throw new IllegalStateException("Unable to find a rotation for target area " + targetArea);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/qualification/CubicUfo-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                double area = scanner.nextDouble();
                Point3d[] centers = solve(area);
                System.out.println("Case #" + testNumber + ":");
                for (Point3d p : centers) System.out.println(p);
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
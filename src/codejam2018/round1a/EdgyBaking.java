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
package codejam2018.round1a;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.Scanner;

/**
 * The Edgy Baking problem from Google Code Jam Round 1A 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007883/dashboard/000000000002fff7
 * 
 * We are asked to come as close as possible to a maximum perimeter starting
 * from an initial perimeter, given by the sum 2*(w[i]+h[i]) of the perimeters
 * of all cookies. Let's subtract the initial perimeter and concentrate on the delta.
 * 
 * For simplicity's sake, lets say cookies are wider than tall (we invert
 * dimensions if needed). For each cookie, we can leave it uncut, or make a cut.
 * The shortest cut is vertical (adding 2h to the perimeter), while the longest
 * is on a diagonal (adding 2*sqrt(w^2+h^2) to the perimeter).
 * 
 * My first idea was going as close as possible to the desired delta perimeter
 * by making only vertical (shortest) cuts to as many cookies as possible,
 * then, for the cookies we opted to cut, add more perimeter by thinking of
 * slanting the cut towards the diagonal. This resulted in a classical
 * 0-1 knapsack problem where item values equal item weights (delta perimeters).
 * https://www.geeksforgeeks.org/knapsack-problem/
 * Unfortunately, this solution turned out to be wrong in general: by maximizing
 * the perimeter doing vertical cuts first, we can exclude cookies that would
 * look less than optimal at first (smaller height), but could add a larger
 * diagonal later.
 * For example with cookies 25x24, 147x115, 122x121, with a target perimeter
 * of 1569, the correct solution is to pick the first and the second, while
 * the incorrect solution would pick the first and the third.
 * 
 * Thus, we attempt to maximize the perimeter using the longest (diagonal) cuts
 * as knapsack item values, while having shortest (vertical) cuts not exceed
 * the target perimeter, by using them as item weights as before. The resulting
 * value could exceed the target perimeter, but we can remove the excess later.
 * 
 * With up to 100 cookies, each up to 250x250 mm big, we can reach at most
 * 100 * (4*250 + 2*sqrt(250^2+250^2)) mm by cutting all of them, that is
 * about 170000 mm, well below the advertised 10^8 limit, and a maximum
 * knapsack capacity of 2*sqrt(250^2+250^2), that is about 35000.
 * The O(nW) dynamic programming solution of the knapsack is tractable.
 * This can be likely optimized, for example by removing the factor of two
 * by all calculation we could halve the time and space needed for the knapsack,
 * but this run comfortably within the 15 seconds and 1GB limits.
 * 
 * @author Salvo Isaja
 */
public class EdgyBaking {

    private static final boolean DEBUG = true;
    private static final int MAX_POSSIBLE_PERIMETER = 100 * (4 * 250 + 2 * 354);

    /** Creates and prints a large random test set. */
    private static void createTests(int testCount) {
        System.out.println(testCount);
        for (int testNumber = 1; testNumber <= testCount; testNumber++) {
            int cookieCount = Math.max(1, (int) (Math.random() * 100));
            int[] widths = new int[cookieCount];
            int[] heights = new int[cookieCount];
            int initialPerimeter = 0;
            double maxDelta = 0;
            for (int i = 0; i < cookieCount; i++) {
                widths[i] = Math.max(1, (int) (Math.random() * 250));
                heights[i] = Math.max(1, (int) (Math.random() * 250));
                initialPerimeter += 2 * (widths[i] + heights[i]);
                maxDelta += 2 * Math.sqrt(widths[i] * widths[i] + heights[i] * heights[i]);
            }
            initialPerimeter += (int) (Math.random() * (maxDelta * 1.5));
            System.out.println(cookieCount + " " + initialPerimeter);
            for (int i = 0; i < cookieCount; i++) {
                System.out.println(widths[i] + " " + heights[i]);
            }
        }
    }

    /** Solves the 0-1 knapsack problem by dynamic programming. */
    private static double[][] knapsack(int capacity, int[] weights, double[] values) {
        assert weights.length == values.length;
        double dp[][] = new double[weights.length + 1][capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = 1; w <= capacity; w++) {
                if (weights[i] <= w) {
                    // i-th item would fit into the knapsack
                    dp[i + 1][w] = Math.max(
                            values[i] + dp[i][w - weights[i]], // use i-th item
                            dp[i][w]); // do not use i-th item
                } else {
                    // i-th item would not fit into the knapsack
                    dp[i + 1][w] = dp[i][w];
                }
            }
        }
        return dp;
    }

    /** Returns the set of items selected into a solved knapsack. */
    private static BitSet reconstructKnapsack(double[][] dp, int[] weights) {
        int itemCount = dp.length - 1;
        int maxWeight = dp[itemCount].length - 1;
        BitSet selectedItems = new BitSet(itemCount);
        while (itemCount > 0) {
            if (dp[itemCount][maxWeight] > dp[itemCount - 1][maxWeight]) {
                selectedItems.set(itemCount - 1);
                maxWeight -= weights[itemCount - 1];
            }
            itemCount--;
        }
        return selectedItems;
    }

    private static double solve(int targetPerimeter, int[] widths, int[] heights) {
        // Find the actual maximum weight (delta target perimeter) of the knapsack
        // as well as item weights and values
        assert widths.length == heights.length;
        int[] shortestCuts = new int[heights.length];
        double[] longestCuts = new double[heights.length];
        int initialPerimeter = 0;
        for (int i = 0; i < heights.length; i++) {
            initialPerimeter += (widths[i] + heights[i]) * 2;
            shortestCuts[i] = 2 * heights[i];
            longestCuts[i] = 2 * Math.sqrt(widths[i] * widths[i] + heights[i] * heights[i]);
        }
        if (targetPerimeter >= MAX_POSSIBLE_PERIMETER) targetPerimeter = MAX_POSSIBLE_PERIMETER;
        targetPerimeter -= initialPerimeter;
        // Apply the standard 0-1 knapsack algorithm to find the maximum
        // perimeter (value) achievable using diagonal cuts. The obtained
        // perimeter could exceed the target perimeter.
        double[][] dp = knapsack(targetPerimeter, shortestCuts, longestCuts);
        double result = dp[heights.length][targetPerimeter];
        // Reconstruct which items have been added to the knapsack
        BitSet selectedItems = reconstructKnapsack(dp, shortestCuts);
        // Remove excess perimeter for the selected items
        for (int i = selectedItems.nextSetBit(0); result > targetPerimeter && i >= 0; i = selectedItems.nextSetBit(i + 1)) {
            double delta = longestCuts[i] - shortestCuts[i];
            result -= delta;
            if (result <= targetPerimeter) result = targetPerimeter;
        }
        return initialPerimeter + result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (false) {
            createTests(10000);
            return;
        }
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/round1a/EdgyBaking-2.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int cookieCount = scanner.nextInt();
                int targetPerimeter = scanner.nextInt();
                int[] widths = new int[cookieCount];
                int[] heights = new int[cookieCount];
                for (int i = 0; i < cookieCount; i++) {
                    int w = scanner.nextInt();
                    int h = scanner.nextInt();
                    if (w >= h) {
                        widths[i] = w;
                        heights[i] = h;
                    } else {
                        widths[i] = h;
                        heights[i] = w;
                    }
                }
                double result = solve(targetPerimeter, widths, heights);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
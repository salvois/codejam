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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Waffle Choppers problem from Google Code Jam Round 1A 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007883/dashboard
 * 
 * The basic idea of this algorithm recalls the "Range Sum Query 2D - Immutable"
 * problem on LeetCode:
 * https://leetcode.com/problems/range-sum-query-2d-immutable/description/
 * where cumulative sums, precomputed in O(R*C) time, are used to find
 * rectangular sums in constant time.
 * 
 * For the outcome to be possible, several conditions must be met:
 * - the total number of chips must be divisible by the number of servings
 * - each horizontal slice must contain chips for V+1 servings
 * - each vertical slice must contain chips for H+1 servings
 * - each resulting rectangle must contain exactly the number of chips per serving.
 * We do a fixed number of scans of all R*C cells to check these conditions.
 * 
 * @author Salvo Isaja
 */
public class WaffleChoppers {

    private static final boolean DEBUG = true;

    private static boolean solve(boolean[][] cells, int horizontalCutCount, int verticalCutCount) {
        int height = cells.length;
        int width = cells[0].length;
        // Build a matrix of cumulative sums, where each cell contains the sum from [0][0] to [y][x]
        int[][] cumulativeSums = new int[height][width];
        for (int y = 0; y < cells.length; y++) {
            boolean[] row = cells[y];
            for (int x = 0; x < row.length; x++) {
                int top = y > 0 ? cumulativeSums[y - 1][x] : 0;
                int left = x > 0 ? cumulativeSums[y][x - 1] : 0;
                int topleft = y > 0 && x > 0 ? cumulativeSums[y - 1][x - 1] : 0;
                cumulativeSums[y][x] = (row[x] ? 1 : 0) + top + left - topleft;
            }
        }
        int servings = (horizontalCutCount + 1) * (verticalCutCount + 1);
        if (cumulativeSums[height - 1][width - 1] % servings != 0) return false;
        int chipsPerServing = cumulativeSums[height - 1][width - 1] / servings;
        // Scan horizontal slices
        int neededChipsPerHorizontalSlice = chipsPerServing * (verticalCutCount + 1);
        List<Integer> horizontalCutsAfter = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            int count = cumulativeSums[y][width - 1];
            if (!horizontalCutsAfter.isEmpty()) count -= cumulativeSums[horizontalCutsAfter.get(horizontalCutsAfter.size() - 1)][width - 1];
            if (count == neededChipsPerHorizontalSlice) {
                horizontalCutsAfter.add(y);
            } else if (count > neededChipsPerHorizontalSlice) {
                return false;
            }
        }
        // Scan vertical slices
        int neededChipsPerVerticalSlice = chipsPerServing * (horizontalCutCount + 1);
        List<Integer> verticalCutsAfter = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            int count = cumulativeSums[height - 1][x];
            if (!verticalCutsAfter.isEmpty()) count -= cumulativeSums[height - 1][verticalCutsAfter.get(verticalCutsAfter.size() - 1)];
            if (count == neededChipsPerVerticalSlice) {
                verticalCutsAfter.add(x);
            } else if (count > neededChipsPerVerticalSlice) {
                return false;
            }
        }
        // Check whether all servigs contains the same number of chips
        for (int h = 0; h < horizontalCutsAfter.size(); h++) {
            for (int v = 0; v < verticalCutsAfter.size(); v++) {
                int y = horizontalCutsAfter.get(h);
                int x = verticalCutsAfter.get(v);
                int top = h > 0 ? cumulativeSums[horizontalCutsAfter.get(h - 1)][x] : 0;
                int left = v > 0 ? cumulativeSums[y][verticalCutsAfter.get(v - 1)] : 0;
                int topleft = h > 0 && v > 0 ? cumulativeSums[horizontalCutsAfter.get(h - 1)][verticalCutsAfter.get(v - 1)] : 0;
                int count = cumulativeSums[y][x] - top - left + topleft;
                if (count != chipsPerServing) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/round1a/WaffleChoppers-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int rowCount = scanner.nextInt();
                int columnCount = scanner.nextInt();
                int horizontalCutCount = scanner.nextInt();
                int verticalCutCount = scanner.nextInt();
                boolean[][] cells = new boolean[rowCount][columnCount];
                for (int y = 0; y < rowCount; y++) {
                    String s = scanner.next();
                    for (int x = 0; x < columnCount; x++) cells[y][x] = s.charAt(x) == '@';
                }
                boolean result = solve(cells, horizontalCutCount, verticalCutCount);
                System.out.println("Case #" + testNumber + ": " + (result ? "POSSIBLE" : "IMPOSSIBLE"));
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
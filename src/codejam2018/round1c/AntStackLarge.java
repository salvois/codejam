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
package codejam2018.round1c;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Ant Stack problem from Google Code Jam Round 1A 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007765/dashboard/000000000003e0a8
 * Large dataset version.
 * 
 * I couldn't figure out this solution. This is implemented with the help of
 * the official Code Jam analysis:
 * https://codejam.withgoogle.com/2018/challenges/0000000000007765/analysis/000000000003e0a8
 * 
 * A variation on the idea of the 0-1 knapsack can be used to solve the large
 * dataset, with some observations.
 * First, given the limits, there is an upper bound of 139 on the number of ants
 * that can go in any stack. This can be easily computed with a script such as
 * the included AntStackGenerator.py.
 * 
 * This time, the dp[i][w] array represents the minimum weight of a stack
 * of w ants using ants up to the i-th. This differs from the usual knapsack
 * solution by storing the minimum instead of the maximum, and keying by the
 * number of ants instead of their weight, effectively swapping "values" with
 * "weights" of knapsack items, and looking for the minimum "value" instead
 * of the maximum.
 * 
 * @author Salvo Isaja
 */
public class AntStackLarge {

    private static final boolean DEBUG = true;
    private static final int MAX_ANTS = 139;

    private static int solve(int[] weights) {
        // Initialize the dp array
        int dp[][] = new int[weights.length + 1][MAX_ANTS + 1];
        for (int i = 0; i <= weights.length; i++) {
            for (int w = 1; w <= MAX_ANTS; w++) {
                dp[i][w] = Integer.MAX_VALUE;
            }
        }
        // Knapsack-like logic
        for (int i = 0; i < weights.length; i++) {
            for (int w = 1; w <= MAX_ANTS; w++) {
                if (dp[i][w - 1] <= 6 * weights[i]) {
                    // i-th item would fit into the knapsack
                    dp[i + 1][w] = Math.min(
                            weights[i] + dp[i][w - 1], // use i-th item
                            dp[i][w]); // do not use i-th item
                } else {
                    // i-th item would not fit into the knapsack
                    dp[i + 1][w] = dp[i][w];
                }
            }
        }
        // Find the maximum populated stack size
        int result = 0;
        for (int i = 0; i <= weights.length; i++) {
            for (int w = 1; w <= MAX_ANTS; w++) {
                if (dp[i][w] < Integer.MAX_VALUE && w > result) {
                    result = w;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/round1c/AntStack-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int antCount = scanner.nextInt();
                int[] weights = new int[antCount];
                for (int i = 0; i < antCount; i++) weights[i] = scanner.nextInt();
                int maxStackSize = solve(weights);
                System.out.println("Case #" + testNumber + ": " + maxStackSize);
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
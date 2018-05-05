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
 * Small dataset version.
 * 
 * This is yet another 0-1 knapsack problem, with ant values equal to one, and
 * a knapsack capacity of 6 * 1000 + 1000, that is the maximum weight of a stack.
 * The only difference from the standard algorithm is that, when an item
 * is put into the knapsack, the weight to take is not just dp[i][w - weights[i]]
 * but the minimum between dp[i][w - weights[i]] and 6 * weights[i],
 * because a (sub)stack of i ants cannot weigh more than 6 * weights[i].
 * 
 * Due to the large number of ants and weight range of test set 2, this
 * solution is only applicable to test set 1.
 * 
 * @author Salvo Isaja
 */
public class AntStackSmall {

    private static final boolean DEBUG = true;
    private static final int MAX_WEIGHT = 7000;

    private static int solve(int[] weights) {
        int dp[][] = new int[weights.length + 1][MAX_WEIGHT + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = 1; w <= MAX_WEIGHT; w++) {
                if (weights[i] <= w) {
                    // i-th item would fit into the knapsack
                    dp[i + 1][w] = Math.max(
                            1 + dp[i][Math.min(6 * weights[i], w - weights[i])], // use i-th item
                            dp[i][w]); // do not use i-th item
                } else {
                    // i-th item would not fit into the knapsack
                    dp[i + 1][w] = dp[i][w];
                }
            }
        }
        return dp[weights.length][MAX_WEIGHT];
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
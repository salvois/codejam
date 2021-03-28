/*
Solutions for Code Jam 2021.
Copyright 2021 Salvatore ISAJA. All rights reserved.

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
package codejam2021.qualification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * The Moons and Umbrellas problem from Google Code Jam Qualification 2021.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000043580a/00000000006d1145
 * 
 * Starting from the beginning of the sequence, for each '?' element,
 * this solution just tries to insert both a 'C' and a 'J' and computes
 * which one would generate the lowest cost.
 * If the very first character is a '?', we can't know in advance whether,
 * given the following elements, starting with a 'C' or a 'J' would result in
 * lower cost, so we just try both.
 * 
 * Working with signed costs, I expected this to work even for the special
 * case of negative costs, but it resulted in a Wrong Answer verdict.
 * Clearly this greedy approach cannot always find the global minimum cost.
 * 
 * @author Salvo Isaja
 */
public class MoonsAndUmbrellas {

    private static final boolean DEBUG = true;
    
    private static int computeCost(int cjCost, int jcCost, String mural) {
        int totalCost = 0;
        StringBuilder result = new StringBuilder(mural);
        for (int i = 0; i < result.length() - 1; i++) {
            char c1 = result.charAt(i);
            char c2 = result.charAt(i + 1);
            int cost = 0;
            assert c1 != '?';
            if (c2 == '?') {
                int costForJ = c1 == 'C' ? cjCost : 0;
                int costForC = c1 == 'J' ? jcCost : 0;
                if (costForJ < costForC) {
                    result.setCharAt(i + 1, 'J');
                    cost = costForJ;
                } else {
                    result.setCharAt(i + 1, 'C');
                    cost = costForC;
                }
            }
            else if (c1 == 'C' && c2 == 'J') cost = cjCost;
            else if (c1 == 'J' && c2 == 'C') cost = jcCost;
            totalCost += cost;
        }
        System.err.println(mural + " became " + result + " costing " + totalCost);
        return totalCost;
    }
    
    private static int solve(int cjCost, int jcCost, String mural) {
        if (mural.charAt(0) == '?') {
            int totalCostForC = computeCost(cjCost, jcCost, 'C' + mural.substring(1));
            int totalCostForJ = computeCost(cjCost, jcCost, 'J' + mural.substring(1));
            return Math.min(totalCostForC, totalCostForJ);
        }
        return computeCost(cjCost, jcCost, mural);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2021/qualification/MoonsAndUmbrellas-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int cjCost = scanner.nextInt();
                int jcCost = scanner.nextInt();
                String mural = scanner.next();
                int totalCost = solve(cjCost, jcCost, mural);
                System.out.println("Case #" + testNumber + ": " + totalCost);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
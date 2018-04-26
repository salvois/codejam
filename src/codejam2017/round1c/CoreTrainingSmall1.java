/*
Solutions for Code Jam 2017.
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
package codejam2017.round1c;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Core Training problem from Round 1C of Code Jam 2017.
 * https://codejam.withgoogle.com/codejam/contest/3274486/dashboard#s=p2
 * Small dataset 1 version.
 * 
 * With all cores required to function, we have to maximize the product
 * of all core success probabilities. Thus, we need to raise as much as possible
 * the lowest values, until we run out of training units.
 * For example, with 0.4 training units, 0.2 0.2 0.3 0.6 must first become
 * 0.3 0.3 0.3 0.6, then 0.366 0.366 0.366 0.6.
 * 
 * I couldn't figure out how to handle dataset 2!
 * 
 * @author Salvo Isaja
 */
public class CoreTrainingSmall1 {

    private static final boolean DEBUG = false;

    private static double solve(double trainingUnits, double[] coreProbs) {
        Arrays.sort(coreProbs);
        while (trainingUnits > 0 && coreProbs[0] < 1) {
            int secondLowest = 1;
            while (secondLowest < coreProbs.length) {
                if (coreProbs[secondLowest] > coreProbs[0]) break;
                secondLowest++;
            }
            double aim = secondLowest < coreProbs.length ? coreProbs[secondLowest] : 1;
            double delta = (aim - coreProbs[0]) * secondLowest;
            double inc = Math.min(delta, trainingUnits);
            for (int i = 0; i < secondLowest; i++) {
                coreProbs[i] += inc / secondLowest;
            }
            trainingUnits -= inc;
        }
        double result = 1;
        for (double p : coreProbs) result *= p;
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1c/C-small-practice-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int coreCount = scanner.nextInt();
                int minCoreCount = scanner.nextInt(); // ignored for small dataset 1
                double trainingUnits = scanner.nextDouble();
                double[] coreProbs = new double[coreCount];
                for (int i = 0; i < coreCount; i++) coreProbs[i] = scanner.nextDouble();
                double result = solve(trainingUnits, coreProbs);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("CoreTrainingSmall1 done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
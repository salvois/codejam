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
package codejam2017.round1c;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Ample Syrup problem from Round 1C of Code Jam 2017.
 * https://codejam.withgoogle.com/codejam/contest/3274486/dashboard
 * 
 * The first observation is that, by stacking pancakes of decreasing sizes,
 * the top surface is always the same as the bottom layer. In fact, any
 * other circles cancel themselves in pairs.
 * The top surface must be added to the side surface, resulting, for k pancakes,
 * in: sum(2pi*Ri*Hi)+pi*Rk^2, that is, the surface of the bottommost pancake
 * (top and side) plus the side surface of any other selected pancake.
 * 
 * To maximize the result, we first try larger pancakes at bottom, sorting
 * them in decreasing order of total surface. For each of these pancakes,
 * we try k-1 smaller pancakes, sorted in decreasing order of side surface.
 * 
 * As a result, we do a O(n*log2(n)) sort, we loop n times, and, for each
 * iteration, we do an O(n*log2(n)) sort, thus we run in O(n^2*log2^2(n)).
 * 
 * @author Salvo Isaja
 */
public class AmpleSyrup {

    private static class Pancake {
        public final int radius;
        public final int height;
        public final double topSurface;
        public final double sideSurface;

        public Pancake(int radius, int height) {
            this.radius = radius;
            this.height = height;
            topSurface = Math.PI * radius * radius;
            sideSurface = 2 * Math.PI * radius * height;
        }
    }
    
    private static final boolean DEBUG = false;

    private static double solve(int stackSize, Pancake[] pancakes) {
        double result = 0;
        Arrays.sort(pancakes, (a, b) -> b.radius - a.radius);
        for (int i = 0; i < pancakes.length - (stackSize - 1); i++) {
            double surface = pancakes[i].topSurface + pancakes[i].sideSurface;
            if (stackSize > 1) {
                Pancake[] others = new Pancake[pancakes.length - i - 1];
                for (int j = i + 1; j < pancakes.length; j++) others[j - i - 1] = pancakes[j];
                Arrays.sort(others, (a, b) -> (int) Math.signum(b.sideSurface - a.sideSurface));
                for (int j = 0; j < stackSize - 1; j++) surface += others[j].sideSurface;
            }
            if (surface > result) result = surface;
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1c/A-large-practice.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int pancakeCount = scanner.nextInt();
                int stackSize = scanner.nextInt();
                Pancake[] pancakes = new Pancake[pancakeCount];
                for (int i = 0; i < pancakeCount; i++) {
                    pancakes[i] = new Pancake(scanner.nextInt(), scanner.nextInt());
                }
                double result = solve(stackSize, pancakes);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("AmpleSyrup done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
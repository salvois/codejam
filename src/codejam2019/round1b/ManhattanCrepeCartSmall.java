/*
Solutions for Code Jam 2019.
Copyright 2019 Salvatore ISAJA. All rights reserved.

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
package codejam2019.round1b;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * The Manhattan Crepe Cart problem from Google Code Jam Round-1B 2019.
 * https://codingcompetitions.withgoogle.com/codejam/round/0000000000051706/000000000012295c
 * Small dataset version.
 * 
 * This O(P*Q^2) version trivially computes the score of each intersection.
 * 
 * @author Salvo Isaja
 */
public class ManhattanCrepeCartSmall {

    private static final boolean DEBUG = true;
    
    private static class Pair {
        public int item1;
        public int item2;

        public Pair(int item1, int item2) {
            this.item1 = item1;
            this.item2 = item2;
        }
    }

    private static void fill(int[] scores, int width, int x0, int y0, int x1, int y1) {
        for (int y = y0; y <= y1; y++)
            for (int x = x0; x <= x1; x++)
                scores[y * width + x]++;
    }
    
    private static Pair solve(int[] xs, int[] ys, char[] dirs, int maxCoord) {
        int width = maxCoord + 1;
        int[] scores = new int[width * width];
        for (int p = 0; p < xs.length; p++) {
            switch (dirs[p]) {
                case 'N': fill(scores, width, 0, ys[p] + 1, maxCoord, maxCoord); break;
                case 'S': fill(scores, width, 0, 0, maxCoord, ys[p] - 1); break;
                case 'E': fill(scores, width, xs[p] + 1, 0, maxCoord, maxCoord); break;
                case 'W': fill(scores, width, 0, 0, xs[p] - 1, maxCoord); break;
                default: throw new IllegalArgumentException();
            }
        }
        int topX = 0;
        int topY = 0;
        for (int y = 0; y <= maxCoord; y++) {
            for (int x = 0; x <= maxCoord; x++) {
                if (scores[y * width + x] > scores[topY * width + topX]) {
                    topY = y;
                    topX = x;
                }
            }
        }
        return new Pair(topX, topY);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2019/round1b/ManhattanCrepeCart-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int peopleCount = scanner.nextInt();
                int maxCoord = scanner.nextInt();
                int[] xs = new int[peopleCount];
                int[] ys = new int[peopleCount];
                char[] dirs = new char[peopleCount];
                for (int p = 0; p < peopleCount; p++) {
                    xs[p] = scanner.nextInt();
                    ys[p] = scanner.nextInt();
                    dirs[p] = scanner.next().charAt(0);
                }
                Pair result = solve(xs, ys, dirs, maxCoord);
                System.out.println("Case #" + testNumber + ": " + result.item1 + " " + result.item2);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
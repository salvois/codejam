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

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Draupnir from Google Code Jam Round-1B 2019.
 * https://codingcompetitions.withgoogle.com/codejam/round/0000000000051706/0000000000122837
 * 
 * The six values are spaced apart by decreasing powers of two, such that
 * the sum for the n-th day is:
 * R1*2^n + R2*2^(n/2) + R3*2^(n/3) + R4*2^(n/4) + R5*2^(n/5) + R6*2^(n/6)
 * 
 * If we find an n such that those powers of 2 are spaced more than 100 units
 * apart (say 128, that is 7 bits), we can find each Ri by bit-masking.
 * n=210 does the job, as 210/6=35, 210/5=42 and 210/4=52, so they are spaced
 * at least 7 bits apart.
 * But we cannot do this in one pass, because 2^210 does not fit in the
 * daily sum modulo 2^63 we can ask for.
 * 
 * So we can use n=210 to find the three least significant rings, and
 * n=48 (as 48/3=16, 48/2=24 and 48 are spaced at least 7 bits apart)
 * to find the three most significant rings.
 * 
 * @author Salvo Isaja
 */
public class Draupnir {

    private static final boolean DEBUG = true;
    
    private static int[] solve(long wLow, long wHigh) {
        int[] rings = new int[6];
        rings[5] = (int) ((wLow >> 35) & 127);
        rings[4] = (int) ((wLow >> 42) & 127);
        rings[3] = (int) ((wLow >> 52) & 127);
        wHigh -= ((long) rings[3] << 12) + ((long) rings[4] << 9) + ((long) rings[5] << 8);
        rings[2] = (int) ((wHigh >> 16) & 127);
        rings[1] = (int) ((wHigh >> 24) & 127);
        rings[0] = (int) ((wHigh >> 48) & 127);
        return rings;
    }
    
    private static void test() {
        for (int t = 0; t < 1000; t++) {
            int[] expected = new int[] {
                ThreadLocalRandom.current().nextInt(0, 100),
                ThreadLocalRandom.current().nextInt(0, 100),
                ThreadLocalRandom.current().nextInt(0, 100),
                ThreadLocalRandom.current().nextInt(0, 100),
                ThreadLocalRandom.current().nextInt(0, 100),
                ThreadLocalRandom.current().nextInt(0, 100)
            };
            long wLow = ((long) expected[5] << 35) + ((long) expected[4] << 42) + ((long) expected[3] << 52);
            long wHigh = ((long) expected[2] << 16) + ((long) expected[1] << 24) + ((long) expected[0] << 48)
                    + ((long) expected[3] << 12) + ((long) expected[4] << 9) + ((long) expected[5] << 8);
            int[] actual = solve(wLow, wHigh);
            for (int i = 0; i < actual.length; i++) {
                if (actual[i] != expected[i])
                    throw new AssertionError();
            }
        }
    }

    public static void main(String[] args) {
        long beginTime = System.nanoTime();
        //test();
        try (Scanner scanner = new Scanner(System.in)) {
            int testCount = scanner.nextInt();
            int wellCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                System.out.println(210);
                System.out.flush();
                long wLow = scanner.nextLong();
                System.out.println(48);
                System.out.flush();
                long wHigh = scanner.nextLong();
                int[] rings = solve(wLow, wHigh);
                System.out.printf("%d %d %d %d %d %d\n", rings[0], rings[1], rings[2], rings[3], rings[4], rings[5]);
                System.out.flush();
                int verdict = scanner.nextInt();
                if (verdict > 0) {
                    System.err.println("Correct");
                } else {
                    System.err.println("Wrong");
                }
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
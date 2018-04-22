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
import java.util.Arrays;
import java.util.Scanner;

/**
 * The Bit Party problem from Google Code Jam Round 1A 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007883/dashboard/000000000002fff6
 * 
 * We have to partition up to a billion bits to up to 1000 robots and cashiers,
 * so this huge number of combinations suggests that a logarithmic or better
 * approach is required.
 * The non-uniform cap on the bits per cashiers and the payment offset of
 * each cashier seem to rule out trivial forms of balancing, because the
 * "bandwidth" of each cashier varies over time.
 * Thus, we could just try to guess a time, and check if it would be possible
 * to pay all bits within that time. The "bandwidth" of each cashier is constant
 * once a specific time has been picked, so we can start paying bits from the
 * fastest cashier. If paying all bits is possible, we could try a shorter time.
 * The highest possible time to try is the one of the slowest cashier at
 * full capacity, that is Mi * Si + Pi, that could be as big as 10^9 * 10^9 + 10^9.
 * Quite a lot of times to test! Binary search comes to mind, as there is a
 * breaking point between impossible and possible guessed times.
 * 10^9 * 10^9 + 10^9 is almost 2^60, so up to 60 iteration must be made.
 * For each iteration, we must compute the "bandwidth" of each of the C cashiers,
 * then sort cashiers from the fastest to the slowest, then let each cashier
 * process their bits. This is dominated by the sort, that is 1000*log2(1000),
 * or about 10000 iterations, thus the algorithm would complete within the limits
 * in at most 600 thousands steps.
 * 
 * @author Salvo Isaja
 */
public class BitParty {

    private static final class Cashier {
        final int maxBitCount;
        final int bitScanSeconds;
        final int paymentSeconds;
        long bitsPerTime; // for the greedy algorithm below

        Cashier(int maxBitCount, int bitScanSeconds, int paymentSeconds) {
            this.maxBitCount = maxBitCount;
            this.bitScanSeconds = bitScanSeconds;
            this.paymentSeconds = paymentSeconds;
        }
    }
    
    private static final boolean DEBUG = true;

    private static boolean isEnoughTime(long testedTime, int robotCount, int bitCount, Cashier[] cashiers) {
        for (Cashier c : cashiers) {
            c.bitsPerTime = (testedTime - c.paymentSeconds) / c.bitScanSeconds;
            if (c.bitsPerTime < 0) c.bitsPerTime = 0;
            else if (c.bitsPerTime > c.maxBitCount) c.bitsPerTime = c.maxBitCount;
        }
        Arrays.sort(cashiers, (a, b) -> -Long.signum(a.bitsPerTime - b.bitsPerTime));
        long resultingTime = 0;
        for (Cashier c : cashiers) {
            if (bitCount == 0) break;
            if (robotCount == 0) break;
            long b = Math.min(bitCount, c.bitsPerTime);
            if (b == 0) break;
            long t = b * c.bitScanSeconds + c.paymentSeconds;
            if (t > resultingTime) resultingTime = t;
            bitCount -= b;
            robotCount--;
        }
        return bitCount == 0;
    }

    private static long solve(int robotCount, int bitCount, Cashier[] cashiers) {
        long from = 0;
        long to = (1L << 60) + (1L << 30); // the maximum possible time ever
        while (from < to) {
            long mid = from + (to - from) / 2;
            boolean b = isEnoughTime(mid, robotCount, bitCount, cashiers);
            if (b) {
                to = mid;
            } else {
                from = mid + 1;
            }
        }
        return from;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/round1a/BitParty-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int robotCount = scanner.nextInt();
                int bitCount = scanner.nextInt();
                int cashierCount = scanner.nextInt();
                Cashier[] cashiers = new Cashier[cashierCount];
                for (int i = 0; i < cashiers.length; i++) {
                    cashiers[i] = new Cashier(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
                }
                long result = solve(robotCount, bitCount, cashiers);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
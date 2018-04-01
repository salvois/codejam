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
package codejam2018.practice;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * The Steed 2: Cruise Control problem from Google Code Jam Practice Session 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000000130/dashboard/0000000000000524
 * 
 * You just need to consider the slowest horse!
 * Any other horse, either in front or behind, including Annie's, must wait the
 * slowest horse to arrive at destination, thus this is how long Annie's trip
 * would need to take.
 *
 * After sketching the problem on paper, the problem looked so simple that
 * I was sure I was wrong! Due to the articulated problem description and
 * detailed limits, I thought a lot what I could be missing, either special
 * corner cases, or subtle issues with overflows or floating point precision,
 * but I couldn't find any.
 * So there it is, four Java lines to find a minimum :)
 * 
 * @author Salvo Isaja
 */
public class Steed2CruiseControl {

    private static final boolean DEBUG = false;

    private static void solve(int testNumber, int destinationDistance, int[] horseBegins, int[] horseSpeeds) {
        double minAnnieSpeed = Double.MAX_VALUE;
        for (int i = 0; i < horseBegins.length; i++) {
            double annieSpeed = (double) ((long) destinationDistance * horseSpeeds[i]) / (destinationDistance - horseBegins[i]);
            if (annieSpeed < minAnnieSpeed) minAnnieSpeed = annieSpeed;
        }
        System.out.println("Case #" + testNumber + ": " + minAnnieSpeed);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/practice/Steed2CruiseControl-1.in") : System.in;
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int destinationDistance = scanner.nextInt();
                int horseCount = scanner.nextInt();
                int[] horseBegins = new int[horseCount];
                int[] horseSpeeds = new int[horseCount];
                for (int i = 0; i < horseCount; i++) {
                    horseBegins[i] = scanner.nextInt();
                    horseSpeeds[i] = scanner.nextInt();
                }
                solve(testNumber, destinationDistance, horseBegins, horseSpeeds);
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
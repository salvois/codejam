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
package codejam2018.round1b;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Rounding Error problem from Google Code Jam Round 1B 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007764/dashboard
 * 
 * We want the fractional part of the percentage of each programming language to
 * be greater than or equal to 0.5, so that it rounds up during the final sum.
 * Thus, whenever we find a fractional part less than 0.5 (referred to as
 * "low scores" below) we add the percentage of one the remaining people to try
 * to make the fractional at least 0.5.
 * We give priority to percentages whose fractional part is as close as possible
 * to 0.5, in order to maximize the number of percentages we can bring above
 * the 0.5 threshold.
 * If we have no percentages whose fractional part is less than 0.5, we
 * add a new percentage for a new programming language, and repeat the process.
 * We don't touch percentages whose fractional part is already >= 0.5
 * (referred to as "high scores" below).
 * 
 * I got a "time limit exceeded" on the hidden data set 3 wrong during the
 * contest because I naively did an O(n) search for low scores, resulting
 * in an O(n^2) total complexity!
 * An O(log2(n)) priority queue can handle the 10^5 limit without problems.
 * 
 * @author Salvo Isaja
 */
public class RoundingError {

    private static final boolean DEBUG = true;

    private static int solve(int peopleCount, int[] languagePeople) {
        List<Double> highScores = new ArrayList<>();
        PriorityQueue<Double> lowScores = new PriorityQueue<>((a, b) -> (int) Math.signum((b - b.intValue()) - (a - a.intValue())));
        int peopleLeft = peopleCount;
        double percentPerPerson = 100.0 / peopleCount;
        for (int i = 0; i < languagePeople.length; i++) {
            peopleLeft -= languagePeople[i];
            double score = percentPerPerson * languagePeople[i];
            double d = score - (int) score;
            if (d > 0 && d < 0.5) lowScores.add(score);
            else highScores.add(score);
        }
        while (peopleLeft > 0) {
            Double score = lowScores.poll();
            if (score == null) score = 0.0;
            score += percentPerPerson;
            double d = score - score.intValue();
            if (d > 0 && d < 0.5) lowScores.add(score);
            else highScores.add(score);
            peopleLeft--;
        }
        int sum = 0;
        for (double d : highScores) sum += Math.round(d);
        for (double d : lowScores) sum += Math.round(d);
        return sum;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/round1b/RoundingError-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int peopleCount = scanner.nextInt();
                int currentLanguageCount = scanner.nextInt();
                int[] languagePeople = new int[currentLanguageCount];
                for (int i = 0; i < languagePeople.length; i++) languagePeople[i] = scanner.nextInt();
                int result = solve(peopleCount, languagePeople);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
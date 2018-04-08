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
package codejam2018.qualification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * The Trouble Sort problem from Google Code Jam Qualification 2018.
 * https://codejam.withgoogle.com/2018/challenges/00000000000000cb/dashboard/00000000000079cb
 * Large dataset version.
 * 
 * That was coded on hindsight, when I realized I misread the limits.
 * Looking at how the "sorting" algorithm works, it is clear that it always
 * swaps even-indexed values with even-indexed values and odd-indexed values
 * with odd-indexed values. Thus, the result is actually the superposition
 * of two regular sorts for the even-indexed and odd-indexed subsequences.
 * Using quicksort via Arrays.sort() provides an efficient implementation.
 * 
 * @author Salvo Isaja
 */
public class TroubleSortLarge {

    private static final boolean DEBUG = true;

    private static int solve(int[] values) {
        int[] evenIndexedValues = new int[(values.length + 1) / 2];
        int[] oddIndexedValues = new int[values.length / 2];
        for (int i = 0; i < values.length; i += 2) evenIndexedValues[i / 2] = values[i];
        for (int i = 1; i < values.length; i += 2) oddIndexedValues[i / 2] = values[i];
        Arrays.sort(evenIndexedValues);
        Arrays.sort(oddIndexedValues);
        // Check if the logically reassembled array is sorted
        int last = evenIndexedValues[0];
        for (int i = 1; i < values.length; i++) {
            int curr = i % 2 == 0 ? evenIndexedValues[i / 2] : oddIndexedValues[i / 2];
            if (curr < last) return i - 1;
            last = curr;
        }
        return -1;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/qualification/TroubleSort-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int valueCount = scanner.nextInt();
                int[] values = new int[valueCount];
                for (int i = 0; i < valueCount; i++) values[i] = scanner.nextInt();
                int result = solve(values);
                System.out.println("Case #" + testNumber + ": " + (result >= 0 ? result : "OK"));
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
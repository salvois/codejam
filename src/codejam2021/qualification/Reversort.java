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
 * The Reversort problem from Google Code Jam Qualification 2021.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000043580a/00000000006d0a5c
 * 
 * This is just an implementation of the proposed algorithm.
 * 
 * @author Salvo Isaja
 */
public class Reversort {

    private static final boolean DEBUG = true;

    private static int findMinIndex(int[] elements, int begin, int end) {
        int minIndex = begin;
        for (int i = begin; i < end; i++)
            if (elements[i] < elements[minIndex])
                minIndex = i;
        return minIndex;
    }
    
    private static void reverse(int[] elements, int begin, int count) {
        for (int i = 0; i < count / 2; i++) {
            int t = elements[i + begin];
            elements[i + begin] = elements[count - i - 1 + begin];
            elements[count - i - 1 + begin] = t;
        }
    }
    
    private static void solve(int testNumber, int[] elements) {
        int cost = 0;
        for (int i = 0; i < elements.length - 1; i++) {
            int j = findMinIndex(elements, i, elements.length);
            int reverseCount = j - i + 1;
            reverse(elements, i, reverseCount);
            cost += reverseCount;
        }
        System.out.println("Case #" + testNumber + ": " + cost);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2021/qualification/ReversortEngineering-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int elementCount = scanner.nextInt();
                int[] elements = new int[elementCount];
                for (int i = 0; i < elementCount; i++)
                    elements[i] = scanner.nextInt();
                solve(testNumber, elements);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
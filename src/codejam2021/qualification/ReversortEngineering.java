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
 * The Reversort Engineering problem from Google Code Jam Qualification 2021.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000043580a/00000000006d12d7
 * 
 * Note that, given a sequence of N elements, the proposed sorting algorithm
 * can do at most the following reverse operations:
 * element index: 0  1    ..  N-3  N-2  N-1
 * reverse count: N  N-1      3    2
 * While at minimum one reverse operation must be performed for each index
 * between 0 and N-2.
 * Thus, finding a sequence if possible if the expected cost is between
 * N-1 and N*(N+1)/2-1 (that the sum of numbers between 2 and N).
 * 
 * To find a sequence, just compute the desired number of reverse operation
 * for each element index, starting from 1,1,1,.,1 and incrementing it up
 * to the maximum reverse count for that index and up to the expected cost.
 * Finally, just apply the reverse operation to an ordered sequence.
 * 
 * @author Salvo Isaja
 */
public class ReversortEngineering {

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
    
    private static int[] calculateReverseCounts(int elementCount, int cost) {
        int[] reverseCounts = new int[elementCount - 1];
        for (int i = 0; i < elementCount - 1; i++)
            reverseCounts[i] = 1;
        int cumulativeReverseCount = elementCount - 1;
        for (int i = 0; i < elementCount - 1; i++) {
            int maxDelta = i + 1;
            int delta = cost - cumulativeReverseCount;
            if (delta < 0) delta = 0;
            if (delta >= maxDelta) delta = maxDelta;
            reverseCounts[elementCount - i - 2] += delta;
            cumulativeReverseCount += delta;
        }
        return reverseCounts;
    }
    
    private static int computeCost(int[] elements) {
        int cost = 0;
        for (int i = 0; i < elements.length - 1; i++) {
            int j = findMinIndex(elements, i, elements.length);
            int reverseCount = j - i + 1;
            reverse(elements, i, reverseCount);
            cost += reverseCount;
        }
        return cost;
    }
    
    private static String elementsToString(int[] elements) {
        StringBuilder elementList = new StringBuilder();
        for (int element : elements) {
            if (elementList.length() > 0) elementList.append(' ');
            elementList.append(element);
        }
        return elementList.toString();
    }
    
    private static void solve(int testNumber, int elementCount, int expectedCost) {
        if (expectedCost < elementCount - 1 || expectedCost > (elementCount * (elementCount + 1) / 2 - 1)) {
            System.out.println("Case #" + testNumber + ": IMPOSSIBLE");
            return;
        }
        int[] reverseCounts = calculateReverseCounts(elementCount, expectedCost);
        int[] elements = new int[elementCount];
        for (int i = 0; i < elementCount; i++)
            elements[i] = i + 1;
        for (int i = elementCount - 2; i >= 0; i--)
            reverse(elements, i, reverseCounts[i]);
        String elementList = elementsToString(elements);
        if (DEBUG) {
            int actualCost = computeCost(elements);
            System.out.println(elementList + " costs " + actualCost + ", expected " + expectedCost);
        }
        System.out.println("Case #" + testNumber + ": " + elementList);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2021/qualification/ReversortEngineering-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int elementCount = scanner.nextInt();
                int expectedCost = scanner.nextInt();
                solve(testNumber, elementCount, expectedCost);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
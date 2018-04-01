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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Senate Evacuation problem from Google Code Jam Practice Session 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000000130/dashboard/00000000000004c0
 * Multi-threaded version.
 * 
 * The problem begins with none of the parties having the absolute majority.
 * Thus, removing one senator from the party with the largest count of senators
 * keeps the senate balanced. Since we are not asked the optimal evacuation plan,
 * I just opted to let them evacuate one at a time whenever possible.
 * An exception is when there are only two parties with senators left. They
 * must have the same number of senators, otherwise one would have the
 * absolute majority. Thus, we have to evacuate two at once.
 * The O(n) complexity, with n being the total number of senators, lets this
 * algorithm do both the small and large datasets without problems.
 * 
 * I ended up using a thread pool to test my multi-threaded code template
 * with the new contest platform.
 * 
 * @author Salvo Isaja
 */
public class SenateEvacuationMT {

    private static class Test implements Callable<String> {

        private final int testNumber;
        private final int[] partySenators;
        
        public Test(int testNumber, int[] partySenators) {
            this.testNumber = testNumber;
            this.partySenators = partySenators;
        }

        @Override
        public String call() {
            System.err.println("Case #" + testNumber + ", " + partySenators.length + " parties");
            StringBuilder result = new StringBuilder();
            int senatorCount = 0;
            int partyCount = partySenators.length;
            for (int i = 0; i < partySenators.length; i++) senatorCount += partySenators[i];
            while (senatorCount > 0) {
                if (partyCount == 2) {
                    result.append(' ');
                    for (int i = 0; i < partySenators.length; i++) {
                        if (partySenators[i] > 0) {
                            partySenators[i]--;
                            senatorCount--;
                            if (partySenators[i] == 0) partyCount--;
                            result.append((char) (i + 'A'));
                        }
                    }
                } else {
                    int biggestParty = 0;
                    for (int i = 1; i < partySenators.length; i++) {
                        if (partySenators[i] > partySenators[biggestParty]) biggestParty = i;
                    }
                    partySenators[biggestParty]--;
                    senatorCount--;
                    if (partySenators[biggestParty] == 0) partyCount--;
                    result.append(' ').append((char) (biggestParty + 'A'));
                }
                if (DEBUG) {
                    int majority = (senatorCount + 1 + 1) / 2;
                    for (int i = 0; i < partySenators.length; i++) {
                        assert partySenators[i] < majority;
                    }
                }
            }
            return "Case #" + testNumber + ":" + result.toString();
        }
    }

    private static final boolean DEBUG = false;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {
        long beginTime = System.nanoTime();
        ExecutorService threadPool = Executors.newFixedThreadPool(DEBUG ? 1 : Runtime.getRuntime().availableProcessors());
        List<Test> tests = new ArrayList<>();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/practice/SenateEvacuation-1.in") : System.in;
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int partyCount = scanner.nextInt();
                int[] partySenators = new int[partyCount];
                for (int i = 0; i < partyCount; i++) partySenators[i] = scanner.nextInt();
                tests.add(new Test(testNumber, partySenators));
            }
        }
        List<Future<String>> results = threadPool.invokeAll(tests);
        for (Future<String> f : results) {
            System.out.println(f.get());
        }
        threadPool.shutdown();
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
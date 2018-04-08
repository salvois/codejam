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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Trouble Sort problem from Google Code Jam Qualification 2018.
 * https://codejam.withgoogle.com/2018/challenges/00000000000000cb/dashboard/00000000000079cb
 * Small dataset, multi-threaded version.
 * 
 * This one fooled me, and I got the large dataset wrong!
 * While just running the O(n^2) algorithm is fast enough for the small dataset,
 * I misread the limits for the large and assumed from my tests that the
 * same approach would suffice.
 * See TroubleSortLarge for Captain Hindsight's solution.
 * 
 * @author Salvo Isaja
 */
public class TroubleSortSmallMT {

    private static class Test implements Callable<String> {

        private final int testNumber;
        private final int[] values;
        
        public Test(int testNumber, int[] values) {
            this.testNumber = testNumber;
            this.values = values;
        }

        private static int solveBruteForce(int[] values) {
            while (true) {
                boolean done = true;
                for (int i = 0; i < values.length - 2; i++) {
                    if (values[i] > values[i + 2]) {
                        int t = values[i];
                        values[i] = values[i + 2];
                        values[i + 2] = t;
                        done = false;
                    }
                }
                if (done) break;
            }
            // Check if sorted
            for (int i = 0; i < values.length - 1; i++) {
                if (values[i] > values[i + 1]) return i;
            }
            return -1;
        }

        @Override
        public String call() {
            //System.err.println("Case #" + testNumber + ", " + values.length + " values, heap size=" + Runtime.getRuntime().totalMemory());
            int result = solveBruteForce(values);
            return "Case #" + testNumber + ": " + (result >= 0 ? result : "OK");
        }
    }

    private static final boolean DEBUG = true;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {
        long beginTime = System.nanoTime();
        ExecutorService threadPool = Executors.newFixedThreadPool(DEBUG ? 1 : Runtime.getRuntime().availableProcessors());
        List<Test> tests = new ArrayList<>();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/qualification/TroubleSort-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int valueCount = scanner.nextInt();
                int[] values = new int[valueCount];
                for (int i = 0; i < valueCount; i++) values[i] = scanner.nextInt();
                tests.add(new Test(testNumber, values));
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
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
 * The Number Guessing problem from Google Code Jam Practice Session 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000000130/dashboard
 * 
 * This is just a simple binary search problem.
 * Exchanging input and output with the judge is the novel aspect.
 * The limit of 30 on the total number of exchanges is a strong hint
 * to the algorithm to use (30 ~= log2 10^9).
 * 
 * @author Salvo Isaja
 */
public class NumberGuessing {

    private static final boolean DEBUG = true;

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = false ? new FileInputStream("resources/codejam2018/practice/NumberGuessing-1.in") : System.in;
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                int min = scanner.nextInt() + 1;
                int max = scanner.nextInt();
                int tryCount = scanner.nextInt();
                boolean done = false;
                for (int n = 1; n <= tryCount && !done; n++) {
                    int answer = min + (max - min) / 2;
                    System.out.println(answer);
                    System.out.flush();
                    String response = scanner.next();
                    switch (response) {
                        case "TOO_SMALL": min = answer + 1; break;
                        case "TOO_BIG": max = answer - 1; break;
                        case "CORRECT": System.err.println("Correct"); done = true; break;
                        default: System.err.println("Wrong"); done = true; break;
                    }
                }
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}

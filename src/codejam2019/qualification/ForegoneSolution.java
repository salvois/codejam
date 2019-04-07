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
package codejam2019.qualification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * The Foregone Solution problem from Google Code Jam Qualification 2019.
 * https://codingcompetitions.withgoogle.com/codejam/round/0000000000051705/0000000000088231
 * 
 * Just observe that any number can be written as in the following example:
 * 12439445 =
 * 12339335 +
 * 00100110
 * 
 * @author Salvo Isaja
 */
public class ForegoneSolution {

    private static final boolean DEBUG = true;
    
    private static class Pair {
        public String item1;
        public String item2;

        public Pair(String item1, String item2) {
            this.item1 = item1;
            this.item2 = item2;
        }
    }

    private static Pair solve(String input) {
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '4') {
                result1.append('3');
                result2.append('1');
            } else {
                result1.append(c);
                result2.append('0');
            }
        }
        int zeroCount = 0;
        while (zeroCount < result2.length() && result2.charAt(zeroCount) == '0') {
            zeroCount++;
        }
        return new Pair(result1.toString(), result2.substring(zeroCount));
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2019/qualification/ForegoneSolution-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                String input = scanner.next();
                Pair result = solve(input);
                System.out.println("Case #" + testNumber + ": " + result.item1 + " " + result.item2);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
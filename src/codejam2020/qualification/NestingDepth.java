/*
Solutions for Code Jam 2020.
Copyright 2020 Salvatore ISAJA. All rights reserved.

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
package codejam2020.qualification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * The Nesting Depth problem from Google Code Jam Qualification 2020.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000019fd27/0000000000209a9f
 * 
 * Just keep track of the current nesting depth (the count of open parentheses)
 * and fetch the next number. Close parentheses when the next number is less
 * than the current depth, open parentheses when it is greater.
 * 
 * @author Salvo Isaja
 */
public class NestingDepth {

    private static final boolean DEBUG = true;
    
    private static StringBuilder solve(String source) {
        StringBuilder result = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            int v = c - '0';
            if (v < depth) {
                for (int j = 0; j < depth - v; j++)
                    result.append(')');
            } else if (v > depth) {
                for (int j = 0; j < v - depth; j++)
                    result.append('(');
            }
            result.append(c);
            depth = v;
        }
        if (depth > 0) {
            for (int j = 0; j < depth; j++)
                result.append(')');
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2020/qualification/NestingDepth-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                String source = scanner.next();
                StringBuilder result = solve(source);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
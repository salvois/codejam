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
package codejam2018.round1c;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * A Whole New Word problem from Google Code Jam Round 1A 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007765/dashboard
 * Small dataset version.
 * 
 * This brute-force solution just tries all 26^2 possible words until
 * one new word is found.
 * 
 * @author Salvo Isaja
 */
public class AWholeNewWordSmall {

    private static final boolean DEBUG = true;

    private static String solve(String[] words) {
        Set<Character>[] letters = new Set[words[0].length()];
        for (int i = 0; i < words[0].length(); i++) {
            letters[i] = new HashSet<>();
        }
        Set<String> wordSet = new HashSet<>();
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                letters[i].add(word.charAt(i));
            }
            wordSet.add(word);
        }
        StringBuilder sb = new StringBuilder(words[0].length());
        for (char c : letters[0]) {
            sb.setLength(0);
            sb.append(c);
            if (letters.length > 1) {
                for (char d : letters[1]) {
                    sb.setLength(1);
                    sb.append(d);
                    String s = sb.toString();
                    if (!wordSet.contains(s)) return s;
                }
            } else {
                String s = sb.toString();
                if (!wordSet.contains(s)) return s;
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/round1c/AWholeNewWord-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int wordCount = scanner.nextInt();
                int length = scanner.nextInt();
                String[] words = new String[wordCount];
                for (int i = 0; i < wordCount; i++) words[i] = scanner.next();
                String result = solve(words);
                System.out.println("Case #" + testNumber + ": " + (result != null ? result : "-"));
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
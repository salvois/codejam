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
import java.util.Scanner;

/**
 * The Saving The Universe Again problem from Google Code Jam Qualification 2018.
 * https://codejam.withgoogle.com/2018/challenges/00000000000000cb/dashboard
 * 
 * Pretty straightforward O(n^2) algorithm, that just tries to push 'C's
 * to the right until the damage is low enough.
 * 
 * @author Salvo Isaja
 */
public class SavingTheUniverseAgain {

    private static final boolean DEBUG = true;

    private static int computeDamage(CharSequence program) {
        int beamStrength = 1;
        int damage = 0;
        for (int i = 0; i < program.length(); i++) {
            char c = program.charAt(i);
            if (c == 'S') {
                damage += beamStrength;
            } else if (c == 'C') {
                beamStrength *= 2;
            }
        }
        return damage;
    }

    private static int solve(int maxAllowedDamage, String program) {
        StringBuilder mutableProgram = new StringBuilder(program);
        int swapCount = 0;
        while (true) {
            int damage = computeDamage(mutableProgram);
            if (damage <= maxAllowedDamage) {
                return swapCount;
            }
            boolean swappable = false;
            boolean swapped = false;
            for (int i = mutableProgram.length() - 1; i >= 0; i--) {
                char c = mutableProgram.charAt(i);
                if (c == 'S') swappable = true;
                if (c == 'C' && swappable) {
                    mutableProgram.setCharAt(i, 'S');
                    mutableProgram.setCharAt(i + 1, 'C');
                    swapCount++;
                    swapped = true;
                    break;
                }
            }
            if (!swapped) {
                return -1;
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2018/qualification/SavingTheUniverseAgain-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int maxAllowedDamage = scanner.nextInt();
                String program = scanner.next();
                int swapCount = solve(maxAllowedDamage, program);
                System.out.println("Case #" + testNumber + ": " + (swapCount >= 0 ? swapCount : "IMPOSSIBLE"));
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
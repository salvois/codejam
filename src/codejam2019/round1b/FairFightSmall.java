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
package codejam2019.round1b;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * The Fair Fight problem from Google Code Jam Round-1B 2019.
 * https://codingcompetitions.withgoogle.com/codejam/round/0000000000051706/0000000000122838
 * Small dataset version.
 * 
 * Just scan all possible values of L and R and try, with cubic complexity.
 * 
 * @author Salvo Isaja
 */
public class FairFightSmall {

    private static final boolean DEBUG = true;
    
    private static int solve(int[] charlesSkills, int[] delilaSkills, int maxDiff) {
        int fairFightCount = 0;
        for (int l = 0; l < charlesSkills.length; l++) {
            for (int r = l; r < delilaSkills.length; r++) {
                int charlesScore = -1;
                int delilaScore = -1;
                for (int i = l; i <= r; i++) {
                    if (charlesSkills[i] > charlesScore) charlesScore = charlesSkills[i];
                    if (delilaSkills[i] > delilaScore) delilaScore = delilaSkills[i];
                }
                if (Math.abs(charlesScore - delilaScore) <= maxDiff) {
                    fairFightCount++;
                }
            }
        }
        return fairFightCount;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2019/round1b/FairFight-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int swordCount = scanner.nextInt();
                int maxDiff = scanner.nextInt();
                int[] charlesSkills = new int[swordCount];
                int[] delilaSkills = new int[swordCount];
                for (int i = 0; i < swordCount; i++) {
                    charlesSkills[i] = scanner.nextInt();
                }
                for (int i = 0; i < swordCount; i++) {
                    delilaSkills[i] = scanner.nextInt();
                }
                int result = solve(charlesSkills, delilaSkills, maxDiff);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
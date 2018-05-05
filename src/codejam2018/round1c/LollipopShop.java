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

import java.util.BitSet;
import java.util.Scanner;

/**
 * Lollipop Shop problem from Google Code Jam Round 1C 2018.
 * https://codejam.withgoogle.com/2018/challenges/0000000000007765/dashboard
 * 
 * This algorithm simply tries to give each customer a lollipop every time
 * there is one matching one of his preferences.
 * To satisfy as many customers as possible, we try to delay giving away
 * more popular lollipops.
 * Interestingly, this is judged wrong by the provided testing tool, but works
 * on the real judging system.
 * 
 * @author Salvo Isaja
 */
public class LollipopShop {

    public static void main(String[] args) {
        long beginTime = System.nanoTime();
        try (Scanner scanner = new Scanner(System.in)) {
            int testCount = scanner.nextInt();
            boolean wrong = false;
            for (int testNumber = 1; testNumber <= testCount && !wrong; testNumber++) {
                int customerCount = scanner.nextInt();
                if (customerCount < 0) {
                    wrong = true;
                    break;
                }
                BitSet lollipops = new BitSet(customerCount); // clear = not sold
                int[] popularities = new int[customerCount];
                for (int i = 0; i < customerCount; i++) {
                    int prefCount = scanner.nextInt();
                    if (prefCount < 0) {
                        wrong = true;
                        break;
                    }
                    int[] prefs = new int[prefCount];
                    for (int j = 0; j < prefCount; j++) {
                        int l = scanner.nextInt();
                        prefs[j] = l;
                        popularities[l]++;
                    }
                    int sold = -1;
                    for (int j = 0; j < prefCount; j++) {
                        int l = prefs[j];
                        if (lollipops.get(l) == false && (sold < 0 || popularities[l] < popularities[sold])) {
                            sold = l;
                        }
                    }
                    if (sold >= 0) lollipops.set(sold);
                    System.out.println(sold);
                    System.out.flush();
                }
            }
            if (wrong) {
                System.err.println("Wrong");
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}

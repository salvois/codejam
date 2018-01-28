/*
Solutions for Code Jam 2017.
Copyright 2017-2018 Salvatore ISAJA. All rights reserved.

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
package codejam2017.qualification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * The Tidy Numbers problem from Google Code Jam Qualification 2017.
 * https://code.google.com/codejam/contest/3264486/dashboard#s=p1
 *
 * Self-explanatory algorithm proportional to the count of digits of the number.
 * It goes like this: starting from 564379 -> 555555, 566666 (greater), 559999.
 * 
 * @author Salvo Isaja
 */
public class TidyNumbers {

    private static final boolean DEBUG = false;

    private long solve(long number) {
        char[] snumber = Long.toString(number).toCharArray();
        char[] sresult = new char[snumber.length];
        for (int i = 0; i < sresult.length; i++) {
            char c = snumber[i];
            for (int j = i; j < sresult.length; j++) sresult[j] = c;
            long result = Long.valueOf(new String(sresult));
            if (result > number) {
                c--;
                sresult[i] = c;
                for (int j = i + 1; j < sresult.length; j++) sresult[j] = '9';
                break;
            }
        }
        return Long.valueOf(new String(sresult));
    }
    
    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                long number = scanner.nextLong();
                long result = solve(number);
                System.out.println("Case #" + t + ": " + result);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        TidyNumbers tn = new TidyNumbers();
        tn.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/qualification/B-large-practice.in") : System.in);
        System.err.println("TidyNumbers done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

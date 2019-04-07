/*
Solutions for Code Jam 2019
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
 * The You Can Go Your Own Way problem from Google Code Jam Qualification 2019.
 * https://codingcompetitions.withgoogle.com/codejam/round/0000000000051705/00000000000881da
 * 
 * The maze is a square and we must basically walk a diagonal, so we can just
 * transpose Lydia's moves to be sure we end to the same destination as her
 * without doing any of her moves.
 * 
 * @author Salvo Isaja
 */
public class YouCanGoYourOwnWay {

    private static final boolean DEBUG = true;
    
    private static String solve(String lydiaSteps) {
        StringBuilder result = new StringBuilder(lydiaSteps.length());
        for (int i = 0; i < lydiaSteps.length(); i++) {
            char lydiaStep = lydiaSteps.charAt(i);
            result.append(lydiaStep == 'E' ? 'S' : 'E');
        }
        return result.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2019/qualification/YouCanGoYourOwnWay-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int mazeSize = scanner.nextInt();
                String lydiaSteps = scanner.next();
                String result = solve(lydiaSteps);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
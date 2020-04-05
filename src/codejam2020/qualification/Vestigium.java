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
import java.util.BitSet;
import java.util.Scanner;

/**
 * The Vestigium problem from Google Code Jam Qualification 2020.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000019fd27/000000000020993c
 * 
 * Computing the sum of the diagonal is trivial.
 * To check if the Latin Square property holds, check each row and each column
 * for duplicates, accumulating found numbers in a O(1)-lookup set.
 * 
 * @author Salvo Isaja
 */
public class Vestigium {

    private static final boolean DEBUG = true;
    
    private static void solve(int testNumber, int[][] matrix) {
        int trace = 0;
        for (int i = 0; i < matrix.length; i++) {
            trace += matrix[i][i];
        }
        int dupRowCount = 0;
        for (int i = 0; i < matrix.length; i++) {
            BitSet numbersPresent = new BitSet(matrix.length + 1);
            for (int j = 0; j < matrix.length; j++) {
                int v = matrix[i][j];
                if (numbersPresent.get(v)) {
                    dupRowCount++;
                    break;
                }
                numbersPresent.set(v);
            }
        }
        int dupColumnCount = 0;
        for (int j = 0; j < matrix.length; j++) {
            BitSet numbersPresent = new BitSet(matrix.length + 1);
            for (int i = 0; i < matrix.length; i++) {
                int v = matrix[i][j];
                if (numbersPresent.get(v)) {
                    dupColumnCount++;
                    break;
                }
                numbersPresent.set(v);
            }
        }
        System.out.println("Case #" + testNumber + ": " + trace + " " + dupRowCount + " " + dupColumnCount);
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2020/qualification/Vestigium-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int rowCount = scanner.nextInt();
                int[][] matrix = new int[rowCount][rowCount];
                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < rowCount; j++) {
                        matrix[i][j] = scanner.nextInt();
                    }
                }
                solve(testNumber, matrix);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
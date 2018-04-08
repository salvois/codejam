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

import java.util.BitSet;
import java.util.Scanner;

/**
 * The Go, Gopher! problem from Google Code Jam Qualification 2018.
 * https://codejam.withgoogle.com/2018/challenges/00000000000000cb/dashboard/0000000000007a30
 * 
 * This algorithm just tries to fill a 3-cell-tall strip from left to right.
 * The input value for the area to prepare can be ignored.
 * We insist on asking for the middle row, and for the leftmost unprepared cell,
 * until the judge says we are done.
 * 
 * @author Salvo Isaja
 */
public class GoGopher {

    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 9;
    private static final int MAX_WIDTH = 69;
    private static final int MAX_HEIGHT = 3;

    public static void main(String[] args) {
        long beginTime = System.nanoTime();
        try (Scanner scanner = new Scanner(System.in)) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int areaToPrepare = scanner.nextInt();
                BitSet[] bitmaps = new BitSet[MAX_HEIGHT];
                for (int i = 0; i < bitmaps.length; i++) bitmaps[i] = new BitSet(MAX_WIDTH);
                while (true) {
                    int x = Integer.MAX_VALUE;
                    for (int i = 0; i < bitmaps.length; i++) {
                        int xi = bitmaps[i].nextClearBit(0);
                        if (xi < x) x = xi;
                    }
                    if (x == 0) x = 1;
                    int y = 1;
                    System.out.format("%d %d\n", x + X_OFFSET, y + Y_OFFSET);
                    System.out.flush();
                    x = scanner.nextInt();
                    y = scanner.nextInt();
                    if (x == 0 && y == 0) {
                        System.err.println("Correct");
                        break;
                    } else if (x < 0 || y < 0) {
                        System.err.println("Wrong");
                        break;
                    }
                    bitmaps[y - Y_OFFSET].set(x - X_OFFSET);
                }
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}

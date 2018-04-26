/*
Solutions for Code Jam 2017.
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
package codejam2017.round1a;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Alphabet Cake problem from Round 1A of Code Jam 2017.
 * https://code.google.com/codejam/contest/5304486/dashboard#s=p0
 *
 * We scan the grid by rows, from top to bottom. If there is at least one letter
 * in a row, we expand it as much as possible (up to edges or another letter)
 * horizontally, then vertically.
 * 
 * .A.C..           AAA  CCC
 * ..B.E.           BBBB  EE
 * ......  becomes  BBBB  EE
 * G.H.I.           GG HH II
 * .J..K.           JJJJ  KK
 * L.....           LLLL  KK
 * 
 * @author Salvo Isaja
 */
public class AlphabetCake {

    private static class Grid {
        public final int rowCount;
        public final int columnCount;
        public final char[] grid;

        public Grid(int rowCount, int columnCount, char[] grid) {
            this.rowCount = rowCount;
            this.columnCount = columnCount;
            this.grid = grid;
        }

        char get(int y, int x) {
            return grid[y * columnCount + x];
        }

        void set(int y, int x, char c) {
            grid[y * columnCount + x] = c;
        }
    }

    private static class Letter {
        public final char c;
        public final int x;
        public final int y;

        public Letter(char c, int x, int y) {
            this.c = c;
            this.x = x;
            this.y = y;
        }
    }

    private static final boolean DEBUG = false;
    private final Grid grid;
    private final List<Letter> letters;

    private AlphabetCake(Grid grid) {
        this.grid = grid;
        letters = new ArrayList<>();
        for (int y = 0; y < grid.rowCount; y++) {
            for (int x = 0; x < grid.columnCount; x++) {
                char c = grid.get(y, x);
                if (c != '?') {
                    letters.add(new Letter(c, x, y));
                }
            }
        }
    }

    private void expand(Letter letter) {
        int xMin = letter.x;
        int xMax = letter.x;
        for (int x = letter.x - 1; x >= 0; x--) {
            char c = grid.get(letter.y, x);
            if (c != '?') break;
            grid.set(letter.y, x, letter.c);
            xMin = x;
        }
        for (int x = letter.x + 1; x < grid.columnCount; x++) {
            char c = grid.get(letter.y, x);
            if (c != '?') break;
            grid.set(letter.y, x, letter.c);
            xMax = x;
        }
        for (int y = letter.y - 1; y >= 0; y--) {
            boolean empty = true;
            for (int x = xMin; x <= xMax; x++) {
                if (grid.get(y, x) != '?') {
                    empty = false;
                    break;
                }
            }
            if (!empty) break;
            for (int x = xMin; x <= xMax; x++) {
                grid.set(y, x, letter.c);
            }
        }
        for (int y = letter.y + 1; y < grid.rowCount; y++) {
            boolean empty = true;
            for (int x = xMin; x <= xMax; x++) {
                if (grid.get(y, x) != '?') {
                    empty = false;
                    break;
                }
            }
            if (!empty) break;
            for (int x = xMin; x <= xMax; x++) {
                grid.set(y, x, letter.c);
            }
        }
    }

    public void solve() {
        for (Letter letter : letters) expand(letter);
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1a/A-large-practice.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int rowCount = scanner.nextInt();
                int columnCount = scanner.nextInt();
                char[] g = new char[rowCount * columnCount];
                Grid grid = new Grid(rowCount, columnCount, g);
                for (int y = 0; y < rowCount; y++) {
                    String s = scanner.next();
                    for (int x = 0; x < columnCount; x++) grid.set(y, x, s.charAt(x));
                }
                AlphabetCake test = new AlphabetCake(grid);
                test.solve();
                System.out.println("Case #" + testNumber + ":");
                for (int y = 0; y < rowCount; y++) {
                    for (int x = 0; x < columnCount; x++) System.out.print(test.grid.get(y, x));
                    System.out.println();
                }
            }
        }
        System.err.println("AlphabetCake done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}

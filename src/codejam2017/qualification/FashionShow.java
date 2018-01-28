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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

/**
 * The Fashion Show problem from Google Code Jam Qualification 2017.
 * https://code.google.com/codejam/contest/3264486/dashboard#s=p3
 *
 * First, the layout of the stage. If n is the side length, there are:
 * - n rows, numbered from 0 to n-1 from top to bottom;
 * - n columns, numbered from 0 to n-1 from left to right;
 * - 2n-1 antidiagonals, shaped like '/', and numbered following an 'L',
 *   0 being the one on top-left, n-1 being the main antidiagonal,
 *   2n-2 being the one on bottom-right;
 *   the index of an antidiagonal equals to y + x;
 * - 2n-1 diagonals, shaped like '\', and numbered following an upside-down 'L',
 *   0 being the one on bottom-left, n-1 being the main diagonal,
 *   2n-2 being the one on top-right;
 *   the index of a diagonal equals to n - 1 - y + x.
 *
 * From the constraints we can observe the following:
 * - we can add as many '+'s to any row and column as long as there is not
 *   another '+' in the antidiagonal and the diagonal intersecting the cell;
 * - we can add as many 'x's to any antidiagonal and diagonal as long as there
 *   is not another 'x' in the row and in the column intersecting the cell;
 * - there can be only one 'o' in each row, column, antidiagonal or diagonal.
 *
 * The first tricky aspect was realizing that an 'o' is actually the
 * superposition of a '+' and an 'x', both as constraints and score.
 * Thus, we can place '+'s and 'x's independently, and call them 'o' when
 * they occupy the same cell. This greatly simplifies the process, letting
 * us not consider model upgrades.
 * 
 * If there are no initial models, in the optimal case it is possible to place
 * n 'x's (one for each distinct row and column) and 2n-1 '+'s (one for each
 * distinct antidiagonal and diagonal), thus forming a 'Z' like pattern
 * (and its rotations, of course):
 * +++++o
 * ....x.
 * ...x..
 * ..x...
 * .x....
 * x++++.
 *
 * We are basically free to place 'x's wherever we want, as long as there is
 * one per row and column, but whenever we place a '+' in a row that is not
 * the first or the last we occupy more diagonals, thus reducing the number
 * of '+'s that we can place.
 * 
 * The second tricky aspect was understanding how to place '+'s if there are
 * already some on the stage. We can observe that, in the optimal case,
 * we placed a '+' on each antidiagonal, provided that they do not share
 * the same diagonal. Thus, we can fill our stage by sweeping antidiagonals,
 * starting from those sharing less diagonals with other antidiagonals,
 * that is the shorter ones: top-left (index 0) and bottom-right (index 2n-2).
 * We place a '+' in a cell not intersecting an already occupied diagonal,
 * then move to the next longer antidiagonals, at index 1 and 2n-3, and so on,
 * until we reach the main antidiagonal.
 * That was tough!
 * 
 * @author Salvo Isaja
 */
public class FashionShow {

    private static class Model {
        final int x; // 0 based
        final int y; // 0 based
        char type; // we may upgrade a '+' or 'x' to an 'o'
        boolean initial; // we may upgrade a '+' or 'x' to an 'o'

        public Model(int x, int y, char type, boolean initial) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.initial = initial;
        }

        public int score() {
            switch (type) {
                case '+':
                case 'x': return 1;
                case 'o': return 2;
                default: throw new IllegalStateException("Invalid model type: " + type);
            }
        }
    }

    private static class Stage {
        public final List<Model> models;
        public final int sideLength;

        public Stage(List<Model> initialModels, int sideLength) {
            this.models = initialModels;
            this.sideLength = sideLength;
        }

        private void addModel(int x, int y, char type) {
            for (Model o : models) {
                if (o.x == x && o.y == y) {
                    assert type == '+' && o.type == 'x' || type == 'x' && o.type == '+';
                    o.type = 'o';
                    o.initial = false;
                    return;
                }
            }
            models.add(new Model(x, y, type, false));
        }

        public void fill() {
            BitSet occupiedRows = new BitSet(sideLength);
            BitSet occupiedColumns = new BitSet(sideLength);
            BitSet occupiedAntidiagonals = new BitSet(2 * sideLength - 1);
            BitSet occupiedDiagonals = new BitSet(2 * sideLength - 1);
            // Check initial models
            for (Model m : models) {
                if (m.type == '+' || m.type == 'o') {
                    occupiedAntidiagonals.set(m.y + m.x);
                    occupiedDiagonals.set(sideLength - 1 - m.y + m.x);
                }
                if (m.type == 'x' || m.type == 'o') {
                    occupiedRows.set(m.y);
                    occupiedColumns.set(m.x);
                }
            }
            // Fill rows and columns with 'x's
            for (int y = occupiedRows.nextClearBit(0); y < sideLength; y = occupiedRows.nextClearBit(y + 1)) {
                for (int x = occupiedColumns.nextClearBit(0); x < sideLength; x = occupiedColumns.nextClearBit(x + 1)) {
                    addModel(x, y, 'x');
                    occupiedRows.set(y);
                    occupiedColumns.set(x);
                    break;
                }
            }
            // Fill antidiagonals of increasing lengths with '+'s, sweeping
            // from top-left and bottom-right towards the main antidiagonal
            // (e.g. for sideLength=6 -> 0, 10, 1, 9, 2, 8, 3, 7, 4, 6, 5)
            for (int a = 0; ; ) {
                if (!occupiedAntidiagonals.get(a)) {
                    int y = Math.min(a, sideLength - 1);
                    int xLast = y;
                    for (int x = a - y; x <= xLast; x++, y--) {
                        int d = sideLength - 1 - y + x;
                        if (!occupiedDiagonals.get(d)) {
                            addModel(x, y, '+');
                            occupiedDiagonals.set(d);
                            occupiedAntidiagonals.set(a);
                            break;
                        }
                    }
                }
                if (a == sideLength - 1) break;
                if (a < sideLength) {
                    a = 2 * sideLength - 2 - a;
                } else {
                    a = 2 * sideLength - 2 - a;
                    a++;
                }
            }
        }

        private void print() {
            char[] grid = new char[sideLength * sideLength];
            Arrays.fill(grid, '.');
            for (Model m : models) grid[m.y * sideLength + m.x] = m.type;
            for (int y = 0; y < sideLength; y++) {
                for (int x = 0; x < sideLength; x++) {
                    char c = grid[y * sideLength + x];
                    System.out.print(c);
                }
                System.out.println();
            }
        }
    }

    private static final boolean DEBUG = false;
    
    private void printResult(int testIndex, Stage stage) {
        int score = 0;
        int extraModelCount = 0;
        for (Model m : stage.models) {
            score += m.score();
            if (!m.initial) extraModelCount++;
        }
        System.out.println("Case #" + testIndex + ": " + score + " " + extraModelCount);
        for (Model m : stage.models) {
            if (!m.initial) System.out.println(m.type + " " + (m.y + 1) + " " + (m.x + 1));
        }
    }

    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                int sideLength = scanner.nextInt();
                int initialModelCount = scanner.nextInt();
                List<Model> initialModels = new ArrayList<>();
                for (int i = 0; i < initialModelCount; i++) {
                    char c = scanner.next().charAt(0);
                    int y = scanner.nextInt();
                    int x = scanner.nextInt();
                    initialModels.add(new Model(x - 1, y - 1, c, true));
                }
                Stage stage = new Stage(initialModels, sideLength);
                stage.fill();
                printResult(t, stage);
                if (DEBUG) stage.print();
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        FashionShow tn = new FashionShow();
        tn.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/qualification/D-large-practice.in") : System.in);
        System.err.println("FashionShow done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

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
package codejam2017.finals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 * The Dice Straight problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p0
 *
 * The basic idea of the algorithm is to put all values on the faces of all dice
 * in a list sorted by value, storing the set of containing dice with each value.
 * We then scan values until consecutive values are present, and until we can
 * use a different die for each value.
 * When no free die is found for a value, we try to recursively shuffle the dice
 * already used for other values so that we can free a die for the new value.
 * The rest is just optimization, such as early exiting when the longest possible
 * straight is found, or avoiding to scan already scanned sequences, whose
 * values already have a die assigned to.
 *
 * Being n the number of dice, and f the number of faces, the algorithm is
 * linear with the number of possible values (at most n*f), but for each value
 * scanned a recursive shuffle may be needed, giving worst case exponential
 * complexity. Fortunately the larger the number of dice, the higher the
 * probability to find an unused die to replace one used by a previous value.
 * 
 * Note: the large test dataset needs deep recursion, thus a large stack is needed (eg. -Xss16m).
 *
 * @author Salvo Isaja
 */
public class DiceStraight {

    private static final int FACE_COUNT = 6;
    
    private static class Die {
        int id; // for debugging purposes only
        int[] values = new int[FACE_COUNT];
        DieValue valueUsing; // the one value using this die, or null if this die is not used
        Die(int id) { this.id = id; }
    }

    private static class DieValue {
        int value;
        List<Die> dice; // Set of dice containing this value

        DieValue(int value, List<Die> dice) {
            this.value = value;
            this.dice = dice;
        }

        /** Returns the first unused die among the ones containing this value, or null if not found. */
        Die findUnusedDie() {
            for (Die die : dice) {
                if (die.valueUsing == null) return die;
            }
            return null;
        }

        /** If no die is used by this value, use the first die among the ones containing this value. */
        void assignDie() {
            for (Die die : dice) {
                if (die.valueUsing != null) return;
            }
            dice.get(0).valueUsing = this;
        }

        /** If a die is used by this value, unassign it. */
        void unassignDie() {
            for (Die die : dice) {
                if (die.valueUsing == this) {
                    die.valueUsing = null;
                    break;
                }
            }
        }
    }

    private static class Test {
        List<Die> dice = new ArrayList<>();
        List<DieValue> dieValues; // Set of distinct values on the face of all dice, sorted by value
        Set<Die> diceVisitedWhileShuffling; // To skip already visited dice when recursively shuffling

        /** Populates the dieValues list from the list of dice. */
        void prepareValues() {
            // We don't have particular memory constrains, let's abuse a TreeMap to get sorted unique values
            Map<Integer, List<Die>> valueMap = new TreeMap<>();
            for (Die die : dice) {
                for (int v : die.values) {
                    List<Die> ds = valueMap.get(v);
                    if (ds == null) valueMap.put(v, new ArrayList<>(Arrays.asList(die)));
                    else ds.add(die);
                }
            }
            // But an ArrayList is much better for iteration later
            dieValues = new ArrayList<>(valueMap.size());
            valueMap.forEach((v, ds) -> dieValues.add(new DieValue(v, ds)));
        }

        /**
         * Attempt to recursively free a die by selecting a different die for the same value.
         * @return true if the die has been freed, false if no other die can be found.
         */
        boolean freeByShuffling(Die die) {
            assert die.valueUsing != null;
            // First check if we can just use another dice for the previous value
            for (Die otherDie : die.valueUsing.dice) {
                if (otherDie.valueUsing == null) {
                    otherDie.valueUsing = die.valueUsing;
                    die.valueUsing = null;
                    return true;
                }
            }
            // Nope, we must free a die recursively
            diceVisitedWhileShuffling.add(die);
            for (Die otherDie : die.valueUsing.dice) {
                if (diceVisitedWhileShuffling.contains(otherDie)) continue;
                if (freeByShuffling(otherDie)) {
                    otherDie.valueUsing = die.valueUsing;
                    die.valueUsing = null;
                    return true;
                }
            }
            return false;
        }

        /**
         * Attempt to find an unused die for the specified value by possibly shuffling other already used dice.
         * @return A now-unused die, or null if no shuffling results in an unused die.
         */
        Die findUnusedByShuffling(DieValue dv) {
            diceVisitedWhileShuffling = new HashSet<>();
            for (Die die : dv.dice) {
                if (freeByShuffling(die)) return die;
            }
            return null;
        }

        /** Finds the maximum length of dice with consecutive values. */
        int findLongestStraight() {
            int maxLength = 1;
            int endIndex = 0;
            for (int beginIndex = 0; beginIndex < dieValues.size() - 1; ) {
                DieValue dv = dieValues.get(beginIndex);
                debugPrint("  Sequence starting from #" + beginIndex + ": " + dv.value + "... ");
                dv.assignDie();
                int beginValue = dieValues.get(beginIndex).value;
                int maxPossibleLength = Math.min(dieValues.size() - beginIndex, dice.size());
                int nextBeginIndex = beginIndex + 1;
                if (endIndex <= beginIndex) endIndex = beginIndex + 1;
                while (endIndex < dieValues.size()) {
                    dv = dieValues.get(endIndex);
                    debugPrint("    Comparing against #" + endIndex + ": " + dv.value + "... ");
                    if (dv.value != beginValue + endIndex - beginIndex) {
                        // Not a consecutive value, restart scanning from there
                        nextBeginIndex = endIndex;
                        break;
                    }
                    Die die = dv.findUnusedDie();
                    if (die == null) die = findUnusedByShuffling(dv);
                    if (die == null) break;
                    die.valueUsing = dv;
                    endIndex++;
                    if (endIndex - beginIndex > maxLength) maxLength = endIndex - beginIndex;
                    if (maxLength == maxPossibleLength) break;
                }
                debugPrint("length " + (endIndex - beginIndex) + " max " + maxLength);
                if (maxLength == maxPossibleLength) break;
                // The old values at the beginning are no longer useful, free their dice
                for (; beginIndex < nextBeginIndex; beginIndex++) {
                    dieValues.get(beginIndex).unassignDie();
                }
            }
            return maxLength;
        }

        void print() {
            if (DEBUG) {
                for (DieValue dv : dieValues) {
                    System.out.print(dv.value);
                    for (Die die : dv.dice) {
                        System.out.print("\t" + die.id);
                    }
                    System.out.println();
                }
            }
        }
    }

    private static final boolean DEBUG = false;
    private final List<Test> tests = new ArrayList<>();

    private static void debugPrint(String s) {
        if (DEBUG) System.out.println(s);
    }

    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 0; t < testCount; t++) {
                Test test = new Test();
                int dieCount = scanner.nextInt();
                for (int d = 0; d < dieCount; d++) {
                    Die die = new Die(d + 1);
                    for (int i = 0; i < FACE_COUNT; i++) {
                        int faceValue = scanner.nextInt();
                        die.values[i] = faceValue;
                    }
                    test.dice.add(die);
                }
                tests.add(test);
            }
        }
    }

    private void runTests() {
        int caseIndex = 1;
        for (Test test : tests) {
            test.prepareValues();
            test.print();
            debugPrint("Case #" + caseIndex + " has " + test.dice.size() + " dice and " + test.dieValues.size() + " values.");
            int length = test.findLongestStraight();
            System.out.println("Case #" + caseIndex + ": " + length);
            caseIndex++;
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        DiceStraight ds = new DiceStraight();
        ds.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/A-large-practice.in") : System.in);
        ds.runTests();
        System.err.println("DiceStraight done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

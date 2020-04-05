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

import java.util.BitSet;
import java.util.Scanner;

/**
 * The ESAb ATAd problem from Google Code Jam Qualification 2020.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000019fd27/0000000000209a9e
 * 
 * The algorithm queries bits in pairs, one from both end of the array,
 * in order to discriminate between bit flips and reversals.
 * 
 * If bitset[i] != bitset[n - i - 1], a combination of bit flip and reversal produces no change.
 * Let's call oppositeBit the first bit we find that satisfies this property.
 * If bitset[i] == bitset[n - i - 1], a reversal produces no change.
 * Let's call specularBit the first bit we find that satisfies this property.
 * 
 * When a quantum fluctuation occurs:
 * - if we detect that oppositeBit has changed, the bitset has either been bit flipped or reversed;
 * - if we detect that specularBit has changed, the bitset has either been bit flipped or both bit flipped and reversed.
 * Thus if after 10 queries we query both oppositeBit and specularBit we can
 * detect the exact fluctuation that has occurred:
 * - oppositeBit unchanged, specularBit unchanged: no change
 * - oppositeBit unchanged, specularBit changed: both bit flip and reversal
 * - oppositeBit changed, specularBit unchanged: reversal
 * - oppositeBit changed, specularBit changed: bit flip
 * 
 * @author Salvo Isaja
 */
public class EsabAtad {

    private static String stringify(BitSet bitset, int bitCount) {
        StringBuilder sb = new StringBuilder(bitCount);
        for (int i = 0; i < bitCount; i++) {
            sb.append(bitset.get(i) ? '1' : '0');
        }
        return sb.toString();
    }
    
    private static BitSet flip(BitSet bitset, int bitCount) {
        BitSet result = new BitSet(bitCount);
        for (int i = 0; i < bitCount; i++) {
            result.set(i, !bitset.get(i));
        }
        return result;
    }

    private static BitSet reverse(BitSet bitset, int bitCount) {
        BitSet result = new BitSet(bitCount);
        for (int i = 0; i < bitCount; i++) {
            result.set(bitCount - i - 1, bitset.get(i));
        }
        return result;
    }
    
    private static boolean readBit(Scanner scanner, int bitIndex) {
        System.out.println(bitIndex + 1);
        System.out.flush();
        return scanner.next().charAt(0) == '1';
    }

    private static boolean runTest(int bitCount, Scanner scanner) {
        BitSet bitset = new BitSet(bitCount);
        int oppositeBitIndex = -1;
        int specularBitIndex = -1;
        int bitIndex = 0;
        int queryIndex = 0;
        while (bitIndex < bitCount / 2) {
            if (queryIndex > 0 && queryIndex % 10 == 0) {
                boolean oppositeChanged = false;
                boolean specularChanged = false;
                if (oppositeBitIndex >= 0) {
                    boolean bit = readBit(scanner, oppositeBitIndex);
                    oppositeChanged = bit != bitset.get(oppositeBitIndex);
                } else {
                    readBit(scanner, 0);
                }
                if (specularBitIndex >= 0) {
                    boolean bit = readBit(scanner, specularBitIndex);
                    specularChanged = bit != bitset.get(specularBitIndex);
                } else {
                    readBit(scanner, 0);
                }
                if (oppositeChanged && !specularChanged) {
                    bitset = reverse(bitset, bitCount);
                } else if (!oppositeChanged && specularChanged) {
                    bitset = flip(reverse(bitset, bitCount), bitCount);
                } else if (oppositeChanged && specularChanged) {
                    bitset = flip(bitset, bitCount);
                }
            } else {
                boolean leftBit = readBit(scanner, bitIndex);
                boolean rightBit = readBit(scanner, bitCount - bitIndex - 1);
                if (leftBit != rightBit && oppositeBitIndex < 0)
                    oppositeBitIndex = bitIndex;
                if (leftBit == rightBit && specularBitIndex < 0)
                    specularBitIndex = bitIndex;
                bitset.set(bitIndex, leftBit);
                bitset.set(bitCount - bitIndex - 1, rightBit);
                bitIndex++;
            }
            queryIndex += 2;
        }
        String stringResult = stringify(bitset, bitCount);
        System.out.println(stringResult);
        System.out.flush();
        char response = scanner.next().charAt(0);
        if (response == 'Y') {
            System.err.println("Correct: " + stringResult);
            return true;
        } else {
            System.err.println("Wrong: " + stringResult);
            return false;
        }
    }
        
    public static void main(String[] args) {
        long beginTime = System.nanoTime();
        try (Scanner scanner = new Scanner(System.in)) {
            int testCount = scanner.nextInt();
            int bitCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                if (!runTest(bitCount, scanner)) break;
            }
        }
        System.err.println( "Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
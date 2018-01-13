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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The Operation problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p1
 * Genetic algorithm version.
 * 
 * This was my first attempt to tackle the problem. Even for the small dataset,
 * the number of permutations was ridiculously high to allow brute force,
 * and I was confident that I could just get rid of negative terms early
 * and do all multiplications at the end.
 * 
 * To try to see some real sequences solving the problem, I've set up this
 * genetic algorithm. It starts with a population of randomly sorted cards,
 * then takes the half with the higher results and randomly mutates them to
 * try to create sequences with even higher results.
 * 
 * It turns out it converges really well, at least on the small dataset,
 * but sticks on local optima in a few cases (that are not repeatable
 * among runs). Nevertheless it was fun and informative, especially for the
 * discovery of the sign inverting factor used in my final solution
 * (see Operation).
 * 
 * @author Salvo Isaja
 */
public class OperationGenetic {

    private static class Card {
        public final int id; // for debugging purposes only
        public final char op;
        public final BigInteger value;

        public Card(int id, char op, BigInteger value) {
            this.id = id;
            this.op = op;
            this.value = value;
        }
    }

    private static class Rational {
        public final BigInteger num;
        public final BigInteger den;
        public final double doubleValue;

        public Rational(BigInteger num) {
            this.num = num;
            den = BigInteger.ONE;
            doubleValue = num.doubleValue();
        }

        public Rational(BigInteger num, BigInteger den) {
            this.num = num;
            this.den = den;
            doubleValue = num.doubleValue() / den.doubleValue();
        }
    }

    private static class Sequence implements Comparable {
        
        private static final int MAX_MUTATIONS_PERCENT = 33;
        private static Random random = new SecureRandom();
        BigInteger initialValue;
        Card[] cards;
        Rational result;

        Sequence(BigInteger initialValue, Card[] cards) {
            this.initialValue = initialValue;
            this.cards = cards;
            computeResult();
        }

        private void swapCards(int i, int j) {
            Card c = cards[i];
            cards[i] = cards[j];
            cards[j] = c;
        }

        private void computeResult() {
            BigInteger num = initialValue;
            BigInteger den = BigInteger.ONE;
            result = new Rational(initialValue);
            for (Card card : cards) {
                BigInteger v = card.value;
                switch (card.op) {
                    case '+':
                        v = v.multiply(den);
                        num = num.add(v);
                        break;
                    case '-':
                        v = v.multiply(den);
                        num = num.subtract(v);
                        break;
                    case '*':
                        num = num.multiply(v);
                        break;
                    case '/':
                        den = den.multiply(v);
                        break;
                    default:
                        throw new UnsupportedOperationException("Invalid operator " + card.op);
                }
                v = num.gcd(den);
                if (den.compareTo(BigInteger.ZERO) < 0) v = v.negate();
                num = num.divide(v);
                den = den.divide(v);
            }
            result = new Rational(num, den);
        }

        public Sequence mutate() {
            Card[] newCards = Arrays.copyOf(cards, cards.length);
            Sequence s = new Sequence(initialValue, newCards);
            int count = random.nextInt(Math.max(s.cards.length * MAX_MUTATIONS_PERCENT / 100, 1));
            if (count < 1) count = 1;
            for (int i = 0; i < count; i++) {
                int from = random.nextInt(s.cards.length);
                int to;
                do {
                    to = random.nextInt(s.cards.length);
                } while (to == from);
                s.swapCards(from, to);
            }
            s.computeResult();
            return s;
        }

        /**
         * Fisher–Yates shuffle algorithm to create a random sequence of cards.
         * Courtesy of https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
         */
        public Sequence createShuffled() {
            Card[] newCards = Arrays.copyOf(cards, cards.length);
            Sequence s = new Sequence(initialValue, newCards);
            for (int i = newCards.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                s.swapCards(i, j);
            }
            s.computeResult();
            return s;
        }

        public void print() {
            if (DEBUG) {
                System.out.println("  " + initialValue);
                for (Card card : cards) System.out.println("  " + card.op + " " + card.value);
                System.out.println("  " + result.num + " " + result.den + " (" + result.doubleValue + ")");
            }
        }

        @Override
        public int compareTo(Object o) {
            return -Double.compare(result.doubleValue, ((Sequence) o).result.doubleValue);
        }
    }

    private static class Test {

        static final int MAX_STABILITY_COUNT = 100;
        Sequence[] sequences;

        Test(BigInteger initialValue, Card[] cards) {
            sequences = new Sequence[cards.length * 100];
            sequences[0] = new Sequence(initialValue, cards);
            for (int i = 1; i < sequences.length; i++) sequences[i] = sequences[0].createShuffled();
            Arrays.sort(sequences);
        }

        Rational findGenetic() {
            Rational result = sequences[0].result;
            Sequence savedSequence = new Sequence(sequences[0].initialValue, Arrays.copyOf(sequences[0].cards, sequences[0].cards.length));
            if (sequences[0].cards.length == 1) return result;
            int stabilityCount = 0;
            while (stabilityCount < MAX_STABILITY_COUNT) {
                for (int i = 0; i < sequences.length / 2; i++) {
                    sequences[i + sequences.length / 2] = sequences[i].mutate();
                }
                Arrays.sort(sequences);
                if (sequences[0].result.doubleValue > result.doubleValue) {
                    result = sequences[0].result;
                    savedSequence = new Sequence(sequences[0].initialValue, Arrays.copyOf(sequences[0].cards, sequences[0].cards.length));
                    stabilityCount = 0;
                } else {
                    stabilityCount++;
                }
            }
            savedSequence.print();
            return result;
        }
    }

    private static final boolean DEBUG = true;
    private final List<Test> tests = new ArrayList<>();

    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 0; t < testCount; t++) {
                BigInteger initialValue = scanner.nextBigInteger();
                int cardCount = scanner.nextInt();
                Card[] cards = new Card[cardCount];
                for (int i = 0; i < cardCount; i++) {
                    char op = scanner.next().charAt(0);
                    BigInteger value = scanner.nextBigInteger();
                    Card card = new Card(i + 1, op, value);
                    cards[i] = card;
                }
                Test test = new Test(initialValue, cards);
                tests.add(test);
            }
        }
    }

    private void runTests() {
        int caseIndex = 1;
        for (Test test : tests) {
            Rational result = test.findGenetic();
            System.out.println("Case #" + caseIndex + ": " + result.num + " " +result.den);
            caseIndex++;
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        OperationGenetic operation = new OperationGenetic();
        operation.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/B-small-practice.in") : System.in);
        operation.runTests();
    }
}

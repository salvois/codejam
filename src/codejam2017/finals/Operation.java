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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Operation problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p1
 * O(n) version.
 * 
 * This is my attempt to find an efficient solution, after trying a genetic
 * approach (see OperationGenetic) that sometimes got stuck on local optima.
 * 
 * Given the sequence of cards, to maximize the result we can consider
 * the following opportunities:
 * - get rid of zero multipliers as early as possible
 * - get rid of divisions as early as possible
 * - maximize positive terms and minimize negative terms
 * - see if we can change the sign of terms (i.e. there is at least
 *   a negative multiplier or divider) to maximize the previous step
 * - multiply as late as possible
 *
 * Thus we create the following groups of cards expressed as rational numbers:
 * - positive term, as the sum of all cards adding a positive value or subtracting a negative value
 * - negative term, as the sum of all cards adding a negative value or subtracting a positive value
 * - a sign inverting factor in case we need to change sign of a group, selected
 *   among cards that either multiply or divide by the largest negative factor
 *   (to change the accumulated value as little as possible)
 * - multiplier, as the product of the value of all multiplication cards
 *   except the one taken for the sign inverting factor, if any
 * - divider, as the product of the value of all division cards except
 *   the one taken for the sign inverting factor, if any.
 * 
 * Keeping the divider (that is the future denominator) positive for the sake of simplicity:
 * - if the multiplier is positive, maximize the positive term and invert
 *   the sign of the negative term if possible
 * - if the multiplier is negative, maximize the negative term and invert
 *   the sign of the positive term if possible
 * - if sign inversion is possible, it is advantageous to do it before maximizing
 *   the useful term, otherwise it is best to get rid of the term as early
 *   as possible.
 * 
 * The algorithm scans all cards once, then evaluates the highest possible
 * result in constant time, thus it is O(n).
 * 
 * @author Salvo Isaja
 */
public class Operation {

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

    private static class Rational implements Comparable {

        public static final Rational ZERO = new Rational(BigInteger.ZERO);
        public static final Rational ONE = new Rational(BigInteger.ONE);

        public final BigInteger num;
        public final BigInteger den;

        public static Rational min(Rational a, Rational b) {
            return a.compareTo(b) < 0 ? a : b;
        }

        public static Rational max(Rational a, Rational b) {
            return a.compareTo(b) > 0 ? a : b;
        }
        
        public Rational(BigInteger num) {
            this.num = num;
            den = BigInteger.ONE;
        }

        public Rational(BigInteger num, BigInteger den) {
            this.num = num;
            this.den = den;
        }
        
        public Rational add(Rational other) {
            BigInteger n = this.num.multiply(other.den).add(other.num.multiply(this.den));
            BigInteger d = this.den.multiply(other.den);
            return new Rational(n, d);
        }

        public Rational subtract(Rational other) {
            BigInteger n = this.num.multiply(other.den).subtract(other.num.multiply(this.den));
            BigInteger d = this.den.multiply(other.den);
            return new Rational(n, d);
        }

        public Rational multiply(Rational other) {
            BigInteger n = this.num.multiply(other.num);
            BigInteger d = this.den.multiply(other.den);
            return new Rational(n, d);
        }

        public Rational divide(Rational other) {
            BigInteger n = this.num.multiply(other.den);
            BigInteger d = this.den.multiply(other.num);
            return new Rational(n, d);
        }

        public Rational normalize() {
            BigInteger v = num.gcd(den);
            if (den.compareTo(BigInteger.ZERO) < 0) v = v.negate();
            return new Rational(num.divide(v), den.divide(v));
        }

        @Override
        public int compareTo(Object o) {
            Rational other = (Rational) o;
            return this.num.multiply(other.den).compareTo(other.num.multiply(this.den));
        }

        @Override
        public String toString() {
            return num.toString() + "/" + den.toString();
        }
    }

    private static class Test {

        Rational initialValue;
        Card[] cards;

        Test(BigInteger initialValue, Card[] cards) {
            this.initialValue = new Rational(initialValue);
            this.cards = cards;
        }

        Rational findMaximum() {
            Rational positiveTerm = Rational.ZERO;
            Rational negativeTerm = Rational.ZERO;
            Rational multiplier = Rational.ONE;
            Rational divider = Rational.ONE;
            Rational mulSignInverter = null;
            Rational divSignInverter = null;
            boolean zeroFactor = false;
            for (Card card : cards) {
                int zeroCmp = card.value.compareTo(BigInteger.ZERO);
                switch (card.op) {
                    case '+':
                        if (zeroCmp > 0) positiveTerm = positiveTerm.add(new Rational(card.value));
                        else negativeTerm = negativeTerm.add(new Rational(card.value));
                        break;
                    case '-':
                        if (zeroCmp < 0) positiveTerm = positiveTerm.subtract(new Rational(card.value));
                        else negativeTerm = negativeTerm.subtract(new Rational(card.value));
                        break;
                    case '*':
                        if (zeroCmp > 0) {
                            multiplier = multiplier.multiply(new Rational(card.value));
                        } else if (zeroCmp < 0) {
                            Rational r = new Rational(card.value);
                            if (mulSignInverter == null) {
                                mulSignInverter = r;
                            } else if (r.compareTo(mulSignInverter) > 0) {
                                multiplier = multiplier.multiply(mulSignInverter);
                                mulSignInverter = r;
                            } else {
                                multiplier = multiplier.multiply(new Rational(card.value));
                            }
                        } else {
                            zeroFactor = true;
                        }
                        break;
                    case '/':
                        if (zeroCmp > 0) {
                            divider = divider.multiply(new Rational(card.value));
                        } else if (zeroCmp < 0) {
                            Rational r = new Rational(card.value);
                            if (divSignInverter == null) {
                                divSignInverter = r;
                            } else if (r.compareTo(divSignInverter) > 0) {
                                divider = divider.multiply(divSignInverter);
                                divSignInverter = r;
                            } else {
                                divider = divider.multiply(new Rational(card.value));
                            }
                        } else {
                            throw new IllegalArgumentException("Zero divisor found");
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Invalid operator " + card.op);
                }
            }
            // Use the greatest between mulSignInverter and divSignInverter
            // and accumulate the other one to the multiplier or divider
            Rational signInverter;
            if (mulSignInverter != null && divSignInverter != null) {
                if (mulSignInverter.compareTo(divSignInverter) > 0) {
                    signInverter = mulSignInverter;
                    divider = divider.multiply(divSignInverter);
                } else {
                    signInverter = Rational.ONE.divide(divSignInverter);
                    multiplier = multiplier.multiply(mulSignInverter);
                }
            } else {
                signInverter = divSignInverter != null ? Rational.ONE.divide(divSignInverter) : mulSignInverter;
            }
            debugPrint("  initialValue=" + initialValue + " positiveTerm=" + positiveTerm + " negativeTerm=" + negativeTerm
                    + " signInverter=" + signInverter + " multiplier=" + multiplier + " divider=" + divider + " zeroFactor=" + zeroFactor);
            // Here we do our constant time evaluation. The sign of the multiplier,
            // the presence of a sign inverter and of a zero factor dictate
            // the optimal order of evaluation.
            Rational result;
            if (multiplier.compareTo(Rational.ZERO) > 0) {
                if (signInverter != null) {
                    if (zeroFactor) {
                        // Let's start with initialValue / divider * 0
                        result = Rational.ZERO.add(negativeTerm).multiply(signInverter).add(positiveTerm).multiply(multiplier);
                    } else {
                        result = Rational.min(initialValue.divide(divider).normalize().add(negativeTerm),
                                initialValue.add(negativeTerm).divide(divider).normalize())
                                .multiply(signInverter).add(positiveTerm).multiply(multiplier);
                    }
                } else {
                    if (zeroFactor) {
                        // Let's start with (initialValue / divider + negativeTerm) * 0
                        result = Rational.ZERO.add(positiveTerm).multiply(multiplier);
                    } else {
                        result = Rational.max(initialValue.add(negativeTerm).divide(divider).normalize().add(positiveTerm).multiply(multiplier),
                                initialValue.divide(divider).normalize().add(positiveTerm).multiply(multiplier).add(negativeTerm));
                    }
                }
            } else {
                if (signInverter != null) {
                    if (zeroFactor) {
                        // Let's start with initialValue / divider * 0
                        result = Rational.ZERO.add(positiveTerm).multiply(signInverter).add(negativeTerm).multiply(multiplier);
                    } else {
                        result = Rational.max(initialValue.divide(divider).normalize().add(positiveTerm),
                                initialValue.add(positiveTerm).divide(divider).normalize())
                                .multiply(signInverter).add(negativeTerm).multiply(multiplier);
                    }
                } else {
                    if (zeroFactor) {
                        // Let's start with initialValue / divider * 0
                        result = Rational.ZERO.add(negativeTerm).multiply(multiplier).add(positiveTerm);
                    } else {
                        result = Rational.max(initialValue.add(positiveTerm).divide(divider).normalize().add(negativeTerm).multiply(multiplier),
                                initialValue.divide(divider).normalize().add(negativeTerm).multiply(multiplier).add(positiveTerm));
                    }
                }
            }
            result = result.normalize();
            return result;
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
            Rational result = test.findMaximum();
            System.out.println("Case #" + caseIndex + ": " + result.num + " " +result.den);
            caseIndex++;
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        Operation operation = new Operation();
        operation.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/B-large-practice.in") : System.in);
        operation.runTests();
        System.err.println("Operation done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

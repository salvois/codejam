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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * The Stack Management problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p4
 * Code Jam analysis version.
 *
 * Unfortunately I wasn't able to find out how to cope with the large dataset
 * on my own, and finally gave up. What follows is an implementation of the
 * official analysis of the problem by Code Jam:
 * https://code.google.com/codejam/contest/6314486/dashboard#s=a&a=4
 * Honestly, I could not have found it...
 * 
 * @author Salvo Isaja
 */
public class StackManagementCodejam {

    private static class Card {
        final int value;
        final int suit;

        Card(int value, int suit) { this.value = value; this.suit = suit; }
        @Override public String toString() { return value + "-" + suit; }
    }

    /** One of the stacks of a game being played. */
    private static class Stack {
        final int id; // for debugging purposes
        final List<Card> cards;

        Stack(int id, int capacity) { this.id = id; this.cards = new ArrayList<>(capacity); }
        @Override public String toString() { return id + cards.toString(); }
    }

    private static class Suit {
        List<Card> cards = new ArrayList<>();
        Collection<Suit> children = new ArrayList<>();

        Card ace() { return cards.get(0); }
        Card king() { return cards.size() > 1 ? cards.get(1) : null; }
    }

    private static class Test {
        final int testIndex;
        final List<Stack> stacks;
        final Map<Integer, Suit> suits = new HashMap<>();
        Set<Suit> vertices = new HashSet<>();
        Collection<Suit> sources = new ArrayList<>();
        Set<Suit> targets = new HashSet<>();

        Test(int testIndex, List<List<Card>> premadeStacks) {
            this.testIndex = testIndex;
            this.stacks = new ArrayList<>(premadeStacks.size());
            for (int stackIndex = 0; stackIndex < premadeStacks.size(); stackIndex++) {
                List<Card> premadeStack = premadeStacks.get(stackIndex);
                Stack stack = new Stack(stackIndex, premadeStack.size());
                for (int i = premadeStack.size() - 1; i >= 0; i--) {
                    Card card = premadeStack.get(i);
                    Suit suit = suits.get(card.suit);
                    if (suit == null) {
                        suit = new Suit();
                        suits.put(card.suit, suit);
                    }
                    suit.cards.add(card);
                    stack.cards.add(card);
                }
                stacks.add(stack);
            }
            for (Suit suit : suits.values()) {
                if (suit == null) continue;
                suit.cards.sort((a, b) -> b.value - a.value);
            }
        }

        private void createGraph() {
            // Build vertices
            for (Stack stack : stacks) {
                Card bottomCard = stack.cards.get(0);
                Suit bottomSuit = suits.get(bottomCard.suit);
                if (bottomCard != bottomSuit.ace()) continue; // ace not at bottom
                vertices.add(bottomSuit);
                if (bottomSuit.cards.size() == 1) sources.add(bottomSuit);
                for (int i = 1; i < stack.cards.size(); i++) {
                    Card otherCard = stack.cards.get(i);
                    Suit otherSuit = suits.get(otherCard.suit);
                    if (otherCard == otherSuit.ace()) {
                        targets.add(bottomSuit);
                        break;
                    }
                }
            }
            // Build edges
            for (Stack stack : stacks) {
                Card bottomCard = stack.cards.get(0);
                Suit bottomSuit = suits.get(bottomCard.suit);
                if (bottomCard != bottomSuit.ace()) continue; // not a vertex
                for (int i = 1; i < stack.cards.size(); i++) {
                    Card otherCard = stack.cards.get(i);
                    Suit otherSuit = suits.get(otherCard.suit);
                    if (otherCard == otherSuit.king()) {
                        if (vertices.contains(otherSuit)) {
                            bottomSuit.children.add(otherSuit);
                        }
                    }
                }
            }
        }
        
        private boolean search(Suit s) {
            if (targets.contains(s)) return true;
            for (Suit t : s.children) {
                if (search(t)) return true;
            }
            return false;
        }
        
        private boolean solve() {
            if (suits.size() < stacks.size()) return true;
            if (suits.size() > stacks.size()) return false;
            createGraph();
            if (vertices.size() == stacks.size()) return true;
            for (Suit s : sources) {
                if (search(s)) return true;
            }
            return false;
        }

        void run() {
            debugPrint("Case #" + testIndex + ", " + stacks.size() + " stacks");
            boolean b = solve();
            System.out.println("Case #" + testIndex + ": " + (b ? "POSSIBLE" : "IMPOSSIBLE"));
        }
    }
    
    private static final boolean DEBUG = false;
    private List<List<Card>> premadeStacks;

    private static void debugPrint(String s) {
        if (DEBUG) System.out.println(s);
    }

    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int premadeStackCount = scanner.nextInt();
            premadeStacks = new ArrayList<>(premadeStackCount);
            for (int i = 0; i < premadeStackCount; i++) {
                int cardCount = scanner.nextInt();
                List<Card> stack = new ArrayList<>(cardCount);
                for (int j = 0; j < cardCount; j++) {
                    stack.add(new Card(scanner.nextInt(), scanner.nextInt()));
                }
                premadeStacks.add(stack);
            }
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                int stackCount = scanner.nextInt();
                int cardCount = scanner.nextInt();
                List<List<Card>> stacks = new ArrayList<>(stackCount);
                for (int i = 0; i < stackCount; i++) {
                    int stackIndex = scanner.nextInt();
                    List<Card> stack = premadeStacks.get(stackIndex);
                    if (stack.size() != cardCount) {
                        throw new IllegalStateException("Different card count for stack " + stackIndex + ": " + stack.size() + ", expected " + cardCount);
                    }
                    stacks.add(stack);
                }
                Test test = new Test(t, stacks);
                test.run();
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        StackManagementCodejam sm = new StackManagementCodejam();
        sm.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/E-large-practice.in") : System.in);
        System.err.println("StackManagementCodejam done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * The Stack Management problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p4
 * Brute force search version.
 *
 * This solution just plays the game, and whenever it gets stuck, it undoes
 * all moves from the previous crossroads and deepens the next one.
 * This is enough to complete the small dataset in an instant, but the limits
 * of the large dataset make trying the large number of possibilities with deep
 * recursion impracticable.
 * 
 * Even performing card removal in batches and limiting recursion to card
 * relocation moves does not help much. I had some success using a best first
 * search by relocating the card on top of the fuller stack with the higher
 * count of highest-valued cards, but failed to complete a handful of test
 * cases from the large dataset.
 * 
 * Given that, and the explicit limit on the total amount of cards (N*C),
 * I was certain that a solution could by found by just looking at the cards,
 * but I was not able to see it. Official solution in StackManagementCodejam.
 * 
 * @author Salvo Isaja
 */
public class StackManagementSmall {

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

        Stack(int id, List<Card> premadeStack) {
            this.id = id;
            cards = new ArrayList<>(premadeStack.size());
            for (int i = premadeStack.size() - 1; i >= 0; i--) cards.add(premadeStack.get(i));
        }
        int size() { return cards.size(); }
        Card peek() { return cards.get(cards.size() - 1); }
        Card pop() { return cards.remove(cards.size() - 1); }
        void push(Card card) { cards.add(card); }
    }

    /** State of a suit before each move. */
    private static class Suit {
        Stack minStack; // stack with the card of the lowest value on top
        int stackCount; // number of stacks with this suit on top
        Suit(Stack minStack, int stackCount) { this.minStack = minStack; this.stackCount = stackCount; }
    }
    
    private static final boolean DEBUG = false;
    private List<List<Card>> premadeStacks;

    private static void debugPrint(String s) {
        if (DEBUG) System.out.println(s);
    }

    private static void debugPrintStacks(List<Stack> stacks) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (Stack stack : stacks) {
                if (sb.length() > 0) sb.append("  ");
                if (stack.size() > 0) sb.append(stack.peek());
                else sb.append("empty");
            }
            System.out.println(sb.toString());
        }
    }

    /** Do the next move given the current state of stacks of a game. */
    private boolean doMove(List<Stack> stacks) {
        // Collect suit states
        Map<Integer, Suit> suits = new HashMap<>(); // keyed by suit number
        Stack emptyStack = null;
        boolean finished = true;
        for (Stack stack : stacks) {
            if (stack.size() > 1) finished = false;
            if (stack.size() == 0) {
                emptyStack = stack;
                continue;
            }
            Card topCard = stack.peek();
            Suit suit = suits.get(topCard.suit);
            if (suit != null) {
                suit.stackCount++;
                if (topCard.value < suit.minStack.peek().value) suit.minStack = stack;
            } else {
                suits.put(topCard.suit, new Suit(stack, 1));
            }
        }
        // Move
        if (finished) return true;
        for (Suit suit : suits.values()) {
            if (suit.stackCount == 1) continue;
            debugPrintStacks(stacks);
            debugPrint("Popping " + suit.minStack.peek() + " from stack " + suit.minStack.id);
            Card card = suit.minStack.pop();
            if (doMove(stacks)) return true;
            suit.minStack.push(card);
            debugPrint("Undid pop of " + suit.minStack.peek() + " from stack " + suit.minStack.id);
        }
        if (emptyStack == null) return false;
        for (Suit suit : suits.values()) {
            if (suit.stackCount > 1) continue;
            if (suit.minStack.size() == 1) continue; // relocating the last card does not make sense
            debugPrintStacks(stacks);
            debugPrint("Relocating " + suit.minStack.peek() + " from stack " + suit.minStack.id + " to " + emptyStack.id);
            Card card = suit.minStack.pop();
            emptyStack.push(card);
            if (doMove(stacks)) return true;
            emptyStack.pop();
            suit.minStack.push(card);
            debugPrint("Undid relocation of " + suit.minStack.peek() + " from stack " + suit.minStack.id + " to " + emptyStack.id);
        }
        return false;
    }

    private void run(int testIndex, List<Stack> stacks) {
        debugPrint("Case #" + testIndex + ", " + stacks.size() + " stacks");
        boolean b = doMove(stacks);
        System.out.println("Case #" + testIndex + ": " + (b ? "POSSIBLE" : "IMPOSSIBLE"));
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
                List<Stack> stacks = new ArrayList<>(stackCount);
                for (int i = 0; i < stackCount; i++) {
                    int stackIndex = scanner.nextInt();
                    Stack stack = new Stack(i, premadeStacks.get(stackIndex));
                    if (stack.cards.size() != cardCount) {
                        throw new IllegalStateException("Different card count for stack " + stackIndex + ": " + stack.cards.size() + ", expected " + cardCount);
                    }
                    stacks.add(stack);
                }
                run(t, stacks);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        StackManagementSmall sm = new StackManagementSmall();
        sm.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/E-small-practice.in") : System.in);
        System.err.println("StackManagementSmall done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

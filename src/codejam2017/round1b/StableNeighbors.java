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
package codejam2017.round1b;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * Stable Neigh-bors problem from Round 1B of Code Jam 2017.
 * https://codejam.withgoogle.com/codejam/contest/8294486/dashboard#s=p1
 * 
 * That was a really tricky one!
 * To solve the small dataset, I opted to consider unicorns as entities to
 * be scheduled, and used a scheduling algorithm to achieve fairness while
 * not scheduling two times in a row two entities of the same scheduling group
 * (that is, equally-colored unicorns).
 * Fairness is achieved by using a policy of selecting larger group first,
 * and the least recently used group in case of tie. If we are not able to
 * find a unicorn with a different color than the last one, the solution
 * is impossible.
 * The scheduling algorithm works quite well, but could fail to join the
 * two ends of the ring. Luckily, this can be solved easily as we have three
 * scheduling groups, so we can just swap the last two elements.
 * 
 * The large dataset required a lot of thought. The revealing observation
 * is that orange, green and violet unicorns can only be neighbor of
 * blue, red and yellow unicorns, respectively.
 * Thus, we can preprocess such "bicolored" unicorns by pairing them with
 * the appropriate "monochrome" unicorns, and calling this batch
 * a "meta-unicorn", that we will add back to the group of the appropriate
 * primary color, so that we can apply the same scheduling algorithm as before.
 * See the comments on preprocessBicolor() for the details.
 * 
 * The resulting algorithm is the combination of several parts running
 * in linear time, thus it is O(n).
 * 
 * @author Salvo Isaja
 */
public class StableNeighbors {

    private enum Color { RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET } 
    private static final char[] COLOR_LETTERS = { 'R', 'O', 'Y', 'G', 'B', 'V' };
    private static final boolean DEBUG = false;

    private static class Unicorn {
        final Color color;
        List<Unicorn> subunicorns; // for meta-unicorns only (large dataset)

        public Unicorn(Color color) {
            this.color = color;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (subunicorns != null) {
                for (Unicorn u : subunicorns) sb.append(COLOR_LETTERS[u.color.ordinal()]);
            } else {
                sb.append(COLOR_LETTERS[color.ordinal()]);
            }
            return sb.toString();
        }
    }
    
    private static boolean areAllGroupsEmpty(Queue<Unicorn>[] groups, Queue<Unicorn> excluded) {
        for (Queue<Unicorn> group : groups) {
            if (group != excluded && !group.isEmpty()) return false;
        }
        return true;
    }

    /**
     * Preprocesses bicolor unicorns.
     * A bicolor unicorn has its mane appearing in a secondary color (orange,
     * green or violet) and can only be neighbor of a unicorn with a specific
     * primary color (blue, red and yellow, respectively).
     * Thus we create a "meta-unicorn" containing all bicolor unicorns that
     * can be side by side, by alternating primary (P) and secondary (S) color
     * in the (regex-like) pattern "P(SP)+"
     * We remove the used unicorns from the original groups and add back
     * the meta-unicorn to the group of the appropriate primary color.
     * In the end, on success, we only have primary-colored unicorns.
     * The patterns "P(SP)*S" and "S" are only allowed if there are no unicorns
     * in any other group.
     * Moreover, the pattern "P(SP)+" is only valid if there are unicorns in
     * other groups, in order to break the pattern when the ring closes.
     * @return true if preprocessing was successful, false if the solution is impossible.
     */
    private static boolean preprocessBicolor(Queue<Unicorn>[] groups, Color secondaryColor, Color primaryColor) {
        Unicorn collector = new Unicorn(primaryColor);
        collector.subunicorns = new ArrayList<>();
        if (groups[secondaryColor.ordinal()].isEmpty()) return true;
        Unicorn u = groups[primaryColor.ordinal()].poll();
        if (u == null) {
            u = groups[secondaryColor.ordinal()].poll();
            if (!areAllGroupsEmpty(groups, null)) return false;
            // The "S" case
            collector.subunicorns.add(u);
            groups[primaryColor.ordinal()].add(collector);
            return true;
        }
        collector.subunicorns.add(u);
        while ((u = groups[secondaryColor.ordinal()].poll()) != null) {
            collector.subunicorns.add(u);
            u = groups[primaryColor.ordinal()].poll();
            if (u == null) {
                if (!areAllGroupsEmpty(groups, null)) return false;
                // The "P(SP)*S" case
                groups[primaryColor.ordinal()].add(collector);
                return true;
            }
            collector.subunicorns.add(u);
        }
        // The "P(SP)+" case
        groups[primaryColor.ordinal()].add(collector);
        return !areAllGroupsEmpty(groups, groups[primaryColor.ordinal()]);
    }

    private static String solve(Queue<Unicorn>[] groups) {
        // Preprocess orange, green and violet unicorns.
        // This is the only modification needed to handle the large dataset.
        if (!preprocessBicolor(groups, Color.ORANGE, Color.BLUE)
                || !preprocessBicolor(groups, Color.GREEN, Color.RED)
                || !preprocessBicolor(groups, Color.VIOLET, Color.YELLOW)) {
            return null;
        }
        // Schedule unicorn groups in largest, least recently used first order
        List<Unicorn> results = new ArrayList<>();
        LinkedList<Queue<Unicorn>> groupsLru = new LinkedList<>();
        for (Queue<Unicorn> group : groups) groupsLru.add(group);
        Queue<Unicorn> lastGroup = null;
        while (!areAllGroupsEmpty(groups, null)) {
            Queue<Unicorn> largestGroup = null;
            for (Queue<Unicorn> group : groupsLru) {
                if ((largestGroup == null || group.size() > largestGroup.size()) && group != lastGroup) largestGroup = group;
            }
            assert largestGroup != null;
            groupsLru.remove(largestGroup);
            groupsLru.addLast(largestGroup);
            if (largestGroup.isEmpty()) return null;
            Unicorn unicorn = largestGroup.iterator().next();
            results.add(unicorn);
            largestGroup.remove(unicorn);
            lastGroup = largestGroup;
        }
        // Resolve conflict between the last and first stall by swapping the last two unicorns.
        if (results.size() > 2 && results.get(results.size() - 1).color == results.get(0).color) {
            Unicorn t = results.get(results.size() - 1);
            if (t.color == results.get(results.size() - 3).color) return null;
            results.set(results.size() - 1, results.get(results.size() - 2));
            results.set(results.size() - 2, t);
        }
        // Create result string
        StringBuilder sb = new StringBuilder();
        for (Unicorn unicorn : results) sb.append(unicorn.toString());
        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1b/B-large-practice.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int unicornCount = scanner.nextInt();
                Queue<Unicorn>[] groups = new Queue[Color.values().length];
                for (int i = 0; i < groups.length; i++) {
                    groups[i] = new ArrayDeque<>();
                    int groupUnicornCount = scanner.nextInt();
                    for (int j = 0; j < groupUnicornCount; j++) {
                        Unicorn u = new Unicorn(Color.values()[i]);
                        groups[i].add(u);
                    }
                }
                String result = solve(groups);
                System.out.println("Case #" + testNumber + ": " + (result != null ? result : "IMPOSSIBLE"));
            }
        }
        System.err.println("StableNeighbors done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
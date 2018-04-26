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
import java.util.BitSet;
import java.util.Scanner;

/**
 * Pony Express problem from Round 1B of Code Jam 2017.
 * https://codejam.withgoogle.com/codejam/contest/8294486/dashboard#s=p2
 * Small dataset version.
 * 
 * The problem is asking for the shortest path in a weighted graph. Though,
 * the weights to consider are not the provided distances, but the times
 * each horse would take.
 * Thus, we construct a new graph, checking, for each horse, what cities
 * are reachable, then we find the shortest path in the resulting graph.
 *
 * For the first part, I did a depth-first search starting from the home city
 * of each horse, to see how far it can go. Note that this proved to be wrong
 * in general (see PonyExpressLarge), since, if the horse could reach another
 * city by multiple paths, we should properly account for distances. But for
 * the small dataset each city can be reached only with a single path, so
 * it worked in practice.
 * 
 * Once we have a graph of cities reachable by each horse, weighted by
 * travel times, the second part is computing the shortest path.
 * The resulting graph is directed acyclic, so it can be done trivially.
 * 
 * This solution runs in quadratic time due to the adjacency matrix representation.
 * 
 * @author Salvo Isaja
 */
public class PonyExpressSmall {

    private static final boolean DEBUG = false;
    private int cityCount;
    private int stopCount;
    private int[] horseMaxDistances;
    private int[] horseSpeeds;
    private int[][] distances;
    private int[] stopFroms;
    private int[] stopTos;
    private double[][] times;

    /** Depth-first search to compute times for cities reachable by horse. */
    private void computeTimes(int horse, int from, int distanceSoFar, BitSet visited) {
        visited.set(from);
        for (int to = 0; to < cityCount; to++) {
            int d = distances[from][to];
            if (d < 0) continue;
            if (distanceSoFar + d > horseMaxDistances[horse]) break;
            distanceSoFar += d;
            times[horse][to] = (double) distanceSoFar / horseSpeeds[horse];
            computeTimes(horse, to, distanceSoFar, visited);
            distanceSoFar -= d;
        }
    }
    
    private double solve() {
        // Construct the graph of travel times
        times = new double[cityCount][cityCount];
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < cityCount; j++) {
                times[i][j] = -1;
            }
        }
        for (int from = 0; from < cityCount; from++) {
            BitSet visited = new BitSet(cityCount);
            computeTimes(from, from, 0, visited);
        }
        // Shortest path in a DAG using times as graph weights
        double[] dp = new double[cityCount];
        for (int i = 1; i < cityCount; i++) dp[i] = Double.POSITIVE_INFINITY;
        for (int from = 0; from < cityCount; from++) {
            for (int to = 0; to < cityCount; to++) {
                if (times[from][to] <= 0) continue;
                double t = dp[from] + times[from][to];
                if (t < dp[to]) dp[to] = t;
            }
        }
        return dp[cityCount - 1];
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1b/C-small-practice.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                PonyExpressSmall pe = new PonyExpressSmall();
                pe.cityCount = scanner.nextInt();
                pe.stopCount = scanner.nextInt();
                pe.horseMaxDistances = new int[pe.cityCount];
                pe.horseSpeeds = new int[pe.cityCount];
                for (int i = 0; i < pe.cityCount; i++) {
                    pe.horseMaxDistances[i] = scanner.nextInt();
                    pe.horseSpeeds[i] = scanner.nextInt();
                }
                pe.distances = new int[pe.cityCount][pe.cityCount];
                for (int i = 0; i < pe.cityCount; i++) {
                    for (int j = 0; j < pe.cityCount; j++) {
                        pe.distances[i][j] = scanner.nextInt();
                    }
                }
                pe.stopFroms = new int[pe.stopCount];
                pe.stopTos = new int[pe.stopCount];
                for (int i = 0; i < pe.stopCount; i++) {
                    pe.stopFroms[i] = scanner.nextInt();
                    pe.stopTos[i] = scanner.nextInt();
                }
                //if (testNumber != 6) continue;
                double result = pe.solve();
                //System.out.format("Case #%d: %.9f\n", testNumber, result);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("PonyExpressSmall done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
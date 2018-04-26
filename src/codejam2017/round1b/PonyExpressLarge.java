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
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Pony Express problem from Round 1B of Code Jam 2017.
 * https://codejam.withgoogle.com/codejam/contest/8294486/dashboard#s=p2
 * Large dataset version.
 * 
 * The premise it the same as the small data set (see PonyExpressSmall).
 * The problem is asking for the shortest path in a weighted graph. Though,
 * the weights to consider are not the provided distances, but the times
 * each horse would take.
 * Thus, we construct a new graph, checking, for each horse, what cities
 * are reachable, then we find the shortest path in the resulting graph.
 *
 * Each horse can travel to any reachable city by multiple path, thus,
 * to construct the alternate graph, we need to properly compute the shortest
 * path from the home city of each horse to any other city, checking if it
 * is reachable during the process. Finally, we need to convert the resulting
 * travel distances to travel times, knowing the speed of the horse.
 * I opted to apply the Dijkstra's algorithm once for each horse, as I didn't
 * know the Floyd-Warshall algorithm.
 * 
 * The resulting graph of cities reachable by each horse, weighted by
 * travel times, is not acyclic but has positive weights, thus we can apply
 * Dijkstra again, for each stop (remembering that they are independent).
 * 
 * The complexity is dominated by Dijkstra on the adjacency matrix of the graph,
 * that is O(n^2 * log2(n)) (assuming a O(log2(n)) binary heap for the
 * implementation of PriorityQueue), times n cities.
 *
 * @author Salvo Isaja
 */
public class PonyExpressLarge {

    private static final boolean DEBUG = false;
    private int cityCount;
    private int stopCount;
    private int[] horseMaxDistances;
    private int[] horseSpeeds;
    private int[][] distances;
    private int[] stopFroms;
    private int[] stopTos;
    private double[][] times;

    /** Computes the minimum time to reach every reachable city by the specified horse, using Dijkstra. */
    private void computeTimes(int horse) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(cityCount, (a, b) -> (int) Math.signum(times[horse][a] - times[horse][b]));
        for (int i = 0; i < cityCount; i++) {
            times[horse][i] = i == horse ? 0 : Double.POSITIVE_INFINITY;
            pq.add(i);
        }
        pq.add(horse);
        while (!pq.isEmpty()) {
            int from = pq.poll();
            for (int to = 0; to < cityCount; to++) {
                if (distances[from][to] < 0) continue;
                double d = times[horse][from] + distances[from][to];
                if (d > horseMaxDistances[horse]) continue;
                if (d < times[horse][to]) {
                    pq.remove(to);
                    times[horse][to] = d;
                    pq.add(to);
                }
            }
        }
        for (int i = 0; i < cityCount; i++) times[horse][i] /= horseSpeeds[horse];
    }

    /** Dijkstra's algorithm for shortest path. */ 
    private double dijkstra(int source, int target) {
        double[] results = new double[cityCount];
        PriorityQueue<Integer> pq = new PriorityQueue<>(cityCount, (a, b) -> (int) Math.signum(results[a] - results[b]));
        for (int i = 0; i < cityCount; i++) {
            results[i] = i == source ? 0 : Double.POSITIVE_INFINITY;
            pq.add(i);
        }
        while (!pq.isEmpty()) {
            int from = pq.poll();
            if (from == target) return results[target];
            for (int to = 0; to < cityCount; to++) {
                if (times[from][to] == Double.POSITIVE_INFINITY) continue;
                double t = results[from] + times[from][to];
                if (t < results[to]) {
                    pq.remove(to);
                    results[to] = t;
                    pq.add(to);
                }
            }
        }
        throw new IllegalStateException("Unreachable destination");
    }

    private void solve(int testNumber) {
        // Construct the graph of travel times
        times = new double[cityCount][cityCount];
        for (int from = 0; from < cityCount; from++) {
            computeTimes(from);
        }
        // Dijkstra shortest paths using times as graph weights
        System.out.print("Case #" + testNumber + ":");
        for (int i = 0; i < stopCount; i++) {
            double t = dijkstra(stopFroms[i], stopTos[i]);
            System.out.print(" " + t);
        }
        System.out.println();
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1b/C-large-practice.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                PonyExpressLarge pe = new PonyExpressLarge();
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
                    pe.stopFroms[i] = scanner.nextInt() - 1;
                    pe.stopTos[i] = scanner.nextInt() - 1;
                }
                pe.solve(testNumber);
            }
        }
        System.err.println("PonyExpressLarge done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
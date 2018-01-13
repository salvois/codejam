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
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * The Spanning Planning problem from Google Code Jam World Finals 2017.
 * https://code.google.com/codejam/contest/6314486/dashboard#s=p2
 * 
 * Internally, I represent the edges of the graph as indexes of an array
 * of pair of nodes, numbering them from zero.
 * For example, for graphs up to 2, 3, 4 and 5 nodes respectively:
 *   1st node: 0   0 1   0 1 2   0 1 2 3
 *   2nd node: 1   2 2   3 3 3   4 4 4 4
 *   edge:     0   1 2   3 4 5   6 7 8 9
 *
 * I used to validate combinations of graph edges by enumerating their
 * (n-1)-combinations (candidate spanning trees) checking if they touched all
 * n nodes, but then I found the Kirchhoff's theorem on Wikipedia, speeding up
 * validation from O(n!) to O(n^3) thanks to Gaussian elimination.
 *
 * In my first attempt I brute-force scanned all combinations of edges for
 * graphs of as many nodes as possible, willing to create larger graphs by
 * joining them with one edge, but I failed to find all primes up to 10000.
 * Thus I switched to a search among random combinations of edges, that happens
 * to find all answers within the limits pretty quickly (almost: 22 seems to be
 * very difficult to find!).
 * Since it seems to me more elegant to use answers with small number of nodes
 * when possible, I still use brute force for up to 8 nodes, as it is tractable.
 * 
 * @author Salvo Isaja
 */
public class SpanningPlanning {

    private static class Graph {

        final int nodeCount;
        final BitSet edges;

        public Graph(int nodeCount, BitSet edges) {
            this.nodeCount = nodeCount;
            this.edges = edges;
        }
    }
    
    private static final boolean DEBUG = false;
    private static final int MAX_SMALL_NODES = 9;
    private static final int MAX_NODES = 22;
    private static final int MAX_EDGES = MAX_NODES * (MAX_NODES - 1) / 2;
    private static final int MIN_SPANNING_TREES = 3;
    private static final int MAX_SPANNING_TREES = 10000;
    private final int[] edgeFirstNodes;
    private final int[] edgeSecondNodes;
    private final TreeMap<Integer, Graph> answers = new TreeMap<>();

    private static void debugPrint(String s) {
        if (DEBUG) System.out.println(s);
    }

    private static void printEdges(String prefix, BitSet edges) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder(prefix);
            for (int i = edges.nextSetBit(0); i >= 0; i = edges.nextSetBit(i + 1)) {
                sb.append('\t').append(i);
            }
            debugPrint(sb.toString());
        }
    }

    private static void printMatrix(double[][] m) {
        if (DEBUG) {
            for (double[] row : m) {
                for (int j = 0; j < row.length; j++) System.out.print("\t" + row[j]);
                System.out.println();
            }
        }
    }

    // Courtesy of https://en.wikipedia.org/wiki/Gaussian_elimination
    private static double computeDeterminant(double[][] m) {
        for (int pivot = 0; pivot < m.length; pivot++) {
            int iMax = pivot;
            double aMax = Math.abs(m[pivot][pivot]);
            for (int i = pivot + 1; i < m.length; i++) {
                double a = Math.abs(m[i][pivot]);
                if (a > aMax) {
                    iMax = i;
                    aMax = a;
                }
            }
            // Treat very small pivot values as zero to avoid surprising results
            // due to floating point precision when multiplying by a large value
            if (aMax < 1.0e-6) return 0.0;
            double[] temp = m[pivot];
            m[pivot] = m[iMax];
            m[iMax] = temp;
            aMax = m[pivot][pivot];
            for (int i = pivot + 1; i < m.length; i++) {
                double f = m[i][pivot] / aMax;
                for (int j = pivot + 1; j < m.length; j++) m[i][j] -= m[pivot][j] * f;
                m[i][pivot] = 0.0;
            }
        }
        // Trivially compute determinant as product of main diagonal
        double result = 1.0;
        for (int i = 0; i < m.length; i++) result *= m[i][i];
        return result;
    }

    public SpanningPlanning() {
        edgeFirstNodes = new int[MAX_EDGES];
        edgeSecondNodes = new int[MAX_EDGES];
        int fi = 0;
        int si = 1;
        for (int i = 0; i < MAX_EDGES; i++) {
            edgeFirstNodes[i] = fi;
            edgeSecondNodes[i] = si;
            fi++;
            if (fi == si) {
                fi = 0;
                si++;
            }
        }
    }

    private int findSpanningTreeCount(int nodeCount, BitSet edges) {
        // Compute the number of spanning trees using Kirchoff's theorem,
        // courtesy of https://en.wikipedia.org/wiki/Kirchhoff%27s_theorem
        // Construct the Laplacian matrix without the last row and column
        double[][] laplacian = new double[nodeCount - 1][nodeCount - 1];
        for (int i = edges.nextSetBit(0); i >= 0; i = edges.nextSetBit(i + 1)) {
            int a = edgeFirstNodes[i];
            int b = edgeSecondNodes[i];
            if (a < nodeCount - 1) {
                laplacian[a][a] += 1.0;
                if (b < nodeCount - 1) laplacian[a][b] = -1.0;
            }
            if (b < nodeCount - 1) {
                laplacian[b][b] += 1.0;
                if (a < nodeCount - 1) laplacian[b][a] = -1.0;
            }
        }
        double d = computeDeterminant(laplacian);
        return d > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.round(d);
    }

    private void tryAddAnswer(int spanningTreeCount, int nodeCount, BitSet edges) {
        if (spanningTreeCount >= MIN_SPANNING_TREES && spanningTreeCount <= MAX_SPANNING_TREES) {
            Graph c = answers.get(spanningTreeCount);
            if (c == null || nodeCount < c.nodeCount) answers.put(spanningTreeCount, new Graph(nodeCount, (BitSet) edges.clone()));
            if (c == null) debugPrint("Answers found: " + answers.size());
        }
    }
    
    private int findEdgeIndex(int firstNode, int secondNode) {
        for (int i = 0; i < MAX_EDGES; i++) {
            if (edgeFirstNodes[i] == firstNode && edgeSecondNodes[i] == secondNode) return i;
        }
        throw new IllegalArgumentException("Invalid nodes");
    }

    private Graph createCycleGraph(int nodeCount) {
        BitSet edges = new BitSet();
        for (int i = 0; i < nodeCount - 1; i++) edges.set(findEdgeIndex(i, i + 1));
        edges.set(findEdgeIndex(0, nodeCount - 1));
        return new Graph(nodeCount, edges);
    }

    /**
     * Precalculate answers for all possible spanning trees within the limits.
     * This is the core of the algorithm described above.
     * It completes in less than two minutes on my i5-3570K.
     */
    private void precalculateAnswers() {
        long time = System.nanoTime();
        Random random = new Random();
        // Use brute force for small node counts
        for (int nodeCount = 3; nodeCount < MAX_SMALL_NODES; nodeCount++) {
            debugPrint("Node count: " + nodeCount);
            int l = 1 << Math.min(nodeCount * (nodeCount - 1) / 2, 26); // no answers found for 8 nodes after 26 edges
            for (long v = 1; v < l; v++) {
                BitSet edges = BitSet.valueOf(new long[] { v });
                int spanningTreeCount = findSpanningTreeCount(nodeCount, edges);
                tryAddAnswer(spanningTreeCount, nodeCount, edges);
            }
        }
        // It seems that reaching an answer with 22 spanning trees is very
        // difficult even after long randomization. Let's construct a cycle graph.
        answers.put(22, createCycleGraph(22));
        debugPrint("Beginning randomization.");
        // Randomize everything else
        while (answers.size() < MAX_SPANNING_TREES - MIN_SPANNING_TREES + 1) {
            int nodeCount = MAX_SMALL_NODES + random.nextInt(MAX_NODES - MAX_SMALL_NODES + 1);
            int edgeCount = nodeCount * (nodeCount - 1) / 2;
            BitSet edges = new BitSet(edgeCount);
            for (int i = 0; i < 1000 && edges.cardinality() < edgeCount; i++) {
                int edgeIndex = random.nextInt(edgeCount);
                if (edges.get(edgeIndex)) continue;
                edges.set(edgeIndex);
                int spanningTreeCount = findSpanningTreeCount(nodeCount, edges);
                tryAddAnswer(spanningTreeCount, nodeCount, edges);
            }
        }
        debugPrint("Found all answers in " + ((System.nanoTime() - time) / 1e9) + " seconds");
        answers.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(k).append('\t').append(v.nodeCount).append('\t').append(v.edges.cardinality());
            for (int i = v.edges.nextSetBit(0); i >= 0; i = v.edges.nextSetBit(i + 1)) {
                sb.append('\t').append(i);
            }
            System.out.println(sb.toString());
        });
    }

    private void loadPrecalcs(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            while (scanner.hasNext()) {
                int spanningTreeCount = scanner.nextInt();
                int nodeCount = scanner.nextInt();
                int edgeCount = scanner.nextInt();
                BitSet bs = new BitSet();
                for (int i = 0; i < edgeCount; i++) bs.set(scanner.nextInt());
                answers.put(spanningTreeCount, new Graph(nodeCount, bs));
            }
        }
        if (DEBUG) {
            answers.forEach((k, v) -> {
                int spanningTreeCount = findSpanningTreeCount(v.nodeCount, v.edges);
                if (k != spanningTreeCount) debugPrint("Wrong spanning tree count " + spanningTreeCount + " expected " + k);
            });
        }
    }

    private void run(int caseIndex, int targetCount) {
        Graph c = answers.get(targetCount);
        if (c == null) {
            System.out.println("Case #" + caseIndex + ": not found");
            return;
        }
        System.out.println("Case #" + caseIndex + ": " + c.nodeCount);
        if (!DEBUG) {
            int[][] thereIsNoSpoon = new int[c.nodeCount][c.nodeCount];
            for (int i = c.edges.nextSetBit(0); i >= 0; i = c.edges.nextSetBit(i + 1)) {
                thereIsNoSpoon[edgeFirstNodes[i]][edgeSecondNodes[i]] = 1;
                thereIsNoSpoon[edgeSecondNodes[i]][edgeFirstNodes[i]] = 1;
            }
            for (int i = 0; i < c.nodeCount; i++) {
                for (int j = 0; j < c.nodeCount; j++) System.out.print(thereIsNoSpoon[i][j]);
                System.out.println();
            }
        }
    }

    private void testAll() {
        for (int i = 3; i <= 10000; i++) {
            run(i, i);
        }
    }
    
    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 0; t < testCount; t++) {
                int targetCount = scanner.nextInt();
                run(t + 1, targetCount);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        SpanningPlanning sp = new SpanningPlanning();
        List<String> argList = Arrays.asList(args);
        if (argList.contains("--createPrecalcs")) {
            sp.precalculateAnswers();
        } else {
            sp.loadPrecalcs(new FileInputStream("resources/codejam2017/finals/C-precalc.in"));
            if (argList.contains("--testAll")) {
                sp.testAll();
            } else {
                sp.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/finals/C-small-practice.in") : System.in);
            }
        }
        System.err.println("SpanningPlanning done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

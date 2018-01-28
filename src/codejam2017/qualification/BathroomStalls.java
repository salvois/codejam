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
package codejam2017.qualification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * The Bathroom Stalls problem from Google Code Jam Qualification 2017.
 * https://code.google.com/codejam/contest/3264486/dashboard#s=p2
 *
 * Given the limits of the large dataset, I searched for an O(log n) or better
 * solution since the beginning.
 * We can observe that, whenever new people arrive, the row of stalls is
 * bisected like in a binary search. Thus, we can think of filling stalls
 * like a tree:
 * 
 * .1..   4 stalls for 1 person -> 2 1
 * 3_2.   3 stalls for 2 people -> 1 0, 0 0
 * ___4   1 stall for 1 person  -> 0 0
 * 
 * ..1..   5 stalls for 1 person -> 2 2
 * 2._3.   4 stalls for 2 people -> 1 0 x2
 * _4__5   2 stalls for 2 people -> 0 0 x2
 * 
 * ..1...   6 stalls for 1 person -> 3 2
 * 3._.2.   5 stalls for 2 people -> 1 1, 1 0
 * _4_5_6   3 stalls for 3 people -> 0 0 x3
 * 
 * .......1.......   15 stalls for 1 person -> 7 7
 * ...2..._...3...   14 stalls for 2 people -> 3 3 x2
 * .4._.5._.6._.7.   12 stalls for 4 people -> 1 1 x4
 * 8_9_a_b_c_d_e_f    8 stalls for 8 people -> 0 0 x8
 * 
 * ......1.......   14 stalls for 1 person -> 7 6
 * ..3..._...2...   13 stalls for 2 people -> 3 3, 3 2
 * 7._.4._.5._.6.   11 stalls for 4 people -> 1 1 x3, 1 0
 * _8_9_a_b_c_d_e    7 stalls for 7 people -> 0 0 x7
 * 
 * .......1........   16 stalls for 1 person -> 8 7
 * ...3..._...2....   15 stalls for 2 people -> 4 3, 3 3
 * .5._.6._.7._.4..   13 stalls for 4 people -> 2 1, 1 1 x3
 * 9_a_b_c_d_e_f_8.    9 stalls for 8 people -> 1 0, 0 0 x7
 * _______________g    1 stall for 1 person  -> 0 0
 * 
 * ...............1................   32 stalls for 1 person  -> 16 15
 * .......3......._.......2........   31 stalls for 2 people  -> 8 7, 7 7
 * ...5..._...6..._...7..._...4....   29 stalls for 4 people  -> 4 3, 3 3 x3
 * .9._.a._.b._.c._.d._.e._.f._.8..   25 stalls for 8 people  -> 2 1, 1 1 x7
 * h_i_j_k_l_m_n_o_p_q_r_s_t_u_v_g.   17 stalls for 16 people -> 1 0, 0 0 x15
 * 
 * 
 * ........1........   17 stalls for 1 person -> 8 8
 * ...2...._...3....   16 stalls for 2 people -> 4 3 x2
 * .6._.4.._.7._.5..   14 stalls for 4 people -> 2 1 x2, 1 1 x2
 * 
 * At each level of the tree we know how many people we can place, and how
 * many stalls they have to chose from. In the last level, we just have
 * to divide the count of available stalls by the people we have to place.
 * Turns out we can resolve the problem in constant time.
 * 
 * Special care to overflow! I firstly got the large dataset wrong because
 * I forgot the L suffix in the people calculation using shift...
 * 
 * @author Salvo Isaja
 */
public class BathroomStalls {

    private static final boolean DEBUG = false;

    private void solve(int testIndex, long stallCount, long peopleCount) {
        int level = Long.SIZE - Long.numberOfLeadingZeros(peopleCount); // 1-based tree level
        long prevLevelOccupiedStalls = (1L << (level - 1)) - 1;
        long currLevelFreeStalls = stallCount - prevLevelOccupiedStalls;
        long currLevelMaxPeople = 1L << (level - 1);
        if (prevLevelOccupiedStalls + currLevelMaxPeople > stallCount) currLevelMaxPeople = stallCount - prevLevelOccupiedStalls;
        long stallsPerPerson = currLevelFreeStalls / currLevelMaxPeople;
        long stallsPerPersonRemainder = currLevelFreeStalls % currLevelMaxPeople;
        long peopleLeft = peopleCount - prevLevelOccupiedStalls;
        if (peopleLeft <= stallsPerPersonRemainder) stallsPerPerson++;
        long minAdjacentEmptyStalls = (stallsPerPerson - 1) / 2;
        long maxAdjacentEmptyStalls = stallsPerPerson / 2;
        System.out.println("Case #" + testIndex + ": " + maxAdjacentEmptyStalls + " " + minAdjacentEmptyStalls);
    }
    
    private void scanTests(InputStream is) {
        try (Scanner scanner = new Scanner(is)) {
            int testCount = scanner.nextInt();
            for (int t = 1; t <= testCount; t++) {
                long stallCount = scanner.nextLong();
                long peopleCount = scanner.nextLong();
                solve(t, stallCount, peopleCount);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t = System.nanoTime();
        BathroomStalls tn = new BathroomStalls();
        tn.scanTests(DEBUG ? new FileInputStream("resources/codejam2017/qualification/C-large-practice.in") : System.in);
        System.err.println("BathroomStalls done in " + ((System.nanoTime() - t) / 1e9) + " seconds.");
    }
}

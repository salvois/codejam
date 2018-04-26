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
package codejam2017.round1c;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Parenting Partnering problem from Round 1C of Code Jam 2017.
 * https://codejam.withgoogle.com/codejam/contest/3274486/dashboard#s=p1
 * 
 * Let's consider a distribution of activities like: C.....CJ..J...J.J..C
 * (sample case 5), where know that the other partner must be parenting during
 * activities: J.....JC..C...C.C..J, and we need to fill the interval of free
 * times using up 12 hours per partner (with wrap-around).
 * 
 * To minimize the number of exchanges, we should first attempt to coalesce
 * intervals of free times where the same partner parents before and after,
 * that is between C and C and between J and J. Each coalesced interval results
 * in no exchanges. If the coalescing partner has not enough parenting time
 * to fill the interval, we need to handle the baby to the other partner
 * for a while (for example J.....J can become JJJJCCJ), resulting in two
 * exchanges. Thus, it is optimal to coalesce smaller free time intervals first.
 * 
 * For intervals of free time where two different partners parent before and
 * after (as in C..J), we need to add an exchange in any case, so we just
 * fill the free time interval from left to right with the remaining parenting
 * time (for example CCCJ or CCJJ or CJJJ, depending on how much time C has).
 * It is very important to do this scan after coalescing interval in the
 * previous scan, and not doing both in parallel, otherwise different-parent
 * intervals could steal useful parenting time to coalesce same-parent intervals
 * (I initially got the large dataset wrong due to this mistake...).
 * 
 * The O(n*log2(n)) of activities and free time intervals sorting dominates
 * the linear scan of intervals described above.
 * 
 * @author Salvo Isaja
 */
public class ParentingPartnering {

    private static final boolean DEBUG = false;
    private static final int CAMERON = 0;
    private static final int JAMIE = 1;
    
    private static final class Activity {
        public final int begin;
        public final int end;
        public final int parentingPartner;

        public Activity(int begin, int end, int parentingPartner) {
            this.begin = begin;
            this.end = end;
            this.parentingPartner = parentingPartner;
        }
    }

    private static final class FreeTime {
        public final int begin;
        public final int length;
        public final int parentingBefore;
        public final int parentingAfter;

        public FreeTime(int begin, int length, int parentingBefore, int parentingAfter) {
            this.begin = begin;
            this.length = length;
            this.parentingBefore = parentingBefore;
            this.parentingAfter = parentingAfter;
        }
    }

    private static int solve(Activity[] activities) {
        // Construct the intervals of free time and compute partners' parenting time
        Arrays.sort(activities, (a, b) -> a.begin - b.begin);
        FreeTime[] freeTimes = new FreeTime[activities.length];
        int[] parentingTimes = { 720, 720 }; // for each partner
        for (int i = 0; i < activities.length; i++) {
            int nextBegin;
            int nextPartner;
            if (i < activities.length - 1) {
                nextBegin = activities[i + 1].begin;
                nextPartner = activities[i + 1].parentingPartner;
            } else {
                nextBegin = activities[0].begin + 1440;
                nextPartner = activities[0].parentingPartner;
            }
            freeTimes[i] = new FreeTime(activities[i].end, nextBegin - activities[i].end, activities[i].parentingPartner, nextPartner);
            parentingTimes[activities[i].parentingPartner] -= activities[i].end - activities[i].begin;
        }
        Arrays.sort(freeTimes, (a, b) -> a.length - b.length);
        // Attempt to coalesce same-parent free time intervals (0 or 2 exchanges per interval)
        int exchangeCount = 0;
        for (FreeTime fi : freeTimes) {
            if (fi.parentingBefore != fi.parentingAfter) continue;
            if (parentingTimes[fi.parentingBefore] < fi.length) {
                exchangeCount += 2;
            }
            int partnerBeforeTime = Math.min(parentingTimes[fi.parentingBefore], fi.length);
            parentingTimes[fi.parentingBefore] -= partnerBeforeTime;
            parentingTimes[fi.parentingBefore ^ 1] -= fi.length - partnerBeforeTime;
        }
        // Fill different-parent free time intervals with the remaining parenting time (1 exchange per interval)
        for (FreeTime fi : freeTimes) {
            if (fi.parentingBefore == fi.parentingAfter) continue;
            exchangeCount++;
            int partnerBeforeTime = Math.min(parentingTimes[fi.parentingBefore], fi.length);
            parentingTimes[fi.parentingBefore] -= partnerBeforeTime;
            parentingTimes[fi.parentingBefore ^ 1] -= fi.length - partnerBeforeTime;
        }
        return exchangeCount;
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2017/round1c/B-large-practice.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int cameronActivityCount = scanner.nextInt();
                int jamieActivityCount = scanner.nextInt();
                Activity[] activities = new Activity[cameronActivityCount + jamieActivityCount];
                // Cameron's activities, Jamie's parenting
                for (int i = 0; i < cameronActivityCount; i++) {
                    activities[i] = new Activity(scanner.nextInt(), scanner.nextInt(), JAMIE);
                }
                // Jamie's activities, Cameron's parenting
                for (int i = 0; i < jamieActivityCount; i++) {
                    activities[i + cameronActivityCount] = new Activity(scanner.nextInt(), scanner.nextInt(), CAMERON);
                }
                int exchangeCount = solve(activities);
                System.out.println("Case #" + testNumber + ": " + exchangeCount);
            }
        }
        System.err.println("ParentingPartnering done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
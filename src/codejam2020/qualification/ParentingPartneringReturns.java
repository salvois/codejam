/*
Solutions for Code Jam 2020.
Copyright 2020 Salvatore ISAJA. All rights reserved.

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
package codejam2020.qualification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * The Parenting Partnering Returns problem from Google Code Jam Qualification 2020.
 * https://codingcompetitions.withgoogle.com/codejam/round/000000000019fd27/000000000020bdf9
 * 
 * Sort activities by begin time and assign one activity to Cameron and one
 * to Jamie, as long as they have completed the previous activity.
 * 
 * @author Salvo Isaja
 */
public class ParentingPartneringReturns {

    private static final boolean DEBUG = true;
    
    private static class Activity {
        int index;
        int beginTime;
        int endTime;

        public Activity(int index, int beginTime, int endTime) {
            this.index = index;
            this.beginTime = beginTime;
            this.endTime = endTime;
        }
    }
    
    /**
     * Fisher-Yates shuffle algorithm.
     * Courtesy of https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
     */
    private static <T> void shuffleArray(T[] a) {
        Random random = new Random();
        for (int i = a.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T t = a[j];
            a[j] = a[i];
            a[i] = t;
        }
    }
    
    private static String solve(Activity[] activities) {
        shuffleArray(activities);
        Arrays.sort(activities, (a, b) -> a.beginTime - b.beginTime);
        
        StringBuilder result = new StringBuilder(activities.length);
        for (int i = 0; i < activities.length; i++) result.append('X');

        Activity lastCameronActivity = null;
        Activity lastJamieActivity = null;
        for (int i = 0; i < activities.length; i++) {
            Activity activity = activities[i];
            if (i == 0) {
                result.setCharAt(activity.index, 'C');
                lastCameronActivity = activity;
                continue;
            }
            if (lastJamieActivity != null && activity.beginTime >= lastJamieActivity.endTime) {
                lastJamieActivity = null;
            }
            if (lastCameronActivity != null && activity.beginTime >= lastCameronActivity.endTime) {
                lastCameronActivity = null;
            }
            if (lastCameronActivity == null) {
                result.setCharAt(activity.index, 'C');
                lastCameronActivity = activity;
            } else if (lastJamieActivity == null) {
                result.setCharAt(activity.index, 'J');
                lastJamieActivity = activity;
            } else {
                return "IMPOSSIBLE";
            }
        }
        return result.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        long beginTime = System.nanoTime();
        InputStream is = DEBUG ? new FileInputStream("resources/codejam2020/qualification/ParentingPartneringReturns-1.in") : System.in;
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is)))) {
            int testCount = scanner.nextInt();
            for (int testNumber = 1; testNumber <= testCount; testNumber++) {
                int activityCount = scanner.nextInt();
                Activity[] activities = new Activity[activityCount];
                for (int i = 0; i < activityCount; i++) {
                    activities[i] = new Activity(i, scanner.nextInt(), scanner.nextInt());
                }
                String result = solve(activities);
                System.out.println("Case #" + testNumber + ": " + result);
            }
        }
        System.err.println("Done in " + ((System.nanoTime() - beginTime) / 1e9) + " seconds.");
    }
}
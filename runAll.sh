#!/bin/bash
java -cp dist/codejam.jar codejam2017.finals.DiceStraight < resources/codejam2017/finals/A-small-practice.in > resources/codejam2017/finals/A-small-practice.out
java -Xss16m -cp dist/codejam.jar codejam2017.finals.DiceStraight < resources/codejam2017/finals/A-large-practice.in > resources/codejam2017/finals/A-large-practice.out
java -cp dist/codejam.jar codejam2017.finals.Operation < resources/codejam2017/finals/B-small-practice.in > resources/codejam2017/finals/B-small-practice.out
java -cp dist/codejam.jar codejam2017.finals.Operation < resources/codejam2017/finals/B-large-practice.in > resources/codejam2017/finals/B-large-practice.out
java -cp dist/codejam.jar codejam2017.finals.SpanningPlanning --createPrecalcs > resources/codejam2017/finals/C-precalc.in
java -cp dist/codejam.jar codejam2017.finals.SpanningPlanning < resources/codejam2017/finals/C-small-practice.in > resources/codejam2017/finals/C-small-practice.out
java -cp dist/codejam.jar codejam2017.finals.Omnicircumnavigation < resources/codejam2017/finals/D-small-practice.in > resources/codejam2017/finals/D-small-practice.out
java -cp dist/codejam.jar codejam2017.finals.Omnicircumnavigation < resources/codejam2017/finals/D-large-practice.in > resources/codejam2017/finals/D-large-practice.out
java -cp dist/codejam.jar codejam2017.finals.StackManagementSmall < resources/codejam2017/finals/E-small-practice.in > resources/codejam2017/finals/E-small-practice.out
java -Xss16m -cp dist/codejam.jar codejam2017.finals.StackManagementCodejam < resources/codejam2017/finals/E-large-practice.in > resources/codejam2017/finals/E-large-practice.out
java -cp dist/codejam.jar codejam2017.finals.TeleportersSmall < resources/codejam2017/finals/F-small-practice.in > resources/codejam2017/finals/F-small-practice.out
java -cp dist/codejam.jar codejam2017.finals.TeleportersLarge < resources/codejam2017/finals/F-large-practice.in > resources/codejam2017/finals/F-large-practice.out

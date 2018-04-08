# Solutions to Code Jam 2017 and 2018 problems

This repository includes my solutions to the problems of the Google Code Jam 2017 and 2018 competitions.\
The sources are in Java, and each package contains the solution to one of the rounds. Each source file contains a description of the algorithm used.\
The *resources* directory contains input datasets, outputs and other support files.

A NetBeans project is included to build the solutions, and the *runAll.sh* script can be used to run all programs at once,
creating the outputs in the *resources* directory.

## Code Jam 2017
The repository contains the following programs. I wrote them for practice, and tested them against
the (now old) Code Jam platform in practice mode, everything being judged as correct.
* Package **codejam2017.finals**: World Finals round of Code Jam 2017.
    * **DiceStraight**: solution for the Dice Straight problem.
    * **OperationGenetic**: first attempt to solve the Operation problem using a genetic approach. Failed to found global optima in a few cases.
    * **Operation**: final attempt to solve the Operation problem.
    * **SpanningPlanning**: solution for the Spanning Planning problem.
        The program works by precalculating all possible spanning trees within the limits, then using a big lookup table to solve the problem.
        When run with the *--createPrecalcs* argument the program outputs precalculated answers, otherwise it reads them from a file under resources.
    * **Omnicircumnavigation**: that was my favorite.
    * **StackManagementSmall**: trivial solution to the Stack Management problem. Not appropriate for the large dataset.
    * **StackManagementLarge**: I couldn't figure this one out by myself. This is from the analysis on the Code Jam website.
    * **TeleportersSmall**: first attempt to solve the Teleporters problem. Not appropriate for the large dataset.
    * **TeleportersLarge**: final attempt to solve the Teleporters problem, using a smarter approach appropriate for the large dataset.
* Package **codejam2017.qualification**: Qualification round of Code Jam 2017.
    * **OversizedPancakeFlipper**: not the most optimized version, but able to handle the large dataset without problems.
    * **TidyNumbers**: finds tidy numbers with an algorithm proportional to the count of digits.
    * **BathroomStalls**: I'm particularly happy with the constant-time solution I found.
    * **FashionShow**: I gave this a lot of thought. Two tricky observations were needed to handle both datasets.

## Code Jam 2018
For Code Jam 2018, I'm actually participating to the contest. Below are my solutions,
either as submitted or coded on a second thought. Due to the new contest rules, where dataset
files are no longer provided, the files under the *codejam2018* subdirectories of *resources*
are the ones I assembled from the provided examples and my own test cases.
Unless otherwise noted, everything is judged as correct.
* Package **codejam2018.practice**: problems of the practice session of the new platform.
    * **NumberGuessing**: the new interactive type of problem.
    * **SenateEvacuationMT**: the Senate Evacuation problem, solved with multiple threads just because.
    * **Steed2CruiseControl**: Steed 2: Cruise Control turned out to be so simple I couldn't believe it!
    * There is no source for the fourth problem, Bathroom Stalls, because I lifted it as is
      from my Code Jam 2017 solutions (see package *codejam2017.qualification*).
* Package **codejam2018.qualification**: Qualification round for Code Jam 2018.
    * **SavingTheUniverseAgain**: pretty straightforward  solution for the Saving The Universe Again problem.
    * **TroubleSortSmallMT**: solution for the small dataset (set 1) of the Trouble Sort problem. Unfortunately I misread the limits and incorrectly convinced myself that a multi-threaded solution was enough for the large dataset (set 2) too.
    * **TroubleSortLarge**: the proper solution to the Trouble Sort problem, coded after the competition, hence not validated by the judge.
    * **GoGopher**: to solve the interactive Go, Gopher! problem I opted for the simple approach of filling a 3-cell-tall strip from left to right.
    * **CubicUfo**: solving the Cubic UFO geometric problem was a lot of fun. I played safely and tried to reproduce the evaluation of the judge, using binary search. Special thanks to the cubic candle lying in my kitchen!

## Why Java?
IDE support, debugger, standard library, good performance, ubiquity.\
Why not, to name one, C++? Templates, basically. While I recognize templates are very useful,
I think modern C++ has gone a little overboard with templates, and I hate error messages involving templates.

## Licensing terms
Permissive, two-clause BSD license.
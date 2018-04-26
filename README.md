# Solutions to Code Jam 2017 and 2018 problems

This repository includes my solutions to the problems of the Google Code Jam 2017 and 2018 competitions.\
The sources are in Java, and each package contains the solution to one of the rounds. Each source file contains a description of the algorithm used.\
The *resources* directory contains input datasets, outputs and other support files.\
A NetBeans project is included to build the solutions.

## Code Jam 2017
The repository contains the following programs. I wrote them for practice, and tested them against
the (now old) Code Jam platform in practice mode, everything being judged as correct.\
The *runAll.sh* script in the root of the repository can be used to run all programs at once,
creating the outputs in the *resources* directory.

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
* Package **codejam2017.round1a**: Round 1A of Code Jam 2017.
    * **AlphabetCake**: filling the alphabet cake horizontally then vertically.
    * I have not yet solved Ratatouille and Play the dragon.
* Package **codejam2017.round1b**: Round 1B of Code Jam 2017.
    * Steed 2: Cruise Control can be found in the *codejam2018.practice* package of Code Jam 2018.
    * **StableNeighbors**: the Stable Neigh-bors problem turned out to be tricky.
      My solution is inspired by thread scheduling in operating systems.
    * **PonyExpressSmall**: solution for the small dataset of the Pony Express problem,
      where the resulting graph is a DAG.
    * **PonyExpressLarge**: solution for the large dataset of the Pony Express problem,
      using the Dijkstra's algorithm to find the shortest paths.
* Package **codejam2017.round1c**: Round 1C of Code Jam 2017.
    * **AmpleSyrup**: greedy solution to stack pancakes giving ample surface for syrup!
    * **ParentingPartnering**: coalescing free time intervals between activities performed
      by the same parent was the key to efficient parenting partnering.
    * **CoreTrainingSmall1**: solution for the small dataset 1 of the Core Training problem,
      maximizing the probability product by increasing the lowest probabilities.
    * I was not able to figure out how to cope with small dataset 2 of the Core Training problem.

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
    * **TroubleSortSmallMT**: solution for the small dataset (set 1) of the Trouble Sort problem.
      Unfortunately I misread the limits and incorrectly convinced myself that
      a multi-threaded solution was enough for the large dataset (set 2) too.
    * **TroubleSortLarge**: the proper solution to the Trouble Sort problem,
      coded after the competition, and validated by the judge in practice mode.
    * **GoGopher**: to solve the interactive Go, Gopher! problem I opted for the
      simple approach of filling a 3-cell-tall strip from left to right.
    * **CubicUfo**: solving the Cubic UFO geometric problem was a lot of fun.
      I played safely and tried to reproduce the evaluation of the judge, using
      binary search. Special thanks to the cubic candle lying in my kitchen!
* Package **codejam2018.round1a**: I was not awake enough for round 1A.
  I coded these solutions after the round took place, and had them judged as correct in practice mode.
    * **WaffleChoppers**: rectangular cumulative sums to the rescue.
    * **BitParty**: that was fun! Bit Party had huge limits but required some
      imagination to figure out that binary search was the key.
    * **EdgyBaking**: A 0-1 knapsack problem, solvable with dynamic programming,
      was well disguised under Mr. Maillard's edgy baking.

## Why Java?
IDE support, debugger, standard library, good performance, ubiquity.\
Why not, to name one, C++? Templates, basically. While I recognize templates are very useful,
I think modern C++ has gone a little overboard with templates, and I hate error messages involving templates.\
I miss value types, generics on primitives and -very rarely- operator overloading, though.

## Licensing terms
Permissive, two-clause BSD license.
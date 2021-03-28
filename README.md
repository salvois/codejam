# Solutions to problems of Code Jam 2021 and earlier

This repository includes my solutions to the problems of the Google Code Jam
competitions up to year 2021, either as submitted during the contest or made on a second thought.

The sources are in Java, and each source file contains a description of the algorithm used.
The Java packages in the *src* directory contain source code grouped by Code Jam edition and round.
Unless otherwise noted, everything is judged as correct, either in contest or under practice mode.

The *resources* directory contains input datasets, outputs and other support files.
Since Code Jam 2018 dataset files are no longer provided, thus what is included here
is what I assembled from the provided examples and my own test cases.

NetBeans is still my favorite IDE, so a NetBeans project is included.

## Code Jam 2021

* Qualification round:
    * **Reversort**: just the implementation of the proposed algorithm.
    * **Moons and Umbrellas**: linear greedy solution minimizing the cost, judged as Wrong Answer for the "extra credit" dataset.
    * **Reversort Engineering**: reasoning about the maximum number of reversal operation for each element was the key.

## Code Jam 2020

* Qualification round:
    * **Vestigium**: trivial O(n*n) solution scanning rows and columns for duplicates.
    * **Nesting Depth**: linear solution comparing the current depth with the next digit.
    * **Parenting Partnering Returns**: O(n log n) solution performing a linear scan on sorted activities.
    * **ESAb ATAd**: very fun interactive problem, solved by querying two specular bits
                     every 10 queries to detect the kind of "quantum fluctuation".

## Code Jam 2019

* Qualification round:
    * **Foregone Solution**: O(n) solution.
    * **You Can Go Your Own Way**: O(n) solution.
    * **Cryptopangrams** in two versions: a small dataset version as submitted during the contest,
                          and a retrospective version able to handle the large dataset.
* Round 1B:
    * **Manhattan Crepe Cart**: small dataset version completed after the contest.
    * **Draupnir**: constant time solution making two round-trips with the judge.
    * **Fair Fight**: small dataset version written after the contest.

## Code Jam 2018

* Practice session of the new platform:
    * **NumberGuessing**: the new interactive type of problem.
    * **SenateEvacuationMT**: the Senate Evacuation problem, solved with multiple threads just because.
    * **Steed2CruiseControl**: Steed 2: Cruise Control turned out to be so simple I couldn't believe it!
    * There is no source for the fourth problem, Bathroom Stalls, because I lifted it as is
      from my Code Jam 2017 solutions (see package *codejam2017.qualification*).
* Qualification round:
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
* Round 1A:
    * **WaffleChoppers**: rectangular cumulative sums to the rescue.
    * **BitParty**: that was fun! Bit Party had huge limits but required some
      imagination to figure out that binary search was the key.
    * **EdgyBaking**: A 0-1 knapsack problem, solvable with dynamic programming,
      was well disguised under Mr. Maillard's edgy baking.
* Round 1B:
    * **RoundingError**: Rounding Error could be solved by increasing percentages
      whose fractional part is < 0.5.
    * I have not yet solved Mysterious Road Signs and Transmuation.
* Round 1C:
    * **AWholeNewWordSmall**: Brute-force scan of all the 26^2 combinations for
      test set 1 of the A Whole New Word problem.
    * **AWholeNewWordLarge**: I didn't consider that brute-force would have worked
      for the large dataset too. And it costed me the contest...
    * **LollipopShop**: this solutions gives away less popular lollipops first.
    * **AntStackSmall**: the small dataset of Ant Stack is yet another 0-1 knapsack.
    * **AntStackLarge**: I needed help from the official Code Jam analysis for this.
      This is similar to the knapsack, but with inverted logic.
    * **AntStackGenerator.py**: Python script to find out the largest possible stack.

## Code Jam 2017

I did not participate to the context, but I wrote those solution for practice,
and tested them against the (now old) Code Jam platform in practice mode.\
The *runAll.sh* script in the root of the repository can be used to run all programs at once,
creating the outputs in the *resources* directory.

* World Finals round:
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
* Qualification round:
    * **OversizedPancakeFlipper**: not the most optimized version, but able to handle the large dataset without problems.
    * **TidyNumbers**: finds tidy numbers with an algorithm proportional to the count of digits.
    * **BathroomStalls**: I'm particularly happy with the constant-time solution I found.
    * **FashionShow**: I gave this a lot of thought. Two tricky observations were needed to handle both datasets.
* Round 1A:
    * **AlphabetCake**: filling the alphabet cake horizontally then vertically.
    * I have not yet solved Ratatouille and Play the dragon.
* Round 1B:
    * Steed 2: Cruise Control can be found in the *codejam2018.practice* package of Code Jam 2018.
    * **StableNeighbors**: the Stable Neigh-bors problem turned out to be tricky.
      My solution is inspired by thread scheduling in operating systems.
    * **PonyExpressSmall**: solution for the small dataset of the Pony Express problem,
      where the resulting graph is a DAG.
    * **PonyExpressLarge**: solution for the large dataset of the Pony Express problem,
      using the Dijkstra's algorithm to find the shortest paths.
* Round 1C:
    * **AmpleSyrup**: greedy solution to stack pancakes giving ample surface for syrup!
    * **ParentingPartnering**: coalescing free time intervals between activities performed
      by the same parent was the key to efficient parenting partnering.
    * **CoreTrainingSmall1**: solution for the small dataset 1 of the Core Training problem,
      maximizing the probability product by increasing the lowest probabilities.
    * I was not able to figure out how to cope with small dataset 2 of the Core Training problem.

## Previous editions
I solved some problems from past editions for practicing. Each top level package refers to a year,
with second level packages for rounds.
* codejam2016: round1b, round1c
* codejam2015: round1c

## Why Java?
IDE support, debugger, standard library, good performance, ubiquity.\
Why not, to name one, C++? Templates, basically. While I recognize templates are very useful,
I think modern C++ has gone a little overboard with templates, and I hate error messages involving templates.\
I miss value types, generics on primitives and -very rarely- operator overloading, though.

## Licensing terms
Permissive, two-clause BSD license.

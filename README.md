# Solutions to Code Jam 2017 problems.

This repository includes my solutions to the problems of the Google Code Jam 2017 competition.\
The sources are in Java, and each package contains the solution to one of the rounds. Each source file contains a description of the algorithm used.\
The *resources* directory contains input datasets, outputs and other support files.

A NetBeans project is included to build the solutions, and the *runAll.sh* script can be used to run all programs at once,
creating the outputs in the *resources* directory.

## Contents
The repository contains the following programs. All of them produce correct output according to the Code Jam website.
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

## Why Java?
IDE support, debugger, standard library, good performance, ubiquity.\
Why not, to name one, C++? Templates, basically. While I recognize templates are very useful,
I think modern C++ has gone a little overboard with templates, and I hate error messages involving templates.

## Licensing terms
Permissive, two-clause BSD license.
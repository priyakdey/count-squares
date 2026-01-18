# Analysis for Count Squares Algorithmic Problem

Efficient algorithms and benchmarks for counting axis-agnostic squares from a
set of 2D integer points.

This repository explores algorithmic, data-structure, and memory trade-offs for
the classic **Count Number of Squares** problem, moving from a naive geometric
solution to highly optimized primitive-based implementations, with JMH
benchmarks across different input distributions.


## Problem Statement

Write a function that takes in a list of Cartesian coordinates (i.e., `(x, y)`
coordinates) and returns the number of squares that can be formed by these
coordinates.

A square must have its four corners amongst the coordinates in order to be
counted. A single coordinate can be used as a corner for multiple different
squares.

**Sample Input:**
```python
points = [[1, 1], [0, 0], [-4, 2], [-2, -1], [0, 1], [1, 0], [-1, 4]]
```

**Sample Output:**
```python
2  # [1, 1], [0, 0], [0, 1], and [1, 0] makes a square,
   # as does [1, 1], [-4, 2], [-2, -1], and [-1, 4]
```

[!NOTE]
This problem is from the [algoexpert.io](https://alogexpert.io) platform.
[Link to problem](https://www.algoexpert.io/questions/count-squares)

## Clarification

Given `n` distinct integer points in a 2D plane, count the number of unique
squares that can be formed using any 4 points.

- Squares may be rotated (not necessarily axis-aligned)
- Each square is counted once
- Coordinates are integers
- Max distance from origin is bounded `(≤ 100)`


_To be updated_


## LICENSE

All code written here is published under the [MIT License](./LICENSE).

The problem statement (the content itself) is owned by the creators of
[algoexpert.io](https://algoexpert.io).

The blog and video content derived from this work is the property of the author,
[Priyak Dey](https://priyakdey.com).

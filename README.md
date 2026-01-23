# Analysis for Count Squares Algorithmic Problem

Efficient algorithms and benchmarks for counting axis-agnostic squares from a
set of 2D integer points.

This repository accompanies a deep-dive blog post on algorithmic design and 
performance engineering. Starting from a naive geometric solution, it 
incrementally evolves toward cache-friendly, allocation-free implementations, 
backed by JMH benchmarks, memory analysis, and visualized results.

> 📖 Blog: [Counting Squares — When Geometry Meets Performance](https://priyakdey.com/posts/software/counting-squares/)
> 
> 🎥 Video: _To be updated_

---

- [Analysis for Count Squares Algorithmic Problem](#analysis-for-count-squares-algorithmic-problem)
  - [Problem Statement](#problem-statement)
  - [Repository Structure](#repository-structure)
  - [Implementation Overview](#implementation-overview)
  - [Running Benchmarks](#running-benchmarks)
    - [Prerequisites](#prerequisites)
    - [Run all benchmarks](#run-all-benchmarks)
  - [Running Analysis \& Plotting](#running-analysis--plotting)
    - [Prerequisites](#prerequisites-1)
    - [Run analysis](#run-analysis)
    - [Why Two Datasets?](#why-two-datasets)
  - [Key Takeaways](#key-takeaways)
  - [LICENSE](#license)


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

Given a set of distinct integer points in a 2D plane, count the number of 
unique squares that can be formed using those points as vertices.
- Squares may be rotated (not necessarily axis-aligned)
- Each square is counted once
- Coordinates are integers
- Maximum distance from origin is bounded (≤ 100)

> [!NOTE]
> This problem is from the [algoexpert.io](https://alogexpert.io) platform.
> [Link to problem](https://www.algoexpert.io/questions/count-squares)

---

## Repository Structure

```bash
.
├── src/
│   ├── main/java/com/priyakdey/        # Implementations
│   └── jmh/java/com/priyakdey/         # JMH benchmarks
├── analysis/
│   ├── data/                           # Raw + parsed benchmark data
│   ├── plots/                          # Generated plots (time & memory)
│   ├── scripts/                        # Parsing & plotting utilities
|   └── Malefile                        # target to run analysis scripts
├── Makefile                            # target to run benchmark
└── README.md
```

---

## Implementation Overview

This repository intentionally contains multiple implementations of the same logical algorithm to explore trade-offs in time complexity, memory allocation, and data structure choice.

1. CountSquaresNaive
    - Brute-force solution
    - Checks every 4-point combination
    - Time complexity: O(n⁴)
    - Serves as a correctness baseline
1. CountSquaresPointSet
    - Uses geometric properties of square diagonals
    - Stores points as boxed objects in a HashSet
    - Eliminates floating-point arithmetic by scaling coordinates
    - Time complexity: O(n²)
    - Demonstrates algorithmic improvement with standard collections
1. CountSquaresLongSet
    - Same algorithm as PointSet
    - Packs (x, y) into a single long key
    - Avoids object allocation during lookup
    - Uses HashSet<Long>
    - Highlights boxing vs primitive representation costs
1. CountSquaresPrimitiveLongSet
    - Fully primitive implementation
    - Uses a custom open-addressing hash set
    - Zero boxing, predictable memory layout
    - Designed to minimize GC pressure
    - Represents the performance ceiling for this approach
1. PrimitiveLongHashSet
    - Custom primitive long hash set
    - Linear probing with power-of-two sizing
    - Tunable load factor
    - MurmurHash3-style mixing
    - Built specifically to study cache behavior and allocation costs

## Running Benchmarks

Benchmarks are executed using JMH via Gradle.

### Prerequisites

- Java 21+ (tested with OpenJDK 25)
- Gradle

### Run all benchmarks

```bash
make jmh
```

This will:
- Run the full JMH suite
- Produce results.json
- Copy it to analysis/data/results.json

> [!WARNING]
> Running benchmarks can take several minutes depending on machine and JVM.

## Running Analysis & Plotting

All analysis steps live under the `analysis/` directory.

### Prerequisites
- Python 3.13+

### Run analysis

```bash
cd analysis
python3 -m venv .venv
source .venv/bin/activate
pip install -r scripts/requirements.txt

make parse  # parses the data/results.json file
make time   # creates the plots for time per operations data
make mem    # creates the plots for allocations per operations data
```

The input and output files are customizable via cli. Check the `analysis/Makefile`
to see how to use the cli args.


### Why Two Datasets?

| Dataset | Purpose                                                      |
| ------- | ------------------------------------------------------------ |
| GRID    | Maximizes square density, worst-case geometry                |
| RANDOM  | Reduces geometric structure, tests hashing & lookup behavior |

Using both reveals how algorithmic complexity and memory behavior diverge under different spatial distributions.

---

## Key Takeaways

- Algorithmic complexity alone is not enough
- Data representation (objects vs primitives) matters
- Hashing strategy and memory layout directly affect performance
- JMH + visualization exposes trade-offs invisible in Big-O notation

---

## LICENSE

All code written here is published under the [MIT License](./LICENSE).

The problem statement (the content itself) is owned by the creators of
[algoexpert.io](https://algoexpert.io).

The blog and video content derived from this work is the property of the author,
[Priyak Dey](https://priyakdey.com).

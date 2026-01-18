"""
parse_jmh.py

Usage:
    python3 parse_jmh.py --in <input_file.json> --out <output_file.csv>

Parse JMH JSON into a csv file for plotting.

Expected JMH JSON shape (JMH 1.37+)
[
  {
    "benchmark": "com.priyakdey.CountSquaresBench.longSet",
    "params": {"dist": "RANDOM", "n": "24"},
    "primaryMetric": {"score": 1.0, "scoreUnit": "us/op", ...},
    "secondaryMetrics": {
      "gc.alloc.rate.norm": {"score": 8264.0, "scoreUnit": "B/op", ...},
      ...
    }
  },
  ...
]
"""


import argparse
import csv
from dataclasses import dataclass
import json
from pathlib import Path

# Change to False, to remove debug logs
DEBUG = False 


@dataclass(frozen=True)
class Row:
    variant: str
    dist: str
    n: int
    us_per_op: float
    bytes_per_op: float


def main() -> int:
    ap = argparse.ArgumentParser(description="Parse JMH json into csv file")
    ap.add_argument("-i", "--in", dest="inp", required=True, type=Path,
                    help="Path to input file")
    ap.add_argument("-o", "--out", dest="out", required=True, type=Path,
                    help="Path to output file")

    
    args = ap.parse_args()

    input_file = Path(args.inp)
    output_file = Path(args.out)
    
    data: list | None = None

    with open(input_file) as fp:
        data = json.load(fp)
        
        if not isinstance(data, list):
            print("ERROR: The json file is not of the correct format.")
            return 1
        
    if data is None:
        print("ERROR: Expecting non-empty input file")
        return 1
    
    print(f"Parsing {input_file}....")

    rows: list[Row] = []

    for item in data:
        benchmark = item["benchmark"]
        variant = benchmark.split(".")[-1]
        dist = item["params"]["dist"]
        try:
            n = int(item["params"]["n"])
        except ValueError:
            print("ERROR: Expected number for input param - `bench.params.n`")
            return 1

        try:
            us_per_op = float(item["primaryMetric"]["score"])
        except ValueError:
            print("ERROR: Expected number for us/ops - `bench.primaryMetric.score`")
            return 1

        try:
            bytes_per_op = float(item["secondaryMetrics"]["gc.alloc.rate.norm"]["score"])
        except ValueError:
            print("ERROR: Expected number for B/ops - `bench.secondaryMetrics.gc.alloc.rate.norm.score`")
            return 1
        
        row = Row(variant, dist, n, us_per_op, bytes_per_op)
        if DEBUG:
            print(row)

        rows.append(row) 

        
    print(f"Writing data to {output_file}")

    with open(output_file, "w") as fp:
        writer = csv.writer(fp)

        writer.writerow(["variant", "dist", "n", "microseconds_per_op", 
                         "bytes_allocated_per_op"])

        for row in rows:
            writer.writerow([row.variant, row.dist, row.n, row.us_per_op, 
                             row.bytes_per_op])


    return 0

if __name__ == "__main__":
    exit(main())


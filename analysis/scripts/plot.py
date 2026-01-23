"""
plot.py

Usage:
  python3 plot.py -i <input.csv> -o <output.png> -d GRID|RANDOM -t time|mem

- time: plots ops/sec vs n   (derived from us/op)
- mem : plots bytes/op vs n
"""

import argparse
import csv
from collections import defaultdict
from pathlib import Path

import matplotlib.pyplot as plt

def read_rows(csv_path: Path) -> list[dict]:
    with open(csv_path, "r", newline="") as fp:
        reader = csv.DictReader(fp)
        return list(reader)

def to_int(x: str) -> int:
    return int(x.strip())

def to_float(x: str) -> float:
    return float(x.strip())


def plot(
    rows: list[dict],
    dist: str,
    plot_type: str,     # "time" | "mem"
    out_path: Path
) -> None:
    # Filter by dist + exclusions
    data = []
    for r in rows:
        if r["dist"] != dist:
            continue

        n = to_int(r["n"])
        us_per_op = to_float(r["microseconds_per_op"])
        bytes_per_op = to_float(r["bytes_allocated_per_op"])
        
        print(dist, bytes_per_op)

        if plot_type == "time":
            # ops/sec = 1e6 / (us/op)
            y = us_per_op 
        else:
            y = bytes_per_op

        data.append((r["variant"], n, y))

    if not data:
        raise SystemExit(f"No data found for dist={dist} with plot_type={plot_type}")

    # Group by variant -> list of (n, y)
    by_variant: Dict[str, list[tuple[int, float]]] = defaultdict(list)
    for variant, n, y in data:
        by_variant[variant].append((n, y))

    # Publication-style defaults
    plt.figure(figsize=(10, 6), dpi=180)

    for variant, pts in sorted(by_variant.items(), key=lambda kv: kv[0]):
        pts.sort(key=lambda p: p[0])
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        plt.plot(xs, ys, marker="o", linewidth=2, label=variant)

    # Axes + scales
    plt.xscale("log", base=2)

    # y-scale:
    # - mem is almost always better in log (wide range)
    # - ops/sec also often spans big range; log makes comparisons readable
    plt.yscale("log", base=2)

    plt.xlabel("n (number of points)")

    if plot_type == "time":
        plt.ylabel("time per op (μsec/op)")
        title = f"TIME TAKEN PER OPERATION | DATA DISTRIBUTION={dist}"
    else:
        plt.ylabel("allocations per operation (bytes/op)")
        title = f"ALLOCATIONS PER OPERATION | DATA DISTRIBUTION={dist}"

    plt.title(title)

    plt.grid(True, which="both", linestyle="--", linewidth=0.6, alpha=0.6)
    plt.legend(title="variant", frameon=True)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    plt.tight_layout()
    plt.savefig(out_path, format="png", dpi=300)
    plt.close()

def main() -> int:
    ap = argparse.ArgumentParser(description="Plot JMH CSV (time or mem) for a given dist.")
    ap.add_argument("-i", "--in", dest="inp", required=True, type=Path, help="Input CSV")
    ap.add_argument("-o", "--out", dest="out", required=True, type=Path, help="Output PNG")
    ap.add_argument("-d", "--dist", dest="dist", required=True, choices=["GRID", "RANDOM"], help="Distribution")
    ap.add_argument("-t", "--type", dest="ptype", required=True, choices=["time", "mem"], help="Plot type")

    args = ap.parse_args()

    args = ap.parse_args()

    rows = read_rows(args.inp)
    plot(rows, args.dist, args.ptype, args.out)

    print(f"Wrote plot -> {args.out}")
    return 0

if __name__ == "__main__":
    exit(main())



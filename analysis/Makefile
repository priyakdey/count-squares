.PHONY: parse time mem

parse:
	python3 scripts/parse_jmh.py --in data/results.json --out data/output.csv

time:
	python3 scripts/plot.py -i data/output.csv -o plots/grid_time_per_operation.png -d GRID -t time
	python3 scripts/plot.py -i data/output.csv -o plots/random_time_per_operation.png -d RANDOM -t time

mem:
	python3 scripts/plot.py -i data/output.csv -o plots/grid_mem_per_operation.png -d GRID -t mem
	python3 scripts/plot.py -i data/output.csv -o plots/random_mem_per_operation.png -d RANDOM -t mem

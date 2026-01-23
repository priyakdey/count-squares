.PHONY: parse time mem

parse:
	python3 parse_jmh.py --in results.json --out output.csv

time:
	python3 plot.py -i output.csv -o ../plots/grid_time_per_operation.png -d GRID -t time
	python3 plot.py -i output.csv -o ../plots/random_time_per_operation.png -d RANDOM -t time

mem:
	python3 plot.py -i output.csv -o ../plots/grid_mem_per_operation.png -d GRID -t mem
	python3 plot.py -i output.csv -o ../plots/random_mem_per_operation.png -d RANDOM -t mem

.PHONY: jmh

jmh:
	./gradlew jmh
	cp -vp build/results/jmh/results.json analysis/data

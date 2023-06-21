#!/bin/bash

set -e

NUM_OF_REP=3
FROM=10
TO=100
STEP_SIZE=10

for (( i=$FROM; i<=$TO; i+=$STEP_SIZE )); do
    for j in {1..$NUM_OF_REP}; do
        ./run.sh $i
    done
done

echo "=> Eval script finished.";

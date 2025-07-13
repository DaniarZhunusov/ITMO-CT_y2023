#!/bin/bash

array=()
N=$1
while true; do
    for i in {1..10}; do
            array+=($i)
        done
        
    if [ "${#array[@]}" -gt "$N" ]; then
    	    echo "The array is full ($N)"
    	    exit 0
        fi
done

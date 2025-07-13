#!/bin/bash

> report.log

array=()
counter=0

while true; do
  array+=({1..10})
  
  ((counter++))
  
  if ((counter % 100000 == 0)); then
    echo "Step: $counter, Array size: ${#array[@]}" >> report.log
  fi
done


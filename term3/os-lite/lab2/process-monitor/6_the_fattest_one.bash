#!/bin/bash

output_file="6_task.txt"
max_mem=0
max_pid=0

for pid in /proc/[0-9]*; do
    pid=${pid##*/}

    if [[ -f /proc/$pid/status ]]; then
        mem=$(grep -i VmRSS /proc/$pid/status | awk '{print $2}')

        if [[ -n $mem ]] && [[ $mem -gt $max_mem ]]; then
            max_mem=$mem
            max_pid=$pid
        fi
    fi
done

echo "Process with PID $max_pid is using the highest amount of memory: $max_mem." > "$output_file"


#!/bin/bash

output_file="4_task.txt"

for pid in $(ps -e -o pid=)
do
    status="/proc/$pid/status"
    sched="/proc/$pid/sched"

    if [[ -f "$status" && -f "$sched" ]]; then
        parent_pid=$(grep "^PPid:" "$status" | awk '{print $2}')
        sum_runtime=$(grep "se.sum_exec_runtime" "$sched" | awk '{print $3}')
        switch=$(grep "nr_switches" "$sched" | awk '{print $3}')
        avg_runtime=$(echo "$sum_runtime $switch" | awk '{print $1 / $2}')
        
        echo "ProcessID=$pid : Parent_ProcessID=$parent_pid : Average_Running_Time=$avg_runtime"
    fi
done | sort -t "=" -k3n > "$output_file"


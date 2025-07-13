#!/bin/bash

input="4_task.txt"
output="5_task.txt"

cur_parent=""
total_time=0
proc_count=0

write_avg() {
    if (( proc_count > 0 )); then
        avg_runtime=$(awk -v total="$total_time" -v count="$proc_count" 'BEGIN { print total / count }')
        echo "Average_Running_Children_of_ParentID=$cur_parent is $avg_runtime" >> "$output"
    fi
}

> "$output"

while read -r entry; do
    parent_id=$(awk -F '[:=]' '{print $4}' <<< "$entry")
    run_time=$(awk -F '[:=]' '{print $6}' <<< "$entry")

    if [[ -z $cur_parent || $parent_id -ne $cur_parent ]]; then
        write_avg
        cur_parent="$parent_id"
        total_time=0
        proc_count=0
    fi

    total_time=$(awk -v total=$total_time -v rt=$run_time 'BEGIN { print total + rt }')
    (( proc_count++ ))

    echo "$entry" >> "$output"
done < "$input"
write_avg

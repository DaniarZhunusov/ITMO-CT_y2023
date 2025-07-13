#!/bin/bash

top_output_file="top_output.txt"

top -b -o +%MEM | head -n 17 | tail -n 10 > "$top_output_file"

process_info=$(head -n 10 "$top_output_file")

declare -A user_mem

while read -r line; do
    user=$(echo "$line" | awk '{print $2}')
    mem=$(echo "$line" | awk '{print $10}' | sed 's/,/./')

    if [[ -n "$user" && "$mem" =~ ^[0-9]+([.][0-9]+)?$ ]]; then
        user_mem["$user"]=$(echo "${user_mem[$user]:-0} + $mem" | bc)
    fi
done <<< "$process_info"

max_user=""
max_mem=0

for user in "${!user_mem[@]}"; do
    mem=${user_mem[$user]}
    if (( $(echo "$mem > $max_mem" | bc -l) )); then
        max_mem=$mem
        max_user=$user
    fi
done

echo "User consuming most memory: $max_user with total memory usage: $max_mem%" | tee -a "$top_output_file"

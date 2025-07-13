#!/bin/bash

old_file="old_file.txt"
new_file="new_file.txt"

collect_bytes_info() {
    local output_file=$1

    for pid in $(ps -A -o pid | tail -n +2); do
        local io_path="/proc/$pid/io"
        if [[ -f $io_path && -r $io_path ]]; then
            local bytes=$(grep -iw "read_bytes:" $io_path | awk '{print $2}')
            local cmd=$(tr '\0' ' ' < "/proc/${pid}/cmdline")
            echo "$pid $bytes $cmd" >> "$output_file"
        fi
    done
}

collect_bytes_info "$old_file"
echo "Wait 1 minute"
sleep 60  

collect_bytes_info "$new_file"

echo "Results:"
while read -r pid bytes_old cmd; do
    bytes_new=$(grep -E "^$pid " "$new_file" | awk '{print $2}')
    
    if [[ -n $bytes_new && -n $bytes_old && $bytes_new =~ ^[0-9]+$ && $bytes_old =~ ^[0-9]+$ ]]; then
        delta_bytes=$((bytes_new - bytes_old))
        echo "$pid:$cmd:$delta_bytes"
    fi
done < "$old_file" | sort -t ':' -nr -k3 | head -n 3

rm -f $new_file $old_file

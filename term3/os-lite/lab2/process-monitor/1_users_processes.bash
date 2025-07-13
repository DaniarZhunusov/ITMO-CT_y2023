#!/bin/bash

process_list=$(ps -u "$USER" -o pid,args --no-headers)
process_count=$(echo "$process_list" | wc -l)
echo "$process_count" > 1_task.txt
echo "$process_list" | awk '{print $1 ":" $2}' >> 1_task.txt


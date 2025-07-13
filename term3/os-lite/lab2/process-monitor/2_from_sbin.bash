#!/bin/bash

file="2_task.txt"
ps -eo pid,cmd | awk '$2 ~ /^\/sbin\// {print $1}' > "$file"


#!/bin/bash
test="$HOME/test"
archive="$test/archived"
report="$HOME/report"
date=$(date +"%Y-%m-%d")
datetime=$(date +"%Y-%m-%d_%H-%M-%S")

mkdir -p "$test" "$archive"
[ ! -f "$report" ] && echo "$(date +"%Y-%m-%d %H:%M:%S") test was created successfully" >> "$report"
touch "$test/$datetime"
find "$test" -maxdepth 1 -type f -name "*_*-*-*" ! -name "*$date*" \
    -exec tar -czf "$archive/$date.tar.gz" -C "$test" $(basename {}) \; \
    -exec rm {} \;
    
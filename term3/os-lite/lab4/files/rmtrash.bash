#!/usr/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Error: One argument expected - file name."
    exit 1
fi

target_file="$1"

if [ ! -f "$target_file" ]; then
    echo "Error: File '$target_file' does not exist."
    exit 1
fi

trash_dir="$HOME/.trash"
if [ ! -d "$trash_dir" ]; then
    mkdir "$trash_dir"
fi

link_name="$target_file"
counter=1
while [ -e "$trash_dir/$link_name" ]; do
    link_name="${target_file}${counter}"
    counter=$((counter + 1))
done

ln "$target_file" "$trash_dir/$link_name"

rm -f "$target_file"

log_file="$HOME/.trash.log"

touch "$log_file"
echo "$(realpath "$target_file") : $link_name" >> "$log_file"
echo "The file '$target_file' has been moved to the trash."

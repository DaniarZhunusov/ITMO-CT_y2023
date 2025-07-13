#!/usr/bin/bash

function get_file_size() {
    wc -c < "$1"
}

source_dir="$HOME/source"
backup_dir="$HOME"
report_file="$HOME/backup-report"

if [[ ! -d "$source_dir" ]]; then
    echo "Source directory $source_dir not found."
    exit 1
fi

[[ ! -f "$report_file" ]] && touch "$report_file"

current_date=$(date +"%Y-%m-%d")
current_timestamp=$(date +%s)
last_backup=$(find "$backup_dir" -maxdepth 1 -type d -name "Backup-*" | sort | tail -n 1)

if [[ -n "$last_backup" ]]; then
    last_backup_date=$(basename "$last_backup" | awk -F'-' '{print $2"-"$3"-"$4}')
    last_backup_timestamp=$(date -d "$last_backup_date" +%s)

    diff_days=$(( (current_timestamp - last_backup_timestamp) / 86400 ))
else
    diff_days=8 
fi

if [[ "$diff_days" -gt 7 || -z "$last_backup" ]]; then
    new_backup="$backup_dir/Backup-$current_date"
    mkdir -p "$new_backup"
    echo "New backup created: $new_backup ($current_date)" >> "$report_file"

    find "$source_dir" -type f | while read -r file; do
        rel_path="${file#$source_dir/}"
        target_path="$new_backup/$rel_path"
        mkdir -p "$(dirname "$target_path")"
        cp "$file" "$target_path"
        echo "$rel_path added to $new_backup" >> "$report_file"
    done
else
    echo "Updating existing backup: $last_backup ($current_date)" >> "$report_file"

    find "$source_dir" -type f | while read -r file; do
        rel_path="${file#$source_dir/}"
        target_path="$last_backup/$rel_path"
        mkdir -p "$(dirname "$target_path")"

        if [[ -f "$target_path" ]]; then
            size_new=$(get_file_size "$file")
            size_old=$(get_file_size "$target_path")

            if [[ "$size_new" -ne "$size_old" ]]; then
                mv "$target_path" "$target_path.$current_date"
                cp "$file" "$target_path"
                echo "$rel_path updated (old version: $rel_path.$current_date)" >> "$report_file"
            fi
        else
            cp "$file" "$target_path"
            echo "$rel_path added as new to $last_backup" >> "$report_file"
        fi
    done
fi

echo "Backup process completed."



#!/usr/bin/bash

prefix="Backup-*"
backup_folder="$HOME"
restore_folder="$HOME/restore"

available_backups=$(find "$backup_folder" -maxdepth 1 -type d -name "$prefix" | sort -r)

if [ -z "$available_backups" ]; then
    echo "No available backups found."
    exit 1
fi

latest_backup=$(echo "$available_backups" | head -n 1)

mkdir -p "$restore_folder"

for file_path in $(find "$latest_backup" -type f); do
    filename=$(basename "$file_path")

    if [[ ! "$filename" =~ [0-9]{4}-[0-9]{2}-[0-9]{2} ]]; then
        relative_path=$(realpath --relative-to="$latest_backup" "$file_path")
        mkdir -p "$restore_folder/$(dirname "$relative_path")"
        cp "$file_path" "$restore_folder/$relative_path"
    fi
done

echo "File restoration completed successfully."

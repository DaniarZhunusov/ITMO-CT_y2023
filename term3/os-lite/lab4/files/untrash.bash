#!/usr/bin/bash

function error() {
    echo "$1"
    exit 1
}

policy="--ignore"

while [[ $# -gt 0 ]]; do
    case $1 in
        -i|--ignore)
            policy="--ignore"
            shift
            ;;
        -u|--unique)
            policy="--unique"
            shift
            ;;
        -o|--overwrite)
            policy="--overwrite"
            shift
            ;;
        *)
            if [[ -z "$restore_file" ]]; then
                restore_file="$1"
            else
                error "Unexpected argument: $1"
            fi
            shift
            ;;
    esac
done

if [[ -z "$restore_file" ]]; then
    error "Expected 1 argument - file name."
fi

log_file="$HOME/.trash.log"
trash_dir="$HOME/.trash/"

if [[ ! -f "$log_file" ]]; then
    error "File '$log_file' does not exist."
fi
if [[ ! -d "$trash_dir" ]]; then
    error "Directory '$trash_dir' does not exist."
fi

matches=$(grep "/$restore_file : " "$log_file")
if [[ -z "$matches" ]]; then
    error "File '$restore_file' not found."
fi

while read -r line; do
    link_file=$(echo "$line" | awk -F' : ' '{print $2}')
    full_path=$(echo "$line" | awk -F' : ' '{print $1}')
    restore_dir=$(dirname "$full_path")

    echo -n "Do you want to restore the '$link_file'? (y/n): "
    read -r answer </dev/tty

    if [[ "$answer" == "y" ]]; then
        if [[ ! -d "$restore_dir" ]]; then
            echo "Directory '$restore_dir' does not exist. Restoring to '$HOME' directory."
            restore_dir="$HOME"
        fi

        target_path="$restore_dir/$restore_file"

        case $policy in
            --ignore)
                if [[ -e "$target_path" ]]; then
                    echo "File '$restore_file' already exists. Skipping restoration."
                else
                    ln "$trash_dir/$link_file" "$target_path" && rm -f "$trash_dir/$link_file"
                    sed -i "/: $link_file\$/d" "$log_file"
                    echo "File '$restore_file' was successfully restored to '$restore_dir'."
                fi
                ;;

            --unique)
                unique_count=1
                base_name="${restore_file%.*}"
                extension="${restore_file##*.}"

                while [[ -e "$target_path" ]]; do
                    target_path="$restore_dir/${base_name}(${unique_count}).${extension}"
                    ((unique_count++))
                done

                ln "$trash_dir/$link_file" "$target_path" && rm -f "$trash_dir/$link_file"
                sed -i "/: $link_file\$/d" "$log_file"
                echo "File '$restore_file' was successfully restored as '${target_path##*/}' to '$restore_dir'."
                ;;

            --overwrite)
                ln -f "$trash_dir/$link_file" "$target_path" && rm -f "$trash_dir/$link_file"
                sed -i "/: $link_file\$/d" "$log_file"
                echo "File '$restore_file' was successfully restored to '$restore_dir'."
                ;;
        esac
    fi

done <<< "$matches"

echo "File recovery is complete."


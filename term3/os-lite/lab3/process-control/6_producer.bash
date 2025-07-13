#!/bin/bash

input_command=""

while true; do
    read input_command
    case "$input_command" in
        "+")
            kill -USR1 $(cat .pid)
            ;;
        "*")
            kill -USR2 $(cat .pid)
            ;;
        "TERM")
            kill -SIGTERM $(cat .pid)
            rm -f "$pipe"
            exit 0
            ;;
        *)
            echo "Ignored: $input_command"
            ;;
    esac
done

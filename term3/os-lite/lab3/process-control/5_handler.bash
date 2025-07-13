#!/bin/bash

operation="sum"
line=""
result=1

(tail -f pipe) |
while true; do
    read line
    case "$line" in
        "QUIT")
            echo "Exit. Final result: $result"
            killall tail
            kill $(cat .pid)
            rm .pid
            exit 0
            ;;
        "*")
            operation="multiply"
            echo "Mode: multiply"
            ;;
        "+")
            operation="sum"
            echo "Mode: sum"
            ;;
        *)
            if [[ "$line" =~ ^[0-9]+$ ]]; then
                if [ "$operation" == "sum" ]; then
                    result=$((result + line))
                elif [ "$operation" == "multiply" ]; then
                    result=$((result * line))
                fi
                echo "Current result: $result"
            else
                echo "ERROR: Invalid input"
                killall tail
                kill $(cat .pid)
                rm .pid
                exit 1
            fi
            ;;
    esac
done

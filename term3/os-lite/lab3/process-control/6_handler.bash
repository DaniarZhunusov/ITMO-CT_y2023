#!/bin/bash

echo $$ > .pid

value=1
MODE="sum"

sum_result() {
    MODE="sum"
}

multiply_result() {
    MODE="multiply"
}

terminate() {
    exit 0
}

trap 'sum_result' USR1
trap 'multiply_result' USR2
trap 'terminate' SIGTERM

while true; do
    case $MODE in
        "sum")
            let value=$value+2
            echo "Result: $value"
            ;;
        "multiply")
            let value=$value*2
            echo "Result: $value"
            ;;
    esac
    sleep 1
done

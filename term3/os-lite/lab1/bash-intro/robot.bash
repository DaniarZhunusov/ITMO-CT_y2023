#!/bin/bash

function show_position {
    echo "x=$x;y=$y"  
}

if [ "$#" -ne 2 ]; then
    exit 1
fi

width=$1
height=$2

x=$((width / 2))
y=$((height / 2))

while true; do
    show_position

    read -rsn1 input

    input=$(echo "$input" | tr '[:lower:]' '[:upper:]')

    if [[ $input == 'Q' ]]; then
        break
    fi

    case $input in
        W) ((y++));;  
        S) ((y--));;  
        A) ((x--));;  
        D) ((x++));;  
    esac

    if ((x < 0 || x >= width || y < 0 || y >= height)); then
        echo "The robot crashed into the edge of the field! The game is over"
        break
    fi
done

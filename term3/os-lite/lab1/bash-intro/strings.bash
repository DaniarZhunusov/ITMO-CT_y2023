#!/bin/bash

while true; do
    read -r line
    if [[ "$line" == "q" ]]; then
        break
    fi

    length=${#line}

    if [[ "$line" =~ ^[a-zA-Z]*$ ]]; then
        is_alpha=true
    else
        is_alpha=false
    fi

    echo "$length"
    echo "$is_alpha"
done
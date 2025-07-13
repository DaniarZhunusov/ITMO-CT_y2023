#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 a b"
    exit 1
fi

a=$1
b=$2

if ! [[ "$a" =~ ^[0-9]+$ ]] || ! [[ "$b" =~ ^[0-9]+$ ]]; then
    echo "Error: arguments must be natural numbers."
    exit 1
fi

a=$((a))
b=$((b))

sum=0
for (( i=a; i<=b; i++ ))
do
    sum=$((sum + i))
    echo "$sum"
done
#!/bin/bash

N=$1
K=30

for ((i = 1; i <= K; i++)); do
    echo "Запуск newmem.bash с N=$N (Запуск $i из $K)"
    ./newmem.bash $N & 
    sleep 1
done
wait

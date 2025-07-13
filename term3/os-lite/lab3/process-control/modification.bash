#!/bin/bash

echo "$$" > .pid

cat1() {
    echo "ASCII кот 1:"
    echo " /\_/\  "
    echo "( o.o ) "
    echo " > ^ <  "
}

cat2() {
    echo "ASCII кот 2:"
    echo " /\_/\  "
    echo "( -.- ) "
    echo " > ~ <  "
}

cat3() {
    echo "ASCII кот 3:"
    echo " /\_/\  "
    echo "( >w< ) "
    echo " > ~_^ < "
}

cat4() {
    echo "ASCII кот 4:"
    echo " |\---/| "
    echo " | o_o | "
    echo "  \_^_/  "
}

terminate() {
    rm -f .pid  
    exit 0
}

trap 'cat1' SIGUSR1   
trap 'cat2' SIGUSR2   
trap 'cat3' SIGHUP    
trap 'cat4' SIGINT   
trap 'terminate' SIGTERM  

while true; do
    sleep 1
done

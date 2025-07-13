#!/bin/bash

LINE=""
echo $$ > .pid

while true
do
    read LINE
    echo "$LINE" > pipe
done


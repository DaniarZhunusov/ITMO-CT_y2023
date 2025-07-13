#!/bin/bash

./4_process.bash &
pid1=$!
./4_process.bash &
./4_process.bash &
pid3=$!

cpulimit --pid=$pid1 --limit=10 &
kill $pid3

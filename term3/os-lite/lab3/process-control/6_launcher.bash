#!/bin/bash

fifo="pipe"

./6_handler.bash "$fifo" &
./6_producer.bash "$fifo" 


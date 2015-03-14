#!/bin/sh
while read pid
do
    echo "kill $pid"
    kill $pid
done < last.pid

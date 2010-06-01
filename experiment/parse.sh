#!/bin/bash

for dir in `ls -d */`; do
	for file in `ls $dir*test*.txt`; do
		echo "times=read.table(\"$file\")\$V3; print( c(\"$dir\", mean(times), sqrt(var(times))) );" | R -q --no-save --slave
	done
done


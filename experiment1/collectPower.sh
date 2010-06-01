#!/bin/bash

rm power.txt 2>/dev/null
echo "#testSet	time_diff	cpu	lcd	wifi	threeg	total" > power.txt

for i in `ls -d */`; do 
	../parsePower.php < $i*Power* > $i/power.txt
	awk '{if(NR<=2) time=$7; diff=($7-time); cpu+=$2; lcd+=$3; wifi+=$4; threeg+=$5; total+=$6}END{print "'$i'", diff, cpu/50, lcd/50, wifi/50, threeg/50, total/50}' < $i/power.txt >> power.txt
done


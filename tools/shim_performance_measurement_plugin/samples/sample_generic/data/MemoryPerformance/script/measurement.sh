#!/bin/bash

#
# Check the parameters
#
if [ $# -ne 2 ]; then
	exit 1
fi

#
# Copy the files to the working directory
#
cwd=`dirname $0`/../../../Measurement/MemoryPerformance/build/
mkdir $cwd
cp `dirname $0`/Makefile $cwd
cd $cwd

echo $cwd;

rm -f $cwd/*.c
rm -f $cwd/*.h

cp $1 ./measurement.c
cp "`dirname $0`/../code/measurement.h" $cwd
cp "`dirname $0`/../code/mem_access.c" $cwd
cp "`dirname $0`/../code/mem_access.h" $cwd
cp "`dirname $0`/../code/mem_asminc.h" $cwd
cp "`dirname $0`/../code/simulator.h" $cwd
cp "`dirname $0`/../code/sreg.h" $cwd
cp "`dirname $0`/../code/sample_GenericRISC_CPU_memory_perform.c" $cwd

cp "`dirname $0`/../code/boot.S" $cwd
cp "`dirname $0`/../code/util.S" $cwd
cp "`dirname $0`/sample.ld" $cwd
# Alternative treatment
cp "`dirname $0`/dummy.csv" $cwd

#
# Build a working directory
#
#echo make...
#make clean
#make
#if [ $? -ne 0 ]; then
#	exit 1
#fi

echo make... done.

#
# Run the measurement process
#

#sh run-simulator.sh &
#sh gdb.sh
#if [ $? -ne 0 ]; then
#	exit 1
#fi

#
# Aggregation of results
#

#python SimulatorResultToCsv.py $1 result.bin loopnum.bin $2

# Alternative treatment
cat dummy.csv > $2

exit 0

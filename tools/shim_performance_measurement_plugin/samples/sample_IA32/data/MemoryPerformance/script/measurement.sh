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
cp "`dirname $0`/../code/sample_IA32_memory_perform.c" $cwd

#
# Build a working directory
#
echo make...
make clean
make
if [ $? -ne 0 ]; then
	exit 1
fi

echo make... done.

#
# Run the measurement process
#
echo measurement...
$cwd/sample.out > $2
echo measurement... done.

exit 0

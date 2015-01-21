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
cwd=`dirname $0`/../../../Measurement/InstructionCycle/build/
mkdir $cwd
cp `dirname $0`/Makefile $cwd
cp `dirname $0`/../code/sample_IA32_instruction_cycle.c $cwd
cd $cwd

echo $cwd;

rm -f $cwd/*.c
rm -f $cwd/*.h

cp "$1\cycle__IA_32__LLVM_Instructions.c" $cwd
cp "$1\cycle__Intel i5-3550.h" $cwd

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
mkdir $2
$cwd/sample.out > $2\\IA_32__LLVM_Instructions.csv
echo measurement... done.

exit 0

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
cp `dirname $0`/../code/sample_GenericRISC_CPU_instruction_cycle.c $cwd
cp `dirname $0`/../code/generic.h $cwd
cp `dirname $0`/../code/simulator.h $cwd
cp `dirname $0`/../code/sreg.h $cwd
cp `dirname $0`/../code/boot.S $cwd
cp `dirname $0`/../code/util.S $cwd
cp `dirname $0`/sample.ld $cwd
# Alternative treatment
cp `dirname $0`/dummy.csv $cwd

cd $cwd

echo $cwd;

rm -f $cwd/*.c
rm -f $cwd/*.h

cp "$1\cycle__GenericRISC_CPU__LLVM_Instructions.c" $cwd
cp "$1\cycle__System.h" $cwd


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

#sh run-simulator.sh &
#sh gdb.sh
#if [ $? -ne 0 ]; then
#	exit 1
#fi

#
# Result extraction
#

#python SimulatorCycleToCsv.py result.bin $1 OpCode.txt $2


# Alternative treatment
mkdir $2
cat dummy.csv > $2\\GenericRISC_CPU__LLVM_Instructions.csv


exit 0

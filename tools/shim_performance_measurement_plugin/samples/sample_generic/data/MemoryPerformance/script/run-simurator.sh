#!/bin/sh
SIMULATOR_PATH=/usr/local/Simulator

# Run with SSC
$SIMULATOR_PATH/simulator

if test $? -eq 2
then
  exit 0
fi

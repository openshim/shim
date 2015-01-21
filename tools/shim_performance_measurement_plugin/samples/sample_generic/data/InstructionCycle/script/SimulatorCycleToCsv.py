#!/usr/bin/env python3
#
# Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
#
# This software is released under the MIT License.
# http://opensource.org/licenses/mit-license.php

import sys, struct, re, math, decimal, csv

class SimulatorCycleToCsv:
	def __init__(self):
		pass

	def prepare(self):
		self.iteration = 1
		with open(sys.argv[2], 'r') as fp:
			buff = fp.readlines()
			for i in buff:
				mat = re.match("^#define\s*ITERATION\s*([0-9]+)\s*$", i)
				if mat is None:
					continue
				else:
					self.iteration = int(mat.group(1))
					break

	def convert(self):
		csvdata = []
		op = []

		with open(sys.argv[3], 'r') as fp:
			buff = fp.readlines()
			for i in buff:
				op.append(i.strip())

		#print (op)

		with open(sys.argv[1], 'rb') as fp:
			for i in op:
				row = [i]
				readData = struct.unpack("<l", fp.read(4))
				out = readData[0] / self.iteration
				#row.append(out if out > 0 else 0)
				row.append(out)
				csvdata.append(row)

		#print (csvdata)

		with open(sys.argv[4], 'w') as fp:
			wr =csv.writer(fp, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
			for r in csvdata:
				print(r)
				if r[1] < 0:
					r[1] = 0
					print("  -> 0")
				wr.writerow(r)

	def convertSimlatorCycleToCsv(self):
		self.prepare()
		self.convert()

def main():
	if len(sys.argv) != 5:
		exit()

	sim = SimulatorCycleToCsv()
	sim.convertSimlatorCycleToCsv()

if __name__ == '__main__':
	main()


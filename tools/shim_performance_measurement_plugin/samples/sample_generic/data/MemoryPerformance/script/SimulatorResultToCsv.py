#!/usr/bin/env python3

import sys, struct, re, math, decimal, csv

class RegPattern:
	ADDRESS_SPACE = "address_space_name\s*\[\s*\]\s*=\s*\{[^};]*\};"
	SUB_SPACE = "sub_space_name\s*\[\s*\]\s*=\s*\{[^};]*\};"
	SLAVE_COMP = "slave_component_name\s*\[\s*\]\s*=\s*\{[^};]*\};"
	MASTER_COMP = "master_component_name\s*\[\s*\]\s*=\s*\{[^};]*\};"
	ACCESS_TYPE = "access_type_name\s*\[\s*\]\s*=\s*\{[^};]*\};"
	REGPTN_ELEMENT_NAME = "\s*\"(.*)\""
	SOURCE_HEADER_END = "volatile float result_buf[AS_NUM][SS_NUM][SC_NUM][MC_NUM][AT_NUM][6];"

class SimulatorCycleToCsv:
	LATENCY_CACHE_HIT_RATIO = 0.95
	LATENCY_CACHE_NOT_FOUND_PROBABILITY = 0.05
	PITCH_CACHE_HIT_RATIO = 0.95
	PITCH_CACHE_NOT_FOUND_PROBABILITY = 0.05

	def __init__(self):
		pass

	def prepare(self):
		self.src = sys.argv[1]
		self.res = sys.argv[2]
		self.loop = sys.argv[3]
		self.csv = sys.argv[4]
		srcbuff = ""
		
		with open(self.src, 'r') as fp:
			buff = fp.readlines()
			for i in buff:
				mat = re.match(RegPattern.SOURCE_HEADER_END, i)
				if mat is None:
					srcbuff += i
					continue
				else:
					break
		
		buff = re.findall(RegPattern.ADDRESS_SPACE, srcbuff)
		self.addrSpace = re.findall(RegPattern.REGPTN_ELEMENT_NAME, buff[0])
		buff = re.findall(RegPattern.SUB_SPACE, srcbuff)
		self.subSpace = re.findall(RegPattern.REGPTN_ELEMENT_NAME, buff[0])
		buff = re.findall(RegPattern.SLAVE_COMP, srcbuff)
		self.slaveComp = re.findall(RegPattern.REGPTN_ELEMENT_NAME, buff[0])
		buff = re.findall(RegPattern.MASTER_COMP, srcbuff)
		self.masterComp = re.findall(RegPattern.REGPTN_ELEMENT_NAME, buff[0])
		buff = re.findall(RegPattern.ACCESS_TYPE, srcbuff)
		self.accessType = re.findall(RegPattern.REGPTN_ELEMENT_NAME, buff[0])

	def loadloopnum(self):
		with open(self.loop, 'rb') as fp:
			data = fp.read(24)
			buff = struct.unpack('<IIIIII', data)
			self.loopnum = []
			for i in buff:
				if i <= 0:
					i = 1
				self.loopnum.append(i);

	def calcWeight(self):
		self.weightTbl = [0, 0, 0, 0, 0]
		self.weightTbl[4] = len(self.accessType)
		self.weightTbl[3] = self.weightTbl[4] * len(self.masterComp)
		self.weightTbl[2] = self.weightTbl[3] * len(self.slaveComp)
		self.weightTbl[1] = self.weightTbl[2] * len(self.subSpace)
		self.weightTbl[0] = self.weightTbl[1] * len(self.addrSpace)

	def calcElementPosition(self, count):
		pos = [0, 0, 0, 0, 0]
		remain = count
		pos[0] = int(remain / self.weightTbl[1])
		remain = int(remain % self.weightTbl[1])
		pos[1] = int(remain / self.weightTbl[2])
		remain = int(remain % self.weightTbl[2])
		pos[2] = int(remain / self.weightTbl[3])
		remain = int(remain % self.weightTbl[3])
		pos[3] = int(remain / self.weightTbl[4])
		remain = int(remain % self.weightTbl[4])
		pos[4] = remain
		return pos

	def convert(self):
		self.calcWeight()
		
		dataList = []
		csvdata = []
		
		with open(self.res, 'rb') as resfp:
			while 1:
				data = resfp.read(16)
				if data == b'':
					break;
				buff = struct.unpack('<HHHHHHI', data)
				dataList.append(buff)
				
		# print (dataList)
		
		dataDict = {}
		
		dataList = sorted(dataList, key=lambda x:(x[0], x[1], x[2], x[3], x[4], x[5]))
		
		for i in dataList:
			list = [str(i[x]) for x in range(5)]
			key = '-'.join(list)
			if key in dataDict:
				result = dataDict[key]
			else:
				result = [0, 0, 0, 0, 0, 0]
			result[i[5]] = i[6]
			dataDict[key] = result
			
		for i in dataDict.items():
			keys = [int(key) for key in i[0].split('-')]
			vals = i[1]
			bw = []
			
			bw.append(self.addrSpace[keys[0]])
			bw.append(self.subSpace[keys[1]])
			bw.append(self.slaveComp[keys[2]])
			bw.append(self.masterComp[keys[3]])
			bw.append(self.accessType[keys[4]])
			
			f = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
			for j in range(6):
				f[j] = vals[j] / self.loopnum[j]
			
			f[2] = (f[0] * self.LATENCY_CACHE_HIT_RATIO) + (f[1] * self.LATENCY_CACHE_NOT_FOUND_PROBABILITY)
			f[5] = (f[3] * self.PITCH_CACHE_HIT_RATIO) + (f[4] * self.PITCH_CACHE_NOT_FOUND_PROBABILITY)
			
			ch = False
			for j in range(6):
				s = "{0:.2f}".format(f[j])
				bw.append(s)
				if f[j] > 0.00:
					ch = True
			if ch:
				csvdata.append(bw)
		
		count = 0;
		with open(self.csv, 'w') as outfp:
			wr = csv.writer(outfp, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
			for r in csvdata:
				#print(r)
				wr.writerow(r)
		
	def convertSimlatorCycleToCsv(self):
		self.prepare()
		self.loadloopnum()
		self.convert()

def main():
	if len(sys.argv) != 5:
		exit()

	sim = SimulatorCycleToCsv()
	sim.convertSimlatorCycleToCsv()

if __name__ == '__main__':
	main()


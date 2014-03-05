from collections import OrderedDict
import ast
import matplotlib.pyplot as plt
import matplotlib.font_manager as fnt
import numpy

path = './'
delim = '#'
docTermMap = {}
classIdClassMap = {}
termIdTermMap = {}
oldClassIdNewIdMap = {}
newClassIdOldIdMap = {}
classDistributionMap = {}

confOrdDict = OrderedDict()
suppOrdDict = OrderedDict()
suppList = []
ruleList = []
ruleOrdList = []
ruleIdRuleMap = OrderedDict()
ruleIdRuleOrdMap = OrderedDict()
ruleIdClassMap = {}
docIdClassMap = {}
docIdTermMap = {}
classIdDocCountMap = {}

def processFile():
	
	global docTermMap

	initialize()
	count = 0
	

	for line in open(path + 'doc_term_index_frequency_map.txt'):
		found = False
		fields = line.split(delim)
		docId = int(fields[0])
		classIdList = ast.literal_eval(fields[2])
		termIdList = ast.literal_eval(fields[3]).keys()

		tokenSet = set()

		if len(classIdList) > 0:
			count += 1

			
			docIdClassMap[docId] = []
			docIdTermMap[docId] = set(map(int, termIdList))

			tokenSet = set(map(int, termIdList))
			
			for classId in classIdList:
				if not oldClassIdNewIdMap[classId] in classIdDocCountMap:
					classIdDocCountMap[oldClassIdNewIdMap[classId]] = 0
				classIdDocCountMap[oldClassIdNewIdMap[classId]] += 1


				docIdClassMap[docId].append(oldClassIdNewIdMap[classId])
				tokenSet.add(oldClassIdNewIdMap[classId])
				if not(classIdClassMap[classId] in classDistributionMap):
					classDistributionMap[classIdClassMap[classId]] = 0
				classDistributionMap[classIdClassMap[classId]] += 1

			docTermMap[docId] = tokenSet
			
	print count



def initialize():
	global classIdClassMap, termIdTermMap, oldClassIdNewIdMap, newClassIdOldIdMap

	for line in open(path + 'term_index_frequency_map.txt'):
		fields = line.split(',')
		termIdTermMap[int(fields[1])] = fields[0]
	
	maxTermId = max(termIdTermMap.keys())
	newClassId = maxTermId + 1

	## old_id, new_id
	# fileObj = open('new_classId.txt', 'w')

	for line in open(path + 'class_master_map.txt'):
		fields = line.split(',')
		classIdClassMap[int(fields[1])] = fields[0]
		# strLine = fields[1].rstrip() + ',' + str(newClassId) + '\n'
		# fileObj.write(strLine)
		# newClassId += 1

	# fileObj.close()

	for line in open(path + 'new_classId.txt'):
		fields = line.split(',')
		oldClassIdNewIdMap[int(fields[0])] = int(fields[1])
		newClassIdOldIdMap[int(fields[1])] = int(fields[0])



def prepareInputFile():
	
	fileObj = open('apriori_input_termId.txt', 'a')

	for docId in docTermMap:
		# print sorted(docTermMap[docId])
		fileObj.write(' '.join(str(id) for id in sorted(docTermMap[docId])) + '\n')

	fileObj.close()

def plotClassDistribution():

	classList = []
	countList = []

	for k,v in classDistributionMap.items():
		classList.append(k)
		countList.append(v)

	idList = numpy.arange(1, len(classList) * 6 +1, 6)
	# print idList
	plt.figure(figsize=(20, 20)) 
	rects = plt.bar(idList, countList, align='center')
	plt.xticks(idList, classList, rotation = 'vertical', fontproperties = fnt.FontProperties(size = 8))

	for rect in rects:
		height = rect.get_height()

		if height < 10:
			textPos = 10*height
		else:
			textPos = 1.005*height
		plt.text(rect.get_x()+rect.get_width()/2., textPos, '%d'%int(height), ha='center', va='bottom', fontsize = 8, rotation = 'vertical')

	plt.savefig("class_distribution.png", dpi=200)
	plt.close()
	

processFile()
prepareInputFile()


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

def sortRules():
	global confOrdDict, suppOrdDict, ruleOrdList

	print 'writing rules to file..'
	fileObj = open('sorted_rules.txt', 'w')

	confOrdDict = OrderedDict(sorted(confOrdDict.items(), key = lambda t: t[0], reverse = True))
	for k,v in confOrdDict.items():
		suppOrdDict = OrderedDict(sorted(v.items(), key = lambda t: t[1], reverse = True))
		for rulId, val in suppOrdDict.items():
			# print ruleIdRuleMap[rulId]
			ruleIdRuleOrdMap[rulId] = ruleIdRuleMap[rulId]
			fileObj.write(str(ruleIdRuleMap[rulId]) + ' ==> ' + str(ruleIdClassMap[rulId]) + '\n')
	
	fileObj.close()


def filterClassRules():
	global confOrdDict, suppList, ruleList, newClassIdOldIdMap, ruleIdRuleMap, ruleIdClassMap
	ruleId = 0

	for line in open("output_1.txt"):
		classBegin = line.find('>')
		classEnd = line.find('#')
		classStr = line[classBegin + 1:classEnd].strip().split(' ')
		if not len(classStr) > 1:
			valid = True

			if not int(classStr[0]) in newClassIdOldIdMap:
				continue

			itemList = line[:classBegin - 2].strip().split(' ')
			
			for item in itemList:
				if int(item) in newClassIdOldIdMap:
					valid = False
					break

			if valid:
				suppEnd = line[classEnd + 1:].find('#')
				suppVal = line[classEnd + 5 : classEnd + suppEnd].strip()
				confVal = line[classEnd + suppEnd + 7:].strip()

				# print confVal
				# print ruleId

				if not float(confVal) in confOrdDict:
					confOrdDict[float(confVal)] = OrderedDict()
				confOrdDict[float(confVal)][ruleId] = float(suppVal)

				ruleIdClassMap[ruleId] = int(classStr[0])
				ruleIdRuleMap[ruleId] = set(map(int, itemList))
				ruleId += 1
				print 'Found rule no : ' + str(ruleId)


def buildClassifier():

	global classIdDocCountMap

	print 'Building Classifier..'
	ruleIdErrorMap = {}
	docList = docIdTermMap.keys()
	tmpDocList = []
	addRule = False
	classifier = []
	defaultClassId = OrderedDict(sorted(classIdDocCountMap.items(), key = lambda t: t[1], reverse = True)).keys()[0]
	count = 0
	errorCount = 0
	print len(ruleIdRuleOrdMap)
	print len(docIdTermMap)
	print sum(OrderedDict(sorted(classIdDocCountMap.items(), key = lambda t: t[1], reverse = True)).values())

	for ruleId, rule in ruleIdRuleOrdMap.items():
		ruleIdErrorMap[errorCount] = 0
		newRule = True
		addRule = False
		for docId in docList:
			if len(docIdTermMap[docId] & rule) == len(rule):
				if(ruleIdClassMap[ruleId] in docIdClassMap[docId]):
					classIdDocCountMap[ruleIdClassMap[ruleId]] -= 1
					tmpDocList.append(docId)
					addRule = True
					if newRule:
						count += 1
						newRule = False
				else:
					ruleIdErrorMap[errorCount] += 1

		if not addRule:
			ruleIdErrorMap[errorCount] = 0

		if addRule:
			for tmpDocId in tmpDocList:
				docList.remove(tmpDocId)
			
					
			tmpDocList = []


			classifier.append(ruleId)

			defaultClassId = OrderedDict(sorted(classIdDocCountMap.items(), key = lambda t: t[1], reverse = True)).keys()[0]

		errorCount += 1
	print count
	print sum(OrderedDict(sorted(ruleIdErrorMap.items(), key = lambda t: t[1], reverse = True)).values())


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
			# print(type(classIdList[0]))
			# print(type(termIdList[0]))
			docIdClassMap[docId] = []
			docIdTermMap[docId] = set(map(int, termIdList))

			tokenSet = set(map(int, termIdList))
			# for termId in termIdList:
			# 	tokenSet.add(termId)

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
			# print docTermMap[docId]
			# break;
	print count



def initialize():
	global classIdClassMap, termIdTermMap, oldClassIdNewIdMap, newClassIdOldIdMap

	for line in open(path + 'term_index_frequency_map.txt'):
		fields = line.split(',')
		termIdTermMap[int(fields[1])] = fields[0]
	
	maxTermId = max(termIdTermMap.keys())
	newClassId = maxTermId + 1

	for line in open(path + 'class_master_map.txt'):
		fields = line.split(',')
		classIdClassMap[int(fields[1])] = fields[0]
		
	for line in open(path + 'new_classId.txt'):
		fields = line.split(',')
		oldClassIdNewIdMap[int(fields[0])] = int(fields[1])
		newClassIdOldIdMap[int(fields[1])] = int(fields[0])
	

processFile()

filterClassRules()
sortRules()
buildClassifier()



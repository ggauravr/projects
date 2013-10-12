from __future__ import division
import random
import ast

import file_h
import text_h
# import config_h
import xml_h

import re
import math
import operator
import time
from sets import Set

topicDocMap = {}
docTermMap = {}
termDocMap = {}
topicTermChiMap = {}
k = 1500
delim = '#'
docWithClassCount = 0
docCount = 0
termTopicCondProb = {}
finalFeatureSet = set()
topicPrior = {}
termMap = {}
trainDocSet = set()
allDocSet = set()
testDocSet = set()
testDocTermMap = {}
docTopicMap = {}
testDoc = 0

def startRead():
	global docCount, topicDocMap, docTermMap, termDocMap, termMap, docTopicMap

	for line in open('doc_term_map.txt'):
		token = line.split(delim, 3)

		docTopicMap[int(token[0])] = [] 
		topicIdList = ast.literal_eval(token[2])
		
		for topic in topicIdList:
			if (not(int(topic) in topicDocMap)):
				topicDocMap[int(topic)] = set()
			topicDocMap[topic].add(int(token[0]))

			docTopicMap[int(token[0])].append(int(topic))

		allDocSet.add(int(token[0]))

		docTermMap[int(token[0])] = ast.literal_eval(token[3])
		docCount += 1

	for line in open('term_doc_index_frequency_map.txt'):
		token = line.split(delim, 2)
		termDocMap[int(token[0])] = set(ast.literal_eval(token[2]).keys())

	for line in open('term_frequency_file.txt'):
		token = line.split(delim, 3)
		termMap[int(token[1])] = token[0]

def GetTrainData(perValue, genRandom=False):
	global docCount, topicDocMap, docTermMap, termDocMap, termMap, trainDocSet, testDocSet, testDocTermMap

	totalDocCount = docCount
	docCount = math.floor((perValue * docCount)/100)
	count = 0
	i = 0
	
	tempTopicDocMap = {}
	for topic in topicDocMap:
		tempTopicDocMap[topic] = []
		for docId in topicDocMap[topic]:
			tempTopicDocMap[topic].append(docId)

	for topic in tempTopicDocMap:
		localCount = int(math.ceil((len(tempTopicDocMap[topic]) * docCount)/totalDocCount))
		trainDocSet |= set(random.sample(tempTopicDocMap[topic], localCount))

	testDocSet = allDocSet - trainDocSet

	for topic in topicDocMap:
		topicDocMap[topic] = topicDocMap[topic] & trainDocSet

	for termId in termDocMap:
		termDocMap[termId] = termDocMap[termId] & trainDocSet

	tempDocTermMap = {}

	for docId in docTermMap:
		if not docId in trainDocSet:
			testDocTermMap[docId] = docTermMap[docId]
		else:
			tempDocTermMap[docId] = docTermMap[docId]
	
	docTermMap.clear()
	docTermMap = tempDocTermMap
			



def FeatureSelection():
	
	global topicTermChiMap, finalFeatureSet

	print('in feature selection...')

	for topic in topicDocMap:
		termIdSet = set()
		topicDocIdSet = topicDocMap[topic]
		
		topicTermChiMap[topic] = {}

		for docId in topicDocIdSet:
			termIdSet |= set(docTermMap[docId].keys())

		for termId in termIdSet:
			termDocIdSet = termDocMap[termId]

			n11 = len(topicDocIdSet & termDocIdSet) #inetrsection
			n10 = len(termDocIdSet) - n11
			n01 = len(topicDocIdSet) - n11
			n00 = docCount - n11 - n10 - n01

			valueNume = (docCount * math.pow((n11*n00 - n10*n01), 2))
			valueDeno = (n11+n01)*(n11+n10)*(n10+n00)*(n01+n00)
			value = valueNume/valueDeno # worked without division import
			topicTermChiMap[topic][termId] = value

	for topic in topicTermChiMap:
		sortedValues = sorted(topicTermChiMap[topic].iteritems(), key=operator.itemgetter(1), reverse=True)
		featureIdList = []
		i = 0
		while i < k and i < len(sortedValues):
			featureIdList.append(sortedValues[i][0])
			i += 1

		finalFeatureSet |= set(featureIdList)

	featureSetFile = open('chi_terms.csv', 'w')
	featureSetFile.write(','.join(str(termId) for termId in finalFeatureSet))
	featureSetFile.close()


def TrainBernoulliNB():
	
	global termTopicCondProb, topicPrior
	for topic in topicDocMap:
		nC = len(topicDocMap[topic])
		topicPrior[topic] = nC / docCount
		for termId in finalFeatureSet:
			if not termId in termTopicCondProb:
				termTopicCondProb[termId] = {}

			nCT = len(termDocMap[termId] & topicDocMap[topic])
			termTopicCondProb[termId][topic] = (nCT + 1)/(nC + 2)


def TestBernoulliNB(testDocTermList):
	topicScore = {}
	for topic in topicDocMap:
		topicScore[topic] = math.log(topicPrior[topic], 2)
		for termId in finalFeatureSet:
			if termId in testDocTermList:
				topicScore[topic] += math.log(termTopicCondProb[termId][topic], 2)
			else:
				topicScore[topic] += math.log(1 - termTopicCondProb[termId][topic], 2)
	
	maxArgTopic = sorted(topicScore.iteritems(), key=operator.itemgetter(1), reverse=True)
	return maxArgTopic[0][0]

def getTextFromDocuments(document):
	text_result = {}

	if document.title:
		title = document.title.text
	else:
		title = ''

	text = document.text

	text_result['title'] 	= text_h.clean(title, '', True)
	text_result['text'] 	= text_h.clean(text, '', True)
	text_result['set'] 	= set( text_result['text'] ) | set( text_result['title'] )

	return text_result

term_term_dict = {}

def prepareTermIdMap():

	global term_term_dict

	with open('term_frequency_file.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split('#')
			term_term_dict[split_line[0]] = split_line[1]

def startNBClassify():
	directory_name = 'dataset'

	file_list = file_h.get_list_of_files(directory_name)

	for filename in file_list:
		filepath = file_h.get_filepath(filename, directory_name)

		xml_h.initialize(filepath)
		test_document_list = xml_h.get_all('reuters')

		for test_doc in test_document_list:

			test_doc_id = test_doc['newid']

			list_term_id = []
			results = getTextFromDocuments(test_doc)

			for token in results['set']:
				list_term_id.append(int(term_term_dict[token]))

			topicId = TestBernoulliNB(list_term_id)

			print "Class for document ", test_doc_id, " is ", class_id_map[topicId]

startTime = time.time()

class_id_map = {}

def initialize(p_class_id_map):

	global class_id_map 

	class_id_map = p_class_id_map
	
	prepareTermIdMap()
	startRead()
	
	FeatureSelection()
	TrainBernoulliNB()

	startNBClassify()
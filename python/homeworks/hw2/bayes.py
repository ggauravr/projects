from __future__ import division
import random
import ast
import re
import math
import operator
import time
from sets import Set
import file_helper
import text_helper
import config_helper
import xml_helper

path = './'
delim = '#'
docWithClassCount = 0
docCount = 0
topicDocMap = {}
docTermMap = {}
termDocMap = {}
topicTermChiMap = {}
k = int(config_helper.get_text('chi_square_cutoff'))
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

def ReadFiles():
	print('Reading input files...')
	#token[0] -> docId, token[1] -> no of Terms, token[2]-> classIdList, token[3] -> {termId: Count in doc, ..}
	global docCount, topicDocMap, docTermMap, termDocMap, termMap, docTopicMap

	for line in open(path + 'doc_term_index_frequency_map.txt'):
		token = line.split(delim, 3)

		# if(len(token[2]) > 2):
		docTopicMap[int(token[0])] = [] 
		topicIdList = ast.literal_eval(token[2])
		
		# token[2].replace('$', delim)
		#Creating topicId documentId map
		for topic in topicIdList:
			if (not(int(topic) in topicDocMap)):
				topicDocMap[int(topic)] = set()
			topicDocMap[topic].add(int(token[0]))

			docTopicMap[int(token[0])].append(int(topic))

		allDocSet.add(int(token[0]))

		#creating documentId {termId:termCount} map
		docTermMap[int(token[0])] = ast.literal_eval(token[3])
		docCount += 1

	# token[0] -> termId, token[1] -> Doc count Containing term, token[2] -> {docId: term count in this Doc, ...}
	for line in open(path + 'term_doc_index_frequency_map.txt'):
		token = line.split(delim, 2)
		#creating termId documentId map
		termDocMap[int(token[0])] = set(ast.literal_eval(token[2]).keys())

	#Creating term map
	# token[0] -> term name, token[1] -> termId, token[2] -> No of Doc Containing the term
	for line in open(path + 'term_index_frequency_map.txt'):
		token = line.split(delim, 3)
		# print token
		termMap[int(token[1])] = token[0]

def GetTrainData(perValue, genRandom=False):
	print('select test data...')
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

	# trainDataFile = open('train_data.csv', 'w')
	# trainDataFile.write(','.join(str(docId) for docId in trainDocSet))
	# trainDataFile.close()

	# testDataFile = open('test_data.csv', 'w')
	# testDataFile.write(','.join(str(docId) for docId in testDocSet))
	# testDataFile.close()

	# print(len(allDocSet))
	# print(len(trainDocSet))
	# print(len(testDocSet))

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
	print('Selecting features...')
	#Chi square feature selection method
	global topicTermChiMap, finalFeatureSet
	for topic in topicDocMap:
		termIdSet = set()
		topicDocIdSet = topicDocMap[topic]
		
		topicTermChiMap[topic] = {}

		for docId in topicDocIdSet:
			#creates unique termId set
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

	# Selection top k features for every topic
	for topic in topicTermChiMap:
		sortedValues = sorted(topicTermChiMap[topic].iteritems(), key=operator.itemgetter(1), reverse=True)
		featureIdList = []
		i = 0
		while i < k and i < len(sortedValues):
			featureIdList.append(sortedValues[i][0])
			i += 1

		finalFeatureSet |= set(featureIdList)

	# print len(finalFeatureSet)

	featureSetFile = open('final_term_list.csv', 'w')
	featureSetFile.write(','.join(str(termId) for termId in finalFeatureSet))
	featureSetFile.close()


def TrainBernoulliNB():
	print('Training NB...')
	#Bernoulli Naive Bayes Training Model
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

def preprocessDocument(document):
	# results map has the following keys
	# { text : [] }
	# { title : [] }
	# { set : set(tokens from title and text.. to remove duplicates) }
	results = {}

	if document.title:
		title = document.title.text
	else:
		title = ''

	text = document.text

	results['title'] 	= text_helper.clean(title, '', True)
	results['text'] 	= text_helper.clean(text, '', True)
	results['set'] 	= set( results['text'] ) | set( results['title'] )

	return results

term_term_id_map = {}

def prepareTermIdMap():

	global term_term_id_map

	with open('term_index_frequency_map.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split('#')
			term_term_id_map[split_line[0]] = split_line[1]

def startNaiveBayesTest():
	correctClassify = 0
	count = 0

	test_directory = config_helper.get_text('test_directory')

	# get the test document and its term_id list
	file_list = file_helper.get_list_of_files(test_directory)

	print file_list

	for filename in file_list:
		filepath = file_helper.get_filepath(filename, test_directory)

		print filepath

		xml_helper.initialize(filepath)
		test_document_list = xml_helper.get_all('reuters')

		for test_doc in test_document_list:

			# print test_doc
			test_doc_id = test_doc['newid']

			termIdList = []
			results = preprocessDocument(test_doc)

			# prepare a list of term_ids from term->term_id map
			for token in results['set']:
				termIdList.append(int(term_term_id_map[token]))

			# print "term id list ", termIdList
			topicId = TestBernoulliNB(termIdList)

			print "Test Document ID ", test_doc_id, " Predicted Class is ", class_id_map[topicId]

			# if topicId in docTopicMap[docId]:
			# 	correctClassify += 1

		# print(correctClassify)

	# for docId in testDocSet:
	# 	count += 1
	# 	if count%50 == 0: print('processed ' + str(count) + ' docs..') 
	# 	termIdList = testDocTermMap[docId].keys()
	# 	topicId = TestBernoulliNB(termIdList)
	# 	if topicId in docTopicMap[docId]:
	# 		correctClassify += 1

	# print(correctClassify)


def GetDistribution():
	dist = {}
	for docId in testDocSet:
		for topicId in docTopicMap[docId]:
			if not topicId in dist: dist[topicId] = 0
			dist[topicId] += 1
	print('No of Class: ' + str(len(dist)))

startTime = time.time()

class_id_map = {}

def initialize(p_class_id_map):

	global class_id_map 

	class_id_map = p_class_id_map
	
	prepareTermIdMap()
	ReadFiles()
	# GetTrainData(60, genRandom=False)
	FeatureSelection()
	TrainBernoulliNB()

	startNaiveBayesTest()
	# GetDistribution()

# def startBayes():
	# startNaiveBayesTest()

# endTime = time.time()
# print('total time: '),
# print(endTime-startTime)
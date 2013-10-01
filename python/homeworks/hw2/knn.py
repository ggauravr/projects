from __future__ import division
import text_helper
import file_helper
import xml_helper
import config_helper
import csv
import ast
import operator
from numpy import array
from math import sqrt
import time
import random
import numpy

# Requirements:
# 	- list of term_ids filtered using chi-square test
# 	- list of term : term_id mapping
# 	- list of doc_id : classes_list mapping

chi_term_id_set 		= set()	# contains term ids of the terms selected by chi-square test
term_id_index_map 	= {}	# contains the term ids selected by chi-square test and their indices from [0 to n-1]
doc_id_term_id_map 	= {}	# contains the doc id and their corresponding term ids 
term_term_id_map 		= {}	# contains the term(string) and the term ids
doc_id_class_id_list 		= {}	# doc and their corresponsing classes
test_doc_id_list 	 		= []	# list of document ids selected for testing
train_doc_id_list 		= []	# list of document ids selected for training

test_directory = ''
# training_directory 		= 'testdata'

# loads the list of testing and training document ids from relevant files
def prepareTestAndTrainDocs():
	global test_doc_id_list, train_doc_id_list

	with open('test_data (1).csv', 'r') as fh:
		reader = csv.reader(fh)
		for row in reader:
			test_doc_id_list = ast.literal_eval(str(row))	# indices are term_ids as strings

	# rand_start = random.randint(0, len(test_doc_id_list)-600)

	test_doc_id_list = random.sample(test_doc_id_list, 50) # test_doc_id_list[rand_start:rand_start+200]

	with open('train_data (1).csv', 'r') as fh:
		reader = csv.reader(fh)
		for row in reader:
			train_doc_id_list = ast.literal_eval(str(row))	# indices are term_ids as strings

	# train_doc_id_list = train_doc_id_list[:6000]

	# train_doc_id_list = train_doc_id_list[:20]
# loads the term_ids of the terms selected by chi-square selection
def prepareChiTermSet():

	global chi_term_id_set

	with open('final_term_list.csv', 'r') as f_chi_term_list:
		reader = csv.reader(f_chi_term_list)
		for row in reader:
			temp_set = set(ast.literal_eval(str(row)))
	
		for item in temp_set:
			chi_term_id_set.add(item)	# indices are term_ids as strings
	# print chi_term_id_set

# prepares the term_term_id_frequency and doc_term_id_frequency of the entire vocabulary
def prepareGlobalStructures():

	global term_term_id_map, doc_id_term_id_map, doc_id_class_id_list

	# TODO : get these file names from the config file.. coz this file is written from other programs
	with open('term_index_frequency_map.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split(',')
			term_term_id_map[split_line[0]] = split_line[1]

	with open('doc_term_index_frequency_map.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split('#')
			classes = ast.literal_eval(split_line[2])
			if len(classes) > 0 :	# consider only those documents which have at least one class
				doc_id_term_id_map[split_line[0]] = split_line[3]
				doc_id_class_id_list[split_line[0]]    = classes

# for the attributes in the chi-square selection, assigns sequential indices [0 to n-1], n - number of selected attributes
def prepareTermIndexMap():
	global term_id_index_map

	index = 1;
	for term_id in chi_term_id_set:
		term_id_index_map[term_id] = index 	# term_id is string here, coz chi_square indices are strings
		index += 1

# prepare a full feature vector from the the list of only terms occurring in the document, i.e from partial feature vector
# given a list of terms with varying number of terms(say m or n), outputs a vector of length n, n - number of chi-square selected terms
def prepareFeatureVector(p_term_id_frequency_list):

	global term_id_index_map

	feature_vector = [0] * (len(chi_term_id_set) + 1) # index started from 1, so allocate n+1 elements

	if type(p_term_id_frequency_list) is str:
		# convert it into a dict and process
		p_term_id_frequency_list = ast.literal_eval(p_term_id_frequency_list)
		# print "converting str to dict"

	if type(p_term_id_frequency_list) is list:
		for term in p_term_id_frequency_list:
			for term_id, frequency in term.items():
				# don't consider the term if the term is not selected in the feature selection process
				if not term_id in chi_term_id_set:
					continue;

				index = term_id_index_map[term_id] # gives the index of the term
				feature_vector[index] = frequency
	elif type(p_term_id_frequency_list) is dict:
		
		for term_id, frequency in p_term_id_frequency_list.items():
			term_id = str(term_id)
			# don't consider the term if the term is not selected in the feature selection process
			if not term_id in chi_term_id_set:
					continue;

			index = term_id_index_map[term_id] # gives the index of the term
			# print "term id , index and frequency ", term_id, "..", index, ".. ", frequency
			feature_vector[index] = frequency
	# elif type(p_term_id_frequency_list) is str:
	# 	# convert it into a list first
	# 	p_term_id_frequency_list = ast.literal_eval(p_term_id_frequency_list)
	# 	for term_id, frequency in p_term_id_frequency_list.items():
	# 		# don't consider the term if the term is not selected in the feature selection process
	# 		if not term_id in chi_term_id_set:
	# 			continue;

	# 		index = term_id_index_map[term_id] # gives the index of the term
	# 		feature_vector[index] = frequency

	return feature_vector

def measureDistance(from_vector, to_vector):
	# both should be of equal distance so iterate and measure the distance

	result = 0
	index = 0

	# len(from_vector) is the dimension of the feature vector
	# while index < len(from_vector):
	# 	result += ( from_vector[index] - to_vector[index]) ** 2 # euclidean distance.. sum of squares
	# 	index += 1

	# result = sqrt(result) # root of the squared distance
	result = numpy.linalg.norm(numpy.array(to_vector) - numpy.array(from_vector))

	# print "Distance is ", result
	return result

def preprocessDocument(document):
	# results map has the following keys
	# { text : [] }
	# { title : [] }
	# { set : set(tokens from title and text.. to remove duplicates) }
	results = {}

	# print document

	if document.title:
		title = document.title.text
	else:
		title = ''

	text = document.text

	results['title'] 	= text_helper.clean(title, '', True)
	results['text'] 	= text_helper.clean(text, '', True)
	results['set'] 	= set( results['text'] ) | set( results['title'] )

	return results


class_id_map = {}

def initialize(p_class_id_map):
	
	global doc_id_term_map, class_id_map

	class_id_map = p_class_id_map

	# get chi-square terms, assign sequential indices to them
	prepareChiTermSet()
	prepareTermIndexMap();

	prepareGlobalStructures()
	prepareTestAndTrainDocs()

	# take records/ documents from test-data folder
	# file_list = file_helper.get_list_of_files(test_directory)

	training_feature_vector = {}

	startProgram = time.time()

	start = time.time()
	# print "preparing training feature vectors "

	# prepare feature vectors for training documents beforehand
	for train_doc_id in doc_id_term_id_map:
		training_feature_vector[train_doc_id] = prepareFeatureVector(doc_id_term_id_map[train_doc_id])
		# print "Training Feature Vector ", training_feature_vector[train_doc_id]
		# print type(train_doc_id)

	end = time.time()
	print "preparing training feature vectors took ", end-start, " s"
	
	test_doc_index = 1
	correctly_matched = {}

	test_directory = config_helper.get_text('test_directory')
	file_list = file_helper.get_list_of_files(test_directory)

	# print file_list

	for filename in file_list:
		filepath = file_helper.get_filepath(filename, test_directory)

		xml_helper.initialize(filepath)
		test_document_list = xml_helper.get_all('reuters')
	
		# print filepath
	# for test_doc_id in test_doc_id_list:

		for test_document in test_document_list:		
			# print test_doc_id
			start = time.time()

			test_doc_id = test_document['newid']

			print "Starting Test Document ", test_doc_index

			term_id_frequency_list = []

			token_results = preprocessDocument(test_document)
			
			# preprocess.. get the clean tokens.. get the term_id, frequency list
			for term in token_results['set']:

				# calculate the term frequency
				if term in token_results['title']:
					# TODO : get the title weight from config file
					term_frequency = 3*token_results['title'].count(term)
				else:
					term_frequency = token_results['text'].count(term)

				term_frequency = int(term_frequency)

				# TODO : get the term_id_index_map from term_master file
				if not term in term_term_id_map:
					continue;

				term_id = term_term_id_map[term]
				# if term_id in chi_term_id_set:
				term_id_frequency_list.append({ term_id : term_frequency} )
			
			# call prepareFeatureVector to get the feature-vector for the current test document
			test_feature_vector = prepareFeatureVector(term_id_frequency_list)
			# print "Test Feature Vector ", test_feature_vector
			doc_distance_map = {}

			# TODO : get the doc_id_term_map from doc_term file
			# for train_doc_id in train_doc_id_list:
			for train_doc_id in doc_id_term_id_map.keys():
				distance = measureDistance(training_feature_vector[train_doc_id], test_feature_vector)
				doc_distance_map[train_doc_id] = distance

			# sort the map of {doc_id : distance} by distance -> list of (doc_id, distance) tuples
			sorted_doc_distance = sorted(doc_distance_map.iteritems(), key=operator.itemgetter(1))
			
			# TODO : parameterize the number of nearest neighbors to be considered, k-nn
			k = 2
			sorted_doc_distance = sorted_doc_distance[:k]
			# print "Sorted Doc Distance ", sorted_doc_distance
			
			# print "found distance for one test document in ", end-start, " s"
			# print "Sorted Doc Distance for test document ", test_doc_id, " is ", sorted_doc_distance

			# according to the parameter "k" extract the top k elements from the above list of tuples
			class_list = []
			class_set = set()
			class_frequency_map = {}
			
			for item in sorted_doc_distance:
				# append the class_list of top k documents to the set of probable class_list of the test data
				# item[0] is the doc_id, item[1] is the distance
				for class_item in doc_id_class_id_list[item[0]]:
					# print class_item
					class_list.append(class_item)
					# print "class item ", class_item, " doc_id_class_id_list ", doc_id_class_id_list[test_doc_id]
					
				
			class_set = set(class_list) # remove duplicate class_list

			# find the frequency of classes from the neighbors, assign class with the highest frequency
			# or assign multiple classes if all of them have the same frequency
			for class_item in class_set:
				if not class_item in class_frequency_map:
					class_frequency_map[class_item] = 0
				class_frequency_map[class_item] = class_list.count(class_item)
				# if class_item in doc_id_class_id_list[test_doc_id]:
				# 		correctly_matched[test_doc_id] = 1

			sorted_class_frequency = sorted(class_frequency_map.iteritems(), key=operator.itemgetter(1), reverse=True)

			end = time.time()
			class_names = []
			for item in sorted_class_frequency:
				# print item
				index = 0

				for item_c in item:
					if index == 0:
						class_names.append(class_id_map[item_c])
						index += 1

			print "Classes for Test Document ", test_doc_id, " might be one of the following ",class_names 
			# print "Actual Class of the document is ", doc_id_class_id_list[test_doc_id], " time taken is ", end-start, " s"
			# print "processed one document in ", end-start, " s"
			test_doc_index += 1

	# print "Correctly classified documents ", len(correctly_matched)

	endProgram = time.time()
	print "Program's total execution time is ", endProgram - startProgram, " s"

# start the program
# initialize(p_class_id_map)
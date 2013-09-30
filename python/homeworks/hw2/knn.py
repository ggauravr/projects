from __future__ import division
import text_helper
import file_helper
import xml_helper
import csv
import ast
import operator
from numpy import array

# Requirements:
# 	- list of term_ids filtered using chi-square test
# 	- list of term : term_id mapping
# 	- list of doc_id : classes_list mapping

chi_term_id_set = set()
term_id_index_map = {}
doc_id_term_id_map = {}
term_id_map = {}
doc_id_class_id_list = {}

test_doc_id_list 	 = []
train_doc_id_list = []

test_directory_name = 'testdata'
training_directory_name = 'testdata'

def prepareTestAndTrainDocs():
	global test_doc_id_list, train_doc_id_list

	with open('test_data.csv', 'r') as fh:
		reader = csv.reader(fh)

		for row in reader:
			test_doc_id_list = ast.literal_eval(str(row))

	test_doc_id_list = test_doc_id_list[:25]

	with open('train_data.csv', 'r') as fh:
		reader = csv.reader(fh)

		for row in reader:
			train_doc_id_list = ast.literal_eval(str(row))

	# print test_doc_id_list, '\n\n'
	# print train_doc_id_list, '\n\n'
# prepareTestAndTrainDocs()

def prepareChiTermSet():

	global chi_term_id_set

	with open('final_term_list.csv', 'r') as f_chi_term_list:
		reader = csv.reader(f_chi_term_list)

		for row in reader:
			# print row
			temp_set = set(ast.literal_eval(str(row)))
	
		for item in temp_set:
			chi_term_id_set.add(int(item))

def prepareGlobalStructures():

	global term_id_map, doc_id_term_id_map, doc_id_class_id_list

	# open the term_id map file and load into a global map
	with open('term_index_frequency_map.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split(',')

			# term : term_id map
			term_id_map[split_line[0]] = split_line[1]

	# print term_id_index_map
	# open the term_id map file and load into a global map
	with open('doc_term_index_frequency_map.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split('#')
			classes = ast.literal_eval(split_line[2])
			
			# term : term_id map
			if len(classes) > 0 :
				doc_id_term_id_map[split_line[0]] = split_line[3] # ast.literal_eval(split_line[3])
				doc_id_class_id_list[split_line[0]]    = classes
	# print len(doc_id_term_id_map)


def prepareTermIndexMap():
	# get the chi_term_id_set
	# create a {term_id, index} map
	global term_id_index_map

	index = 1;
	for term_id in chi_term_id_set:
		term_id_index_map[term_id] = index
		index += 1


def prepareFeatureVector(p_term_id_frequency_list):
	#	input
	#		term_id_frequency_set 
	#	output
	#		feature_vector with k terms, with values equal to the frequency of occurrence

	# initialize a zero vector

	global term_id_index_map

	feature_vector = [0] * (len(chi_term_id_set) + 1) # index started from 1, so allocate n+1 elements

	if str(type(p_term_id_frequency_list)) == "<type 'list'>":
		for term in p_term_id_frequency_list:
			for term_id, frequency in term.items():
				
				if not term_id in chi_term_id_set:
					continue;

				index = term_id_index_map[term_id] # gives the index of the term
				feature_vector[index] = frequency
	elif str(type(p_term_id_frequency_list)) == "<type 'dict'>":
		for term_id, frequency in p_term_id_frequency_list.items():

			if not term_id in chi_term_id_set:
					continue;

			index = term_id_index_map[term_id] # gives the index of the term
			feature_vector[index] = frequency
	elif str(type(p_term_id_frequency_list)) == "<type 'str'>":
		
		# print p_term_id_frequency_list

		p_term_id_frequency_list = ast.literal_eval(p_term_id_frequency_list)

		for term_id, frequency in p_term_id_frequency_list.items():
			# print "Term Id and Frequency ",term_id, frequency
			
			if not term_id in chi_term_id_set:
				continue;

			index = term_id_index_map[term_id] # gives the index of the term
			# print " Index ", index, '\n'
			feature_vector[index] = frequency

	return feature_vector

def measureDistance(from_vector, to_vector):
	# both should be of equal distance so iterate and measure the distance

	result = 0
	index = 0

	while index < len(from_vector):
		result += pow( from_vector[index] - to_vector[index], 2) # euclidean distance.. sum of squares
		index += 1
	# print index
	result = pow(result, 0.5) # root of the squared distance

	return result

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

def main():
	
	global doc_id_term_map
	prepareChiTermSet()
	prepareGlobalStructures()
	prepareTestAndTrainDocs()

	# print  doc_id_term_id_map

	# call prepareTermIndexMap
	prepareTermIndexMap();

	# take records/ documents from test-data folder
	file_list = file_helper.get_list_of_files(test_directory_name)

	training_feature_vector = {}

	# for doc_id, term_id_frequency_list in doc_id_term_id_map.items():
	list_list = []
	for train_doc_id in train_doc_id_list:
		# print "\nType ", type(ast.literal_eval(doc_id_term_id_map[doc_id]))
		training_feature_vector[int(train_doc_id)] = prepareFeatureVector(doc_id_term_id_map[train_doc_id])
		list_list.append(training_feature_vector[int(train_doc_id)])

	numpy_array = array( list_list )
	print numpy_array
	return
		# print "Training Feature Vector ", training_feature_vector[int(train_doc_id)]

	# for filename in file_list:
	# 	filepath = file_helper.get_filepath(filename, test_directory_name)

	# 	xml_helper.initialize(filepath)
	# 	test_document_list = xml_helper.get_all('reuters')
	
	for test_doc_id in test_doc_id_list:
		# print test_doc_id

		term_id_frequency_list = []

		# token_results = preprocessDocument(test_document)
		
		# # preprocess.. get the clean tokens.. get the term_id, frequency list
		# for term in token_results['set']:

		# 	# calculate the term frequency
		# 	if term in token_results['title']:
		# 		# TODO : get the title weight from config file
		# 		term_frequency = 3*token_results['title'].count(term)
		# 	else:
		# 		term_frequency = token_results['text'].count(term)

		# 	term_frequency = int(term_frequency)

		# 	# TODO : get the term_id_index_map from term_master file
		# 	if not term in term_id_map:
		# 		continue;

		# 	term_id = term_id_map[term]
		# 	# if term_id in chi_term_id_set:
		# 	term_id_frequency_list.append({ term_id : term_frequency} )
			
		# call prepareFeatureVector to get the feature-vector for the current test document
		test_feature_vector = prepareFeatureVector(doc_id_term_id_map[test_doc_id])
		# print "TEst Feature Vector ", test_feature_vector
		doc_distance_map = {}

		# print doc_id_term_id_map

		# TODO : get the doc_id_term_map from doc_term file
		for train_doc_id in train_doc_id_list:
		# for doc_id, term_id_frequency_list in doc_id_term_id_map.items():
			# print "\nType ", type(ast.literal_eval(doc_id_term_id_map[doc_id]))
			# print "Training Doc ID",train_doc_id
			# print "Feature Vector", training_feature_vector[int(train_doc_id)], '\n\n'

			distance = measureDistance(training_feature_vector[int(train_doc_id)], test_feature_vector)
			# print "Test Doc ", test_doc_id, " Training Doc ID ", train_doc_id, " distance ", distance
			doc_distance_map[train_doc_id] = distance

		# sort the map of {doc_id : distance} by distance -> list of (doc_id, distance) tuples
		sorted_doc_distance = sorted(doc_distance_map.iteritems(), key=operator.itemgetter(1))
		sorted_doc_distance = sorted_doc_distance[:5]

		print "Test Doc ", test_doc_id, "Sorted Doc Distance", sorted_doc_distance
		# according to the parameter "k" extract the top k elements from the above list of tuples
		classes = []
		k = 1;

		print "sorted doc distance " , type(sorted_doc_distance)
		for item in sorted_doc_distance:
			# append the classes of top k documents to the set of probable classes of the test data
			# item[0] is the doc_id, item[1] is the distance
			# TODO : get the classes given a doc_id
			for class_item in doc_id_class_id_list[item[0]]:
				# print class_item
				classes.append(class_item)
			# classes.append(doc_id_class_id_list[item[0]])
			# item[0].getClasses
			# print item
		print classes, '\n'
		classes = set(classes) # remove duplicate classes
		print "Test Document ID ", test_doc_id, " might belong to this class " , str(classes), '\n'

# start the program
main()
# prepareGlobalStructures()

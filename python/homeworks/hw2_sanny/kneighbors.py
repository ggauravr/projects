import text_h
import file_h
import xml_h
import csv
import ast
import operator
from numpy import array
import time
import random
import numpy
from math import sqrt

term_term_mapping 		= {}
doc_class_map 		= {}
test_doc_id_list 	 		= []
train_doc_id_list 		= []
chi_term_id_set 		= set()
term_id_index_map 	= {}
doc_term_mapping 	= {}

directory_name = ''
	
def getVector(p_term_id_frequency_list):

	global term_id_index_map

	feature_vector = [0] * (len(chi_term_id_set) + 1)

	if type(p_term_id_frequency_list) is str:
		p_term_id_frequency_list = ast.literal_eval(p_term_id_frequency_list)
		
	if type(p_term_id_frequency_list) is list:
		for term in p_term_id_frequency_list:
			for term_id, frequency in term.items():
				if not term_id in chi_term_id_set:
					continue;

				index = term_id_index_map[term_id]
				feature_vector[index] = frequency
	elif type(p_term_id_frequency_list) is dict:
		
		for term_id, frequency in p_term_id_frequency_list.items():
			term_id = str(term_id)
			
			if not term_id in chi_term_id_set:
					continue;

			index = term_id_index_map[term_id]
			feature_vector[index] = frequency
	
	return feature_vector

def runChiMethod():

	global chi_term_id_set

	with open('chi_terms.csv', 'r') as f_chi_term_list:
		reader = csv.reader(f_chi_term_list)
		for row in reader:
			temp_set = set(ast.literal_eval(str(row)))
	
		for item in temp_set:
			chi_term_id_set.add(item)

def preprocess():

	global term_term_mapping, doc_term_mapping, doc_class_map

	with open('term_frequency_file.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split('#')
			term_term_mapping[split_line[0]] = split_line[1]

	with open('doc_term_map.txt') as fh:
		contents = fh.readlines()

		for line in contents:
			split_line = line.split('#')
			classes = ast.literal_eval(split_line[2])
			if len(classes) > 0 :
				doc_term_mapping[split_line[0]] = split_line[3]
				doc_class_map[split_line[0]]    = classes


def preprocessDocument(document):
	results = {}


	if document.title:
		title = document.title.text
	else:
		title = ''

	text = document.text

	results['title'] 	= text_h.clean(title, '', True)
	results['text'] 	= text_h.clean(text, '', True)
	results['set'] 	= set( results['text'] ) | set( results['title'] )

	return results


def loadTermIndexMapping():
	global term_id_index_map

	index = 1;
	for term_id in chi_term_id_set:
		term_id_index_map[term_id] = index
		index += 1

def calculateDistance(vector_1, vector_2):

	result = 0
	index = 0

	result = numpy.linalg.norm(numpy.array(vector_2) - numpy.array(vector_1))

	return result

class_id_map = {}

def initialize(p_class_id_map):
	
	global doc_id_term_map, class_id_map

	class_id_map = p_class_id_map

	runChiMethod()
	loadTermIndexMapping();

	preprocess()

	training_feature_vector = {}

	for train_doc_id in doc_term_mapping:
		training_feature_vector[train_doc_id] = getVector(doc_term_mapping[train_doc_id])
	
	document_index = 1

	directory_name = 'dataset'
	file_list = file_h.get_list_of_files(directory_name)

	for filename in file_list:
		filepath = file_h.get_filepath(filename, directory_name)

		xml_h.initialize(filepath)
		test_document_list = xml_h.get_all('reuters')
	
		for test_doc in test_document_list:		
			start = time.time()

			doc_test_id = test_doc['newid']

			list_term_frequency = []

			token_results = preprocessDocument(test_doc)
			
			for term in token_results['set']:

				if term in token_results['title']:
					# TODO : get the title weight from config file
					term_frequency = 3*token_results['title'].count(term)
				else:
					term_frequency = token_results['text'].count(term)

				term_frequency = int(term_frequency)

				if not term in term_term_mapping:
					continue;

				term_id = term_term_mapping[term]
				list_term_frequency.append({ term_id : term_frequency} )
			
			test_feature_vector = getVector(list_term_frequency)
			distance_dict = {}

			for train_doc_id in doc_term_mapping.keys():
				distance = calculateDistance(training_feature_vector[train_doc_id], test_feature_vector)
				distance_dict[train_doc_id] = distance

			sort_distance = sorted(distance_dict.iteritems(), key=operator.itemgetter(1))
			
			k = 2
			sort_distance = sort_distance[:k]
			list_class = []
			class_set = set()
			map_class_frequency = {}
			
			for item in sort_distance:
				for class_item in doc_class_map[item[0]]:
					list_class.append(class_item)
					
				
			class_set = set(list_class) # remove duplicate list_class

			for class_item in class_set:
				if not class_item in map_class_frequency:
					map_class_frequency[class_item] = 0
				map_class_frequency[class_item] = list_class.count(class_item)
			
			class_f = sorted(map_class_frequency.iteritems(), key=operator.itemgetter(1), reverse=True)

			class_names = []
			for item in class_f:
				index = 0

				for item_c in item:
					if index == 0:
						class_names.append(class_id_map[item_c])
						index += 1

			print "given document ", doc_test_id, " belongs to one the following ",class_names 
			document_index += 1
import text_h
import xml_h

import bayes_classifier
import kneighbors

import sys

list_test_docs = {}

def loadTestDocuments(p_directory_name):

	global list_test_docs

	file_list = file_h.get_list_of_files(p_directory_name)

	for filename in file_list:
		filepath = file_h.get_filepath(filename, directory_name)

		xml_h.initialize(filepath)
		list_test_docs = xml_h.get_all('reuters')

def initialize():
	directory_name = config.get_text('directory_name')

	if directory_name != '':
		loadTestDocuments(directory_name)
		classify()
	
def loadClassDictionary():

	class_id_map = {}

	class_list_file = 'class_list.txt'

	with open(class_list_file, "r") as fh:
		content = fh.readlines()

		for line in content:
			split_content = line.split(',')
			class_id_map[int(split_content[1])] = split_content[0]

	return class_id_map

def classify(classifier):
	class_id_map = loadClassDictionary()

	if classifier == 'bayes_classifier':
		bayes_classifier.initialize(class_id_map)
	elif classifier == 'kneighbors':
		kneighbors.initialize(class_id_map)

def start():
	classifier = sys.argv[1]

	classify(classifier)

start()
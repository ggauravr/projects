import text_helper
import xml_helper
import config_helper

import bayes
import knn

import sys

test_document_list = {}

def getTestDocumentList(p_test_directory):

	global test_document_list

	file_list = file_helper.get_list_of_files(p_test_directory)

	for filename in file_list:
		filepath = file_helper.get_filepath(filename, test_directory)

		xml_helper.initialize(filepath)
		test_document_list = xml_helper.get_all('reuters')

def initialize():
	test_directory = config.get_text('test_directory')

	if test_directory != '':
		# read files from this directory
		getTestDocumentList(test_directory)
		startClassification()
	else:
		# take 80:20 data from existing documents
		pass

def loadClassMap():

	class_id_map = {}

	class_master_file = config_helper.get_text('class_master')

	with open(class_master_file, "r") as fh:
		content = fh.readlines()

		for line in content:
			split_content = line.split(',')
			class_id_map[int(split_content[1])] = split_content[0]

	return class_id_map

def startClassification(classifier):
	# start naive bayes

	# get the class_name_id map
	class_id_map = loadClassMap()

	print " Classsififer ", classifier

	if classifier == 'bayes':
		bayes.initialize(class_id_map)
	elif classifier == 'knn':
		knn.initialize(class_id_map)

def main():
	# get command line arg
	classifier = sys.argv[1]

	startClassification(classifier)

main()
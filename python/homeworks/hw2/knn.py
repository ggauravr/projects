# Requirements:
# 	- list of term_ids filtered using chi-square test
# 	- list of term : term_id mapping
# 	- list of doc_id : classes_list mapping


chi_term_id_set = set()
term_id_index_map = {}


def prepareTermIndexMap():
	# get the chi_term_id_set
	# create a {term_id, index} map

	index = 1;
	for term_id in chi_term_set:
		term_id_index_map[term_id] = index


def prepareFeatureVector(p_term_id_frequency_list):
	#	input
	#		term_id_frequency_set 
	#	output
	#		feature_vector with k terms, with values equal to the frequency of occurrence

	# initialize a zero vector
	feature_vector = [0] * (len(chi_term_id_set) + 1) # index started from 1, so allocate n+1 elements

	for term in p_term_id_frequency_list:
		for term_id, frequency in term.items():
			index = term_id_index_map[term_id] # gives the index of the term
			feature_vector[index] = frequency

	return feature_vector

def measureDistance(from_vector, to_vector):
	# both should be of equal distance so iterate and measure the distance

	result = 0;

	for index in from_vector:
		result += pow( from_vector[index] - to_vector[index], 2) # euclidean distance.. sun of squares

	result = pow(result, 0.5) # root of the squared distance

	return result

def preprocessDocument(document):
	# results map has the following keys
	# { text : [] }
	# { title : [] }
	# { set : set(tokens from title and text.. to remove duplicates) }
	if document.title:
		title = document.title.text
	else:
		title = ''

	text = document.text

	results['title'] 	= text_helper.clean(title, stopwords, True)
      results['text'] 	= text_helper.clean(text, stopwords, True)
      results['set'] 	= set( results['text'] ) | set( results['title'] )

	return results

def main():
	
	# call prepareTermIndexMap
	prepareTermIndexMap();

	# take records/ documents from test-data folder
	file_list = file_helper.get_list_of_files(test_directory_name)

	for filename in file_list:
	    filepath = file_helper.get_filepath(filename, test_directory_name)

	    xml_helper.initialize(filepath)
	    test_document_list = xml_helper.get_all('reuters')

		for test_document in test_document_list:
			
			term_id_frequency_list = []

			token_results = preprocessDocument(document)

			# preprocess.. get the clean tokens.. get the term_id, frequency list
			for term in token_results['set']:

	            	# calculate the term frequency
	            	if term in token_results['title']:
					term_frequency = title_weight*results['title'].count(term)
				else:
					term_frequency = results['text'].count(term)

				term_frequency = int(term_frequency)

	            	term_id = term_id_map[term]
	            	term_id_frequency_list[term_id] = term_frequency

			# call prepareFeatureVector to get the feature-vector for the current test document
			test_feature_vector = prepareFeatureVector(term_id_frequency_list)
			doc_distance_map = {}

			for doc_id, term_id_frequency_list in doc_id_term_id_map.items():
				train_feature_vector = prepareFeatureVector(term_id_frequency_list)

				distance = measureDistance(train_feature_vector, test_feature_vector)
				doc_distance_map[doc_id] = distance

			# sort the map of {doc_id : distance} by distance -> list of (doc_id, distance) tuples
			sorted_doc_distance = sorted(doc_distance_map.iteritems(), key=operator.itemgetter(1))
			
			# according to the parameter "k" extract the top k elements from the above list of tuples
			classes = []
			k = 1;
			for item in sorted_doc_distance:
				# append the classes of top k documents to the set of probable classes of the test data
				# item[0] is the doc_id, item[1] is the distance
				classes.append(item[0].getClasses())

			classes = set(classes) # remove duplicate classes
			print "current test document might belong to the following classes", str(classes), '\n'

# start the program
main()


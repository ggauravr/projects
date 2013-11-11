import csv
import ast
import numpy

chi_term_id_set = set()
chi_feature_vectors = {}
term_id_index_map = {}
cluster_points = []
doc_clusters  = {}
nDimensions  = None
nClusters = 0
convergenceThreshold = 0.125


def prepareTermIndexMap():
    global term_id_index_map

    index = 0
    for term_id in chi_term_id_set:
        term_id_index_map[term_id] = index  # term_id is string here, coz chi_square indices are strings
        index += 1


def prepareChiFeatureVector(term_vector_map):

    feature_vector = [0] * (len(chi_term_id_set))

    for (term_id, frequency) in term_vector_map.items():
        term_id = str(term_id)

        if not term_id in chi_term_id_set:
            continue

        index = term_id_index_map[term_id]  # gives the index of the term

        feature_vector[index] = frequency

    return numpy.array(feature_vector)


def generateChiFeatureVectors():

    global chi_feature_vectors, nDocuments

    with open('../hw1/doc_term_index_frequency_map.txt', 'r') as fh:
        for line in fh:
            fields = line.split('#')

            doc_id = fields[0]
            term_vector_map = ast.literal_eval(fields[3])

            chi_feature_vectors[doc_id] = prepareChiFeatureVector(term_vector_map)

def getChiSquareTerms():

    global chi_term_id_set, nDimensions

    with open('../hw2/final_term_list.csv', 'r') as f_chi_term_list:
        reader = csv.reader(f_chi_term_list)
        for row in reader:
            temp_set = set(ast.literal_eval(str(row)))

        for item in temp_set:
            chi_term_id_set.add(item)  # indices are term_ids as strings

    nDimensions = len(chi_term_id_set)

    # print nDimensions

def init():

    getChiSquareTerms()

    prepareTermIndexMap()

    generateChiFeatureVectors()
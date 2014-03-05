import csv
import ast
import numpy

'''
Global Variables
------------------------------------

chiTermIdSet
    set of terms/features selected by chi-square selection method

feature_vectors
    docId -> featureVector of only chi-terms

termIdIndexMap
    term_id -> index

clusterPoints
    index/cluster_number -> feature vector of the centroid

docClusters
    docId -> cluster index
'''
chiTermIdSet = set()
chiFeatureVectors = {}
docIdClassListMap = {}
termIdIndexMap = {}
clusterPoints = []
docClusters  = {}
nDimensions  = None
nClusters = 0
convergenceThreshold = 0.25
chiFeatureList = []


def prepareTermIndexMap():
    global termIdIndexMap

    index = 0
    for term_id in chiTermIdSet:
        termIdIndexMap[term_id] = index  # term_id is string here, coz chi_square indices are strings
        index += 1


def prepareChiFeatureVector(term_vector_map, toNumpy = True):

    featureVector = [0] * (len(chiTermIdSet))

    for (term_id, frequency) in term_vector_map.items():
        term_id = str(term_id)

        if not term_id in chiTermIdSet:
            continue

        index = termIdIndexMap[term_id]  # gives the index of the term

        featureVector[index] = frequency

    if toNumpy:
        return numpy.array(featureVector)
    else:
        return featureVector


def generateChiFeatureVectors(needNumpy = True):

    global chiFeatureVectors, nDocuments, docIdClassListMap, chiFeatureList

    with open('doc_term_index_frequency_map.txt', 'r') as fh:
        for line in fh:
            fields = line.split('#')

            docId = fields[0]
            classList = ast.literal_eval(fields[2])
            term_vector_map = ast.literal_eval(fields[3])

            docIdClassListMap[docId] = classList
            chiFeatureVectors[docId] = prepareChiFeatureVector(term_vector_map, needNumpy)
            chiFeatureList.append(chiFeatureVectors[docId])

def getChiSquareTerms():

    global chiTermIdSet, nDimensions

    with open('final_term_list.csv', 'r') as f_chi_term_list:
        reader = csv.reader(f_chi_term_list)
        for row in reader:
            temp_set = set(ast.literal_eval(str(row)))

        for item in temp_set:
            chiTermIdSet.add(item)  # indices are term_ids as strings

    nDimensions = len(chiTermIdSet)

    # print nDimensions

def init(needNumpy = True):
    getChiSquareTerms()
    prepareTermIndexMap()
    generateChiFeatureVectors(needNumpy)
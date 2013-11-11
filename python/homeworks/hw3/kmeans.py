from __future__ import division
import csv
import ast
import random
import sys
import numpy

'''
Global Variables

chi_term_id_set
    set of terms/features selected by chi-square selection method

feature_vectors
    doc_id -> feature_vector of only chi-terms

term_id_index_map
    term_id -> index

cluster_points
    index/cluster_number -> feature vector of the centroid

doc_clusters
    doc_id -> cluster index
'''
chi_term_id_set = set()
chi_feature_vectors = {}
term_id_index_map = {}
cluster_points = []
doc_clusters  = {}
nDimensions  = 0
nClusters = 0
convergenceThreshold = 0.125

def generateCentroids(isRandom=False):
    global cluster_points, doc_clusters, nDimensions, nClusters

    nDocuments = len(chi_feature_vectors)

    nClusters = k = 16

    if isRandom:

        # generate initial clusters, random

        random_docs = [random.randrange(1 + x, nDocuments + 1) for x in range(k)]
        cluster_points = [chi_feature_vectors[str(doc)] for doc in random_docs]
    else:

        sum_clusters = [0] * k
        num_clusters = [0] * k

        for i in range(k):
            sum_clusters[i] = [0] * nDimensions

        # TODO : change this to chi_feaure_vectors, instead od doc_lcusters
        for doc_id in doc_clusters:

            cluster_index = doc_clusters[doc_id]
            sum_clusters[cluster_index] = sum_clusters[cluster_index] + chi_feature_vectors[str(doc_id)]
            num_clusters[cluster_index] += 1

        for i in range(k):
            if type(sum_clusters[i]) is numpy.ndarray:
                cluster_points[i] = sum_clusters[i] / num_clusters[i]
        
    assignClusters()


def getDistance(doc_id, cluster_vector):

    doc_feature_vector = chi_feature_vectors[doc_id]

    dist = numpy.linalg.norm(doc_feature_vector - cluster_vector)

    return dist


def assignClusters():

    global doc_clusters, cluster_points

    nDocuments = len(chi_feature_vectors)

    for doc_id in range(1, 500):
        cluster = 0
        min_distance = sys.maxsize

    for (index, cluster_vector) in enumerate(cluster_points):
            dist = getDistance(str(doc_id), cluster_vector)  # distance between point and the current_document
            if dist < min_distance:

                min_distance = dist
                doc_clusters[doc_id] = index

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

    global chi_feature_vectors

    with open('../hw1/doc_term_index_frequency_map.txt', 'r') as fh:
        for line in fh:
            fields = line.split('#')

            doc_id = fields[0]
            term_vector_map = ast.literal_eval(fields[3])

            chi_feature_vectors[doc_id] = prepareChiFeatureVector(term_vector_map)

    file_h_features = open('chi_features.txt', 'w');
    for index in range(1, 500):
        if index > 0:
            print chi_feature_vectors[str(index)]


def getChiSquareTerms():

    global chi_term_id_set, nDimensions

    with open('../hw2/final_term_list.csv', 'r') as f_chi_term_list:
        reader = csv.reader(f_chi_term_list)
        for row in reader:
            temp_set = set(ast.literal_eval(str(row)))

        for item in temp_set:
            chi_term_id_set.add(item)  # indices are term_ids as strings

    nDimensions = len(chi_term_id_set)

def startKMeans():
    global cluster_points, convergenceThreshold

    # generate initial centroids, randomly
    generateCentroids(True)

    # max number of cluster changes = 12.50% of total number of clusters
    # as the number of clusters we consider are powers of two, 12.50% yields an integer number of clusters

    # copy the cluster points, to check for comparison after cluster points are changed after mean calculation
    copy_cluster_points = list(cluster_points)

    nIterations = 0
    toContinue = True
    while toContinue:

        nIterations += 1
        generateCentroids()

        hasChanged = 0
        print '------------Iteration Number ', str(nIterations), '---------------------------------------'
        for index, vector in enumerate(cluster_points):
            dist = numpy.sum(numpy.linalg.norm(copy_cluster_points[index] - vector))
            if dist > 0:
                hasChanged += 1
            
        print "Number of Clusters changed in this iteration ", hasChanged
        if hasChanged / nClusters <= convergenceThreshold:
            # stop clustering
            toContinue = False
        else:
            # continue to find new cluster points
            copy_cluster_points = list(cluster_points)


def main():

    getChiSquareTerms()

    prepareTermIndexMap()

    generateChiFeatureVectors()

    

    startKMeans()
    
main()
from __future__ import division
import random
import sys
import numpy
import time

import preprocess as p
import distance_helper

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

def generateCentroids(isRandom=False):
    # global p.cluster_points, p.doc_clusters, p.nDimensions, p.nClusters, chi_feature_vectors

    nDocuments = len(p.chi_feature_vectors)

    if isRandom:
        # generate initial clusters, random
        random_docs = [random.randrange(1 + x, nDocuments + 1) for x in range(p.nClusters)]
        p.cluster_points = [p.chi_feature_vectors[str(doc)] for doc in random_docs]
    else:

        sum_clusters = [0] * p.nClusters
        num_clusters = [0] * p.nClusters

        print "p.nDimensions ", p.nDimensions, "p.nClusters ", p.nClusters
        for i in range(p.nClusters):
            sum_clusters[i] = numpy.array([0] * p.nDimensions)

        # TODO : change this to chi_feaure_vectors, instead od doc_lcusters
        for doc_id in p.doc_clusters:

            cluster_index = p.doc_clusters[doc_id]
            print sum_clusters[cluster_index].shape, p.chi_feature_vectors[str(doc_id)].shape, doc_id

            sum_clusters[cluster_index] = sum_clusters[cluster_index] + p.chi_feature_vectors[str(doc_id)]
            num_clusters[cluster_index] += 1

        for i in range(p.nClusters):
            if type(sum_clusters[i]) is numpy.ndarray:
                p.cluster_points[i] = sum_clusters[i] / num_clusters[i]
        
    assignClusters()


def assignClusters():

    # global p.doc_clusters, cluster_points, p.chi_feature_vectors

    nDocuments = len(p.chi_feature_vectors)

    # print p.chi_feature_vectors

    for doc_id in range(1, nDocuments):
        cluster = -1
        min_distance = sys.maxsize

        doc_id = str(doc_id)
        for (index, cluster_vector) in enumerate(p.cluster_points):
                dist = distance_helper.getEuclideanDistance(p.chi_feature_vectors[doc_id], cluster_vector,)  # distance between point and the current_document
                
                if dist < min_distance:
                    min_distance = dist
                    p.doc_clusters[doc_id] = index


def startKMeans():
    # global cluster_points, convergenceThreshold

    # generate initial centroids, randomly
    generateCentroids(True)

    # return
    # print convergenceThreshold

    # max number of cluster changes = 12.50% of total number of clusters
    # as the number of clusters we consider are powers of two, 12.50% yields an integer number of clusters

    # copy the cluster points, to check for comparison after cluster points are changed after mean calculation
    copy_cluster_points = list(p.cluster_points)

    nIterations = 0
    toContinue = True
    while toContinue:

        nIterations += 1
        generateCentroids()

        hasChanged = 0
        # print '------------Iteration Number ', str(nIterations), '---------------------------------------'
        for index, vector in enumerate(p.cluster_points):
            dist = numpy.sum(numpy.linalg.norm(copy_cluster_points[index] - vector))
            if dist > 0:
                hasChanged += 1
            
        print "Number of Clusters changed in ", nIterations," iteration is ", hasChanged
        if hasChanged / p.nClusters <= p.convergenceThreshold:
            # stop clustering
            toContinue = False
        else:
            # continue to find new cluster points
            copy_cluster_points = list(p.cluster_points)


def main():

    # global nClusters, convergenceThreshold

    # convergenceThreshold = 0.25

    cluster_sets = set([8, 16, 32])

    # preprocess init
    p.init()

    for n in cluster_sets:
        p.nClusters = n
        start = time.time()
        startKMeans()
        end = time.time()

        print "K-Means Clustering for ", n, " clusters took ", end - start, " time"
    
main()
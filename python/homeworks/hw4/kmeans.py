from __future__ import division
import random
import sys
import numpy
import time
import math
import random
from matplotlib import pyplot as plt
from collections import OrderedDict

import preprocess as p
import distance_helper

global nDocuments, docIdList, clusterDocList

def generateCentroids(isRandom=False):
    
    global nDocuments, docIdList

    # nDocuments = len(p.chiFeatureVectors)

    if isRandom:
        # generate initial clusters, random
        # randrange generates random number in the range [m,n), hence nDocuments+1
        randomDocIds = [random.randrange(1 + x, nDocuments + 1) for x in range(p.nClusters)]
        p.clusterPoints = [p.chiFeatureVectors[str(doc)] for doc in randomDocIds]
    else:

        sumClusters = [0] * p.nClusters
        numClusters = [0] * p.nClusters

        for i in range(p.nClusters):
            sumClusters[i] = numpy.array([0] * p.nDimensions)

        # TODO : change this to chi_feaure_vectors, instead od doc_lcusters
        for docId in docIdList:

            docId = str(docId)
            clusterIndex = p.docClusters[docId]
            # print sumClusters[clusterIndex].shape, p.chiFeatureVectors[str(docId)].shape, docId

            sumClusters[clusterIndex] = sumClusters[clusterIndex] + p.chiFeatureVectors[docId]
            numClusters[clusterIndex] += 1

        for i in range(p.nClusters):
            if numClusters[i] != 0:
                p.clusterPoints[i] = sumClusters[i] / numClusters[i]
        
    assignClusters()


def getLabelForClusters():
    global clusterDocMap, clusterClassMap

    clusterClassMap = {}
    classFrequencyMap = OrderedDict()
    for (index, clusterVector) in enumerate(p.clusterPoints):
        docs = clusterDocMap[index]

        for docId in docs:
            # get the classes for this document, increment the class counter
            classes = p.docIdClassListMap[docId]

            for classId in classes:
                if classId not in classFrequencyMap:
                    classFrequencyMap[classId] = 0
                classFrequencyMap[classId] += 1

        sortedClassFrequencyDict = OrderedDict(sorted(classFrequencyMap.items(), key=lambda t: t[1], reverse=True))
        for classId in sortedClassFrequencyDict:
            clusterClassMap[index] = classId
            break

    print clusterClassMap

def assignClusters():
    global nDocuments, docIdList, clusterDocMap

    # reset the clusterDocMap each time
    clusterDocMap = {}
    for (index, clusterVector) in enumerate(p.clusterPoints):
        clusterDocMap[index] = []

    for docId in docIdList:
        cluster = -1
        min_distance = sys.maxsize

        docId = str(docId)
        for (index, clusterVector) in enumerate(p.clusterPoints):
                # distance between point and the current_document
                dist = distance_helper.getEuclideanDistance(p.chiFeatureVectors[docId], clusterVector) 
                
                if dist < min_distance:
                    # update the cluster this document belongs to
                    min_distance = dist
                    p.docClusters[docId] = index

        # add the document to the cluster in clusterDocMap
        clusterDocMap[p.docClusters[docId]].append(docId)

    # here clusterDocMap will contain the cluster id as key and docIds as list of values
    getLabelForClusters()


def startKMeans():
    
    # generate initial centroids, randomly
    generateCentroids(True)

    # max number of cluster changes = convergenceThreshold*100% of total number of clusters
    
    # copy the cluster points, to check for comparison after cluster points are changed after mean calculation
    copyClusterPoints = list(p.clusterPoints)

    nIterations = 0
    toContinue = True
    while toContinue:

        nIterations += 1

        # cluster centroid will be changed after this
        generateCentroids()

        hasChanged = 0
        for index, vector in enumerate(p.clusterPoints):
            dist = numpy.linalg.norm(copyClusterPoints[index] - vector)
            # distace greater than zero implies the current cluster centroid has changed, in this iteration
            if dist > 0:
                hasChanged += 1
            
        # print "Number of Clusters changed in ", nIterations," iteration is ", hasChanged

        # declare convergence if the ratio of number of clusters changed <= the specified threshold
        if hasChanged / p.nClusters <= p.convergenceThreshold:
            # stop clustering
            toContinue = False
        else:
            # continue to find new cluster points
            copyClusterPoints = list(p.clusterPoints)

classMap = {}
def loadClassMasterMap():
    global classMap

    with open('class_master_map.txt', 'r') as classFileHanlder:
        for line in classFileHanlder:
            # fields[0] -> class name,
            # fields[1] -> class id
            fields = line.split(',')

            classMap[int(fields[1])] = fields[0]

clustersEntropyMap = {}
def displayClusterCounts():

    global nDocuments, docIdList

    clusterClassMap = [0] * p.nClusters
    clusterDocsCount = [0] * p.nClusters

    for i in range(p.nClusters):
        clusterClassMap[i] = {}

    docCount = 0
    for docId in docIdList:
        docId = str(docId)

        clusterIndex = p.docClusters[docId]
        classList = p.docIdClassListMap[docId]

        # gives the number of documents in the current cluster
        clusterDocsCount[clusterIndex] += 1

        if len(classList):
            for classId in classList:
                docCount += 1
                if classId not in clusterClassMap[clusterIndex].keys():
                    clusterClassMap[clusterIndex][classId] = 0
                
                clusterClassMap[clusterIndex][classId] += 1
        # else:
        #     docCount += 1

    for i in range(p.nClusters):
        tempCount = 0

        # print 'Cluster Number ', i
        # print "Class Id, Class Name, Count"

        for classId, count in clusterClassMap[i].items():
            tempCount += count
            # print classId, ', ',  classMap[classId], ', ', count

        # each class is considered a different document
        clusterDocsCount[i] = tempCount

    clusterEntropy = [0] * p.nClusters
    # docCount = 0
    for i in range(p.nClusters):
        for classId, count in clusterClassMap[i].items():
            ratio = count / clusterDocsCount[i]
            # docCount += count
            clusterEntropy[i] += ratio * math.log(ratio, 2)
        clusterEntropy[i] = -1 * clusterEntropy[i]

    overallEntropy = 0
    # print "nDocs ", nDocuments, "Doc Count ", docCount

    for i in range(p.nClusters):
        weight = clusterDocsCount[i]/docCount
        overallEntropy += weight * clusterEntropy[i]

    print "\nOverall Entropy ", overallEntropy
    return overallEntropy

def plotGraphs(clusterSets, clusterTiming, clusterEntropy):

    global nDocuments

    now = str(time.time())

    plt.xlabel('Number of Clusters (n)')
    plt.ylabel('Time Taken for Clustering (t) in s')
    plt.title('Cluster Runtime Plot')

    plt.scatter(clusterSets, clusterTiming)
    plt.savefig('cluster-timing-'+now+'-'+str(nDocuments)+'.png')
    plt.close()

    plt.xlabel('Number of Clusters (n)')
    plt.ylabel('Entropy for Clusters (e)')
    plt.title('Cluster Entropy Plot')

    plt.scatter(clusterSets, clusterEntropy)
    plt.savefig('cluster-entropy-'+now+'-'+str(nDocuments)+'.png')

    plt.close()


def main():
    global nDocuments, docIdList

    clusterSets = [8, 16, 32, 64]
    clusterTiming  = []
    clusterEntropy = []

    # preprocess init
    p.init()
    loadClassMasterMap()
    nDocuments = len(p.chiFeatureVectors)

    # 15000 documents took 742 s to run , i.e 12 mins approx
    # nDocuments = 10000
    docIdList = random.sample(p.chiFeatureVectors.keys(), nDocuments)

    for n in clusterSets:
        p.nClusters = n
        start = time.time()
        startKMeans()
        end = time.time()

        print "\nK-Means Clustering for ", n, " clusters took ", end - start, " time"
        entropy = displayClusterCounts()

        clusterTiming.append(end-start)
        clusterEntropy.append(entropy)
        
        print n, end-start, entropy
        print '-------------------------------------------------------------------------------------\n'
    
    plotGraphs(clusterSets, clusterTiming, clusterEntropy)

main()
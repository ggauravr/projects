from __future__ import division
import distance_helper
import gzip
import ast
import preprocess as p
import random
from matplotlib import pyplot as plt
import math
import time

docList = []
clusterPtsMap = {}
noisePts = []
clusterId = 0
#visited = []
isVisited = [0]*len(docList)
isClustered = [0]*len(docList)
neighbourCount = [0]*len(docList)
eps = 0.4
field = []
sampledDocId = []
sampledDocList = []
clusterMap = {}

def findCluster(minPts):
	global docList, clusterPtsMap, noisePts, clusterId, sampledDocList

	#Itearate through all unvisited points
	for idx, pt in enumerate(sampledDocList):
		if(isVisited[idx] > 0):
			continue

		isVisited[idx] = 1

		neighbourInfo = getNearestPts(pt, idx)
		if (neighbourInfo[1] < minPts):
			noisePts.append(pt)
			continue

		clusterId += 1
		isClustered[idx] = clusterId
		print "Found new Cluster with Id:", clusterId
		# clusterPtsMap[clusterId] = []
		# clusterPtsMap[clusterId].append(pt)
		neighbourPts = neighbourInfo[0]
		count = 0
		for neighbourPtIdx in neighbourPts:

			print neighbourPtIdx, "dds", len(neighbourPts), "length"
			#Mark neighbourPt as visited if it is not visited
			if(isVisited[neighbourPtIdx] == 0):
				isVisited[neighbourPtIdx] = 1
				tempNeighbourInfo = getNearestPts(sampledDocList[neighbourPtIdx], neighbourPtIdx)
				if tempNeighbourInfo[1] >= minPts:
					neighbourPts += tempNeighbourInfo[0]
			if(isClustered[neighbourPtIdx] == 0):
				isClustered[neighbourPtIdx] = clusterId

			# if count%100:
			# 	print "Neighbour pts to process:", len(neighBourPts)
			# 	count += 1



def getNearestPts(currentPt, currentIdx):
	
	nearestPts = []
	if sum(currentPt) == 0:
		return (nearestPts, 1)

	global neighbourCount
	nearestPts = []
	for idx, pt in enumerate(sampledDocList):
		if(isVisited[idx] > 0 and isClustered[idx] > 0):
			continue
		if sum(pt) > 0 and distance_helper.getCosineDistance(currentPt, pt) <= eps:
			nearestPts.append(idx)
			if currentIdx != idx:
				neighbourCount[idx] += 1
			neighbourCount[currentIdx] += 1

	return (nearestPts, neighbourCount[currentIdx])


def main():
	global isVisited, isClustered, neighbourCount, docList, sampledDocId
	p.init(False)
	docList = p.chiFeatureList
	
	sampledDocId = random.sample(list(xrange(21000)), 20000)
	for docId in sampledDocId:
		sampledDocList.append(docList[docId])

	# knnHistogram()
	# drawHistogram()

	isVisited = [0]*len(sampledDocList)
	isClustered = [0]*len(sampledDocList)
	neighbourCount = [0]*len(sampledDocList)
	print(len(sampledDocList))

	start = time.time()
	findCluster(8)

	# fileObj = open("cluster_result.txt", "w")
	# fileObj.write(str(isClustered))
	# fileObj.close()
	evaluate()
	end = time.time()
	print "Running time" ,end - start


def knnHistogram():
	global docList
	knnMap = {}
	knn = [4, 8, 10, 25, 50, 75]
	count = 0
	print len(docList)
	sampledData = random.sample(docList, 10)
	print(len(sampledData))
	for parentIdx, parentPt in enumerate(sampledData):
		distanceEuc = []
		distanceCos = []
		for idx, pt in enumerate(sampledData):
			if parentIdx == idx:
				continue
			else:
				distanceEuc.append(distance_helper.getEuclideanDistance(parentPt, pt))
				distanceCos.append(distance_helper.getCosineDistance(parentPt, pt))
		distanceEuc.sort()
		distanceCos.sort()
		for item in knn:
			if item not in knnMap:
				knnMap[item] = [0]*2
				knnMap[item][0] = []
				knnMap[item][1] = []
			knnMap[item][0].append(distanceEuc[item])
			knnMap[item][1].append(distanceCos[item])
		# if count%20 == 0:
		print "processed pts: ", count + 1
		count += 1
	fileObj = open("knnHistogram.txt", "w")
	fileObj.write(str(knnMap))
	fileObj.close()


def drawHistogram():
	global field
	for line in open("knnHistogram.txt"):
		field = ast.literal_eval(line)
	draw()


def draw():
	ptList = list(xrange(1000))
	i = 0;
	print len(ptList)
	for k,v in field.items():
		for item in v:
			item.sort()
			plt.plot(ptList, item)
			plt.savefig(str(k) + "_" + str(i) + ".png")
			plt.close()
			if i == 1:
				i = 0
			else:
				i += 1

def evaluate():
	global clusterMap, sampledDocId
	docCount = 0
	for idx,clusterId in enumerate(isClustered):
		if clusterId == 0:
			continue
		if clusterId not in clusterMap:
			clusterMap[clusterId] = {}

		# print idx, sampledDocId
		for classId in p.docIdClassListMap[str(sampledDocId[idx])]:
			if classId not in clusterMap[clusterId]:
				clusterMap[clusterId][classId] = 0
			clusterMap[clusterId][classId] += 1
			docCount += 1

	# print clusterMap
	clusterDocsCount = [0] * (len(clusterMap) + 1)
	for i in range(1,len(clusterMap)+1):
		tempCount = 0

		# print 'Cluster Number ', i
		# print "Class Id, Class Name, Count"

		for classId, count in clusterMap[i].items():
		   tempCount += count
		   # print classId, ', ',  classMap[classId], ', ', count

		# each class is considered a different document
		clusterDocsCount[i] = tempCount

	clusterEntropy = [0] * (len(clusterMap) + 1)
	# docCount = 0
	for i in range(1,len(clusterMap)+1):
	   for classId, count in clusterMap[i].items():
			# print classId, count, clusterDocsCount[i]
			ratio = count / clusterDocsCount[i]
			# docCount += count
			clusterEntropy[i] += ratio * math.log(ratio, 2)
	   clusterEntropy[i] = -1 * clusterEntropy[i]
	   print i, clusterEntropy[i]

	overallEntropy = 0
	# print "nDocs ", nDocuments, "Doc Count ", docCount

	for i in range(1,len(clusterMap)+1):
	   weight = clusterDocsCount[i]/docCount
	   # print '\n'
	   # print "cluster ", i 
	   # print "weight ", weight
	   # print "cluster entropy ", clusterEntropy[i]
	   overallEntropy += weight * clusterEntropy[i]

	print "\nOverall Entropy ", overallEntropy
	return overallEntropy



main()


# clusterMap = {}
# for item in isClustered:
# 	if item not in clusterMap:
# 		clusterMap[item] = 0
# 	clusterMap[item] += 1

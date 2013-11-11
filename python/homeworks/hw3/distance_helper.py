import numpy
from scipy import spatial

def getNumpyArray(vector):
  return numpy.array(vector)

def getEuclideanDistance(vector_one, vector_two):
  vector_one = getNumpyArray(vector_one)
  vector_two = getNumpyArray(vector_two)

  return numpy.linalg.norm(vector_one - vector_two)

def getCosineDistance(vector_one, vector_two):
  vector_one = getNumpyArray(vector_one)
  vector_two = getNumpyArray(vector_two)

  return spatial.distance.cosine(vector_one, vector_two)

def getJaccardDistance(vector_one, vector_two):
  vector_one = getNumpyArray(vector_one)
  vector_two = getNumpyArray(vector_two)

  return spatial.distance.jaccard(vector_one, vector_two)
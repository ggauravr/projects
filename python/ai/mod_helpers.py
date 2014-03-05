from __future__ import division
import numpy as np
import math
import ast

def getLabelVector(labelFile):
  trainLabels = []

  # reading training data labels from labelFile
  with open(labelFile, 'r') as fh:
    for line in fh:
      currentLabel = int(line)
      trainLabels.append(currentLabel)
  # converting python list to numpy array, for vector operations
  trainLabels = np.array(trainLabels)

  return trainLabels

def getFeatureVector(featureFile):
  trainFeatures = []

  # reading training features from featureFile
  with open(featureFile, 'r') as fh:
    for line in fh:
      currentFeature = [1]+list(map(int, line.split(' ') ))
      trainFeatures.append(currentFeature)
  # converting python list into numpy array for batch operations
  trainFeatures = np.array(trainFeatures)

  return trainFeatures

def normalizeVector(X):
  _mu = np.mean(X)
  _max  = np.amax(X)
  _min  = np.amin(X)

  return (X - _mu) / (_max-_min)
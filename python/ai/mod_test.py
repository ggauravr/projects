from __future__ import division
import numpy as np
import math
import sys
import ast

import mod_helpers

def TestLogReg(mdlFile, tsFeatureFile, predLabelFile, D):

  global gblTestFeatures
  testFeatures = mod_helpers.getFeatureVector(tsFeatureFile)

  # read model vector from mdlFile
  with open(mdlFile, 'r') as fh:
    for line in fh:
      modelVector = list(map(float, ast.literal_eval(line) ))
  # convert modelVector from list to numpy vector
  modelVector = np.array(modelVector)

  # normalize features.. to avoid overflow errors
  normalizedFeatures = []
  for i in range(testFeatures.shape[0]):
    temp = (mod_helpers.normalizeVector(testFeatures[i])).tolist()
    normalizedFeatures.append(temp)
  # get the normalized features into a global feature vector
  gblTestFeatures = np.array(normalizedFeatures)

  # number of samples in test dataset
  nSamples = testFeatures.shape[0]

  # assume zero, change if h > 0.5
  predictedLabels = [0] * nSamples
  for i in range(nSamples):
    power = np.dot(modelVector, gblTestFeatures[i])
    h = 1 / (1+math.exp(-power))
    label = 0
    # rounding to five-digits
    h = math.ceil(h*(10**5)) / (10**5)
    if h > 0.5:
      label = 1

    predictedLabels[i] = label

  with open(predLabelFile, 'w') as fh:
    for label in predictedLabels:
      fh.write(str(label)+'\n')

def readArguments():
  
  _args = sys.argv

  return _args[1], _args[2], _args[3]

def main():
  mdlFile, tsFeatureFile, predLabelFile= readArguments()

  TestLogReg(mdlFile, tsFeatureFile, predLabelFile, 785)

main()
from __future__ import division
import numpy as np
import math
import sys
import ast

import mod_helpers

def predict(modelVector, feature):
  predictedLabel = 0

  power = np.dot(modelVector, feature)
  h = 1 / (1+math.exp(-power))
  label = 0
  # rounding to five-digits
  h = math.ceil(h*(10**7)) / (10**7)
  if h > 0.5:
    predictedLabel = 1

  return predictedLabel


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
    predictedLabels[i] = predict(modelVector, gblTestFeatures[i])

  with open(predLabelFile, 'w') as fh:
    for label in predictedLabels:
      fh.write(str(label)+'\n')

def TestSample(nSamples, nCorrect, modelVector, feature, trueLabel):
  predictedLabel = predict(modelVector, feature)

  if predictedLabel == trueLabel:
    nCorrect += 1

  # print nCorrect
  if nSamples == nCorrect:
    nSamples += 1

  accuracy = nCorrect / nSamples

  error = 1 - accuracy

  logError = math.log(error, 2)

  # print "predcited : ", predictedLabel, ": true label : ", trueLabel, ": nCorrect : ", nCorrect, nSamples, '\n\n'

  return nCorrect, logError

def readArguments():
  
  _args = sys.argv

  return _args[1], _args[2], _args[3]

def main():
  mod_helpers.readConfigurations()
  
  mdlFile = mod_helpers.getConfig('model')
  tsFeatureFile = mod_helpers.getConfig('test_sample')
  predLabelFile= mod_helpers.getConfig('predict')

  TestLogReg(mdlFile, tsFeatureFile, predLabelFile, 785)

main()
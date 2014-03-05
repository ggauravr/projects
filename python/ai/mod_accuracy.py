from __future__ import division
import numpy as np
import math
import sys
import ast

import mod_helpers

def getAccuracy(predLabelFile, tsLabelFile):

  predictedLabels = mod_helpers.getLabelVector(predLabelFile)
  trueLabels =mod_helpers.getLabelVector(tsLabelFile)

  nSamples = len(predictedLabels)
  nCorrectlyClassified = 0
  for i in range(nSamples):
    if predictedLabels[i] == trueLabels[i]:
      nCorrectlyClassified += 1

  accuracy = nCorrectlyClassified / nSamples

  print "Number of samples tested ", nSamples
  print "Number of samples correctly classified ", nCorrectlyClassified
  print "Accuracy ", accuracy

  return accuracy

def readArguments():
  
  _args = sys.argv

  return _args[1], _args[2]

def main():
  predLabelFile, trueLabelFile = readArguments()

  getAccuracy(predLabelFile, trueLabelFile)

main()
from __future__ import division
import numpy as np
import math
import sys
import ast

import mod_helpers
# import mod_test
# import mod_accuracy

def buildModel(trainLabels, lmbda, D):
	global gblTrainFeatures

	t = 0
	nSamples = gblTrainFeatures.shape[0]
	w = np.array([0]*(D+1))

	for i in range(nSamples):
		t += 1
		wcopy = np.copy(w)
		power = np.dot(w, gblTrainFeatures[i])
		h = 1 / (1+math.exp(-power))
		dw = -(trainLabels[i] - h) *h * (1-h) * gblTrainFeatures[i]
		w = w - (lmbda/t) * dw

	return w.tolist()

def TrainLogReg(trFeatureFile, trLabelFile, mdlFile, D, lmbda, Niter):

	global gblTrainFeatures

	trainFeatures = mod_helpers.getFeatureVector(trFeatureFile)
	trainLabels = mod_helpers.getLabelVector(trLabelFile)
	nSamples = trainFeatures.shape[0]

	# normalize before building the model, to prevent overflow errors
	normalizedFeatures = []
	for i in range(nSamples):
		temp = (mod_helpers.normalizeVector(trainFeatures[i])).tolist()
		normalizedFeatures.append(temp)

	gblTrainFeatures = np.array(normalizedFeatures)

	modelVector = buildModel(trainLabels, lmbda, D)

	with open(mdlFile, 'w') as fh:
		fh.write(str(modelVector))

def readArguments():
  
	_args = sys.argv

	return _args[1], _args[2], _args[3]


def main():
	prdLabelFile = 'predictedLabels.dat'
	trFeatureFile, trLabelFile, mdlFile = readArguments()

	nDimensions = 785

	# lmbdas = [10**-4, 10**-5, 10**-6, 10**-7, 10**-8, 10**-9, 10**-10]
	lmbdas = [10**-10]
	for lmbda in lmbdas:
		TrainLogReg(trFeatureFile, trLabelFile, mdlFile, nDimensions, lmbda, 1)

main()
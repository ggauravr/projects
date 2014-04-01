from __future__ import division
import numpy as np
import math
import sys
import ast
import os

sys.path.insert(0, 'modules')
import mod_helpers
import mod_test
import mod_visualize

def buildModel(trainLabels, lmbda, D, tau):
	global gblTrainFeatures
	gradients = [0]

	# print "tau: ", tau

	accuracies = []
	nCorrect    = 0

	t = 0
	nSamples = gblTrainFeatures.shape[0]
	w = np.array([0]*(D+1))
	gradients = [0]

	for i in range(nSamples):
		t += 1

		# compute the loss function and the gradient
		wcopy = np.copy(w)
		power = np.dot(w, gblTrainFeatures[i])
		# lmbda = np.var(w)
		h = 1 / (1+math.exp(-power))
		dw = -(trainLabels[i] - h) *h * (1-h) * gblTrainFeatures[i]

		# append/cache the gradient
		gradients.append(np.copy(dw))
		
		nCorrect, accuracy = mod_test.TestSample(t, nCorrect, w, gblTrainFeatures[i], trainLabels[i])
		accuracies.append(accuracy)

		if t >= tau+1: # tau+1 = n -> number of clients
			# w = w - (lmbda/math.sqrt(t-tau)) * gradients[t-tau]
			w = w - (lmbda/(t-tau)) * gradients[t-tau]
			# print "t = ", t, ".. using gradient computed at time = ", t-tau, " so delay is ", tau
		else: # t < tau+1
			w = w# - (lmbda/t) * gradients[1]

	return w.tolist(), accuracies

def TrainLogReg(trFeatureFile, trLabelFile, mdlFile, D, lmbda, Niter):
	global gblTrainFeatures
	taus = [0, 1, 10, 100, 1000]
	trainFeatures = mod_helpers.getFeatureVector(trFeatureFile)
	trainLabels = mod_helpers.getLabelVector(trLabelFile)
	nSamples = trainFeatures.shape[0]

	# normalize before building the model, to prevent overflow errors
	normalizedFeatures = []
	for i in range(nSamples):
		temp = (mod_helpers.normalizeVector(trainFeatures[i])).tolist()
		normalizedFeatures.append(temp)

	gblTrainFeatures = np.array(normalizedFeatures)
	accuracies = [0] * len(taus)

	for i in range(len(taus)):
		modelVector, accuracies[i] = buildModel(trainLabels, lmbda, D, taus[i])
		# print accuracies[i], '\n'

	mod_visualize.plotAccuracy(accuracies, taus)

	with open(mdlFile, 'w') as fh:
		fh.write(str(modelVector))

def readArguments():
  
	_args = sys.argv

	return _args[1], _args[2], _args[3]

def main():
	mod_helpers.readConfigurations()
	trFeatureFile 	= mod_helpers.getConfig('training_sample')
	trLabelFile 		= mod_helpers.getConfig('training_label')
	mdlFile 		= mod_helpers.getConfig('model')

	nDimensions = 785

	# lmbdas = [10**-4, 10**-5, 10**-6, 10**-7, 10**-8, 10**-9, 10**-10]
	lmbdas = [1]
	for lmbda in lmbdas:
		TrainLogReg(trFeatureFile, trLabelFile, mdlFile, nDimensions, lmbda, 1)

main()
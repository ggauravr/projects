from __future__ import division
import numpy as np
import math
import sys
import ast

# features = np.array([])

def prepareFeatureVector():

  global features, labels, nSamples, nDimensions

  featureVector = []
  labelVector = []

  with open('trainingFeature.dat', 'r') as fh:
    for line in fh:
      currentFeature = list(map(int, line.split(' ') ))

      featureVector.append(currentFeature)

  features = np.array(featureVector)

  nSamples = features.shape[0]
  nDimensions = features.shape[1]

  with open('trainingLabel.dat', 'r') as fh:
    for line in fh:
      currentLabel = int(line)

      labelVector.append(currentLabel)

    labels = np.array(labelVector)


def buildModel(trainFeatures, trainLabels, lmbda, D):
  
  hValues = []

  t = 0
  # lmb = 1/10000

  nSamples = trainFeatures.shape[0]
  w = np.array([0]*D)

  while t < 10:
    wcopy = w
    t += 1
    
    hValues = []
    for i in range(nSamples):
      power = np.dot(w, trainFeatures[i])
      h = 1 / (1+math.exp(-power))
      # calculate estimated probability for i-th sample, store it
      hValues.append(h)

    # convert to a vector
    hValues = np.array(hValues)
    print len(hValues)

    # loss derivative, vector of D dimensions
    dw = (trainLabels[i]-hValues) * hValues * (1-hValues) * trainFeatures[i]

    # update the regression co-efficients
    w = w - (lmbda/t)*dw

    # if the regression coefficients don't change
    # convergence is achieved
    # else continue, with new co-efficient vector
    print "Iteration ", t
    print sum(w-wcopy)
    if sum(w-wcopy) == 0:
      break

  return w.tolist()


# prepareFeatureVector()

# buildModel()

def TrainLogReg(trainingFeatureFile, trainingLabelFile, modelFile, D, lmbda, Niter):

  trainFeatures = []
  trainLabels = []

  # reading training features from trainingFeatureFile
  with open(trainingFeatureFile, 'r') as fh:
    for line in fh:
      currentFeature = list(map(int, line.split(' ') ))
      trainFeatures.append(currentFeature)
  # converting python list into numpy array for batch operations
  trainFeatures = np.array(trainFeatures)

  # reading training data labels from trainingLabelFile
  with open(trainingLabelFile, 'r') as fh:
    for line in fh:
      currentLabel = int(line)
      trainLabels.append(currentLabel)
    # converting python list to numpy array, for vector operations
    trainLabels = np.array(trainLabels)

    modelVector = buildModel(trainFeatures, trainLabels, lmbda, D)

    with open(modelFile, 'w') as fh:
      fh.write(str(modelVector))

def TestLogReg(modelFile, testFeatureFile, predLabelFile, D):

  testFeatures = []

  # reading the model vector from modelFile
  # converting string to list using ast module
  with open(modelFile, 'r') as fh:
    for line in fh:
      modelVector = list(map(int, ast.literal_eval(line) ))

  modelVector = np.array(modelVector)

  # readinf the features from test data
  with open(testFeatureFile, 'r') as fh:
    for line in fh:
      testFeatures.append(list(map(int, line.split(' ') )))

  testFeatures = np.array(testFeatures)

  # run logistic regression model
  nSamples = testFeatures.shape[0]
  predictedLabels = [0] * nSamples
  for i in range(nSamples):
    print sum(testFeatures[i])
    power = np.dot(modelVector, testFeatures[i])
    # predicted probability of class = 1
    h = 1 / (1+math.exp(-power))

    label = 0
    # print sum(modelVector)
    if h > 0.5:
      label = 1

    # print h
    predictedLabels.append(label)

  with open(predLabelFile, 'w') as fh:
    for label in predictedLabels:
      fh.write(str(label)+'\n')

def Accuracy(predLabelFile, trueLabelFile):

  predictedLabels = []
  with open(predLabelFile, 'r') as fh:
    for line in fh:
      predictedLabels.append(int(line))

  trueLabels = []
  with open(trueLabelFile, 'r') as fh:
    for line in fh:
      trueLabels.append(int(line))

  nSamples = len(predictedLabels)
  nCorrectlyClassified = 0
  for i in range(nSamples):
    if predictedLabels[i] == trueLabels[i]:
      nCorrectlyClassified += 1

  accuracy = nCorrectlyClassified / nSamples

  print "Sample Size is: ", nSamples, " Correctly Classified Items: ", nCorrectlyClassified, " Accuracy: ", accuracy, '\n'

  return accuracy

def main():
  trainingFeatureFile = sys.argv[1]
  trainingLabelFile = sys.argv[2]
  modelFile = sys.argv[3]

  # lmbdas = [10**-4, 10**-5, 10**-6, 10**-7, 10**-8, 10**-9, 10**-10]

  lmbdas = [10**-4]
  for lmbda in lmbdas:
    TrainLogReg(trainingFeatureFile, trainingLabelFile, modelFile, 785, lmbda, 0)
    TestLogReg(modelFile, trainingFeatureFile, "predLabels.dat", 785)
    # accuracy = Accuracy("predLabels.dat", trainingLabelFile)

    # print "lambda: ", lmbda, " Accuracy: ", lmbda, '\n'

main()
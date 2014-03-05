from __future__ import division
import numpy as np
import math
import ast
import yaml
import os

def readConfigurations():
  global config

  configFile = file('config.yaml', 'r')
  config = yaml.load(configFile)

def getConfig(pKey, filename=''):

  rValue = ''
  cJoin   = os.path.join

  if pKey == 'training_sample' or pKey == 'training_label' or pKey == 'test_sample':
    rValue = cJoin(config['dir']['input'], config['file']['input'][pKey])
  elif pKey == 'model' or pKey == 'predict':
    rValue = cJoin(config['dir']['output'], config['file']['output'][pKey])
  elif pKey == 'test_label':
    rValue = cJoin(config['dir']['input'], filename)

  return rValue

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
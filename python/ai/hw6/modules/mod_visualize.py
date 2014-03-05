import matplotlib.pyplot as plt

def plotAccuracy(accuracies, taus):

  graphParams = ['r', 'g', 'b--', 'g--', 'r--','b' , 'r^']

  nSamples = len(accuracies[0])
  xAxis = list(range(1, nSamples+1))

  for i in range(len(accuracies)):
    plt.plot(xAxis, accuracies[i], graphParams[i], label=str(taus[i]))

  plt.legend(loc='upper right')
  plt.show()
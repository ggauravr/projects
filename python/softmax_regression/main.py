import numpy
import scipy.optimize
import array
import time
import scipy.sparse

import softmax
import data_helper

###########################################################################################
""" Loads data, trains the model and predicts classes for test data """

def executeSoftmaxRegression():
    
    """ Initialize parameters of the Regressor """
    
    n_dimensions     = 784    # input vector size
    n_classes    = 10     # number of classes
    lamda          = 0.0001 # weight decay parameter
    max_iterations = 100    # number of optimization iterations
    
    """ Load MNIST training images and labels """
    
    training_data   = data_helper.loadMNISTImages('train-images.idx3-ubyte')
    training_labels = data_helper.loadMNISTLabels('train-labels.idx1-ubyte')
    
    """ Initialize Softmax Regressor with the above parameters """
    
    regressor = softmax.SoftmaxRegression(n_dimensions, n_classes, lamda)
    
    """ Run the L-BFGS algorithm to get the optimal parameter values """
    
    opt_solution  = scipy.optimize.minimize(regressor.softmaxCost, regressor.theta, 
                                            args = (training_data, training_labels,), method = 'L-BFGS-B', 
                                            jac = True, options = {'maxiter': max_iterations})
    opt_theta     = opt_solution.x
    
    """ Load MNIST test images and labels """
    
    test_data   = data_helper.loadMNISTImages('t10k-images.idx3-ubyte') 
    test_labels = data_helper.loadMNISTLabels('t10k-labels.idx1-ubyte')
    
    """ Obtain predictions from the trained model """
    
    predictions = regressor.softmaxPredict(opt_theta, test_data)
    
    """ Print accuracy of the trained model """
    
    correct = test_labels[:, 0] == predictions[:, 0]
    print """Accuracy :""", numpy.mean(correct)
    
executeSoftmaxRegression()

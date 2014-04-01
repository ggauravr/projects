% function rbf_optimal
% trains an RBF kernel SVM classifier
% using the best hyper parameters found from cross-validation
% and evaluates the performance on the test data provided

function [ ] = rbf_main( )
    
    % prepare training data
    load('train79.mat');
    x_train = d79;

    [n_samples n_dimensions] = size(x_train);
    k = 10;

    % prepare labels for training data
    y_train = zeros(n_samples, 1);
    y_train(1:n_samples/2) = 7;
    y_train(n_samples/2+1:n_samples) = 9;

    % prepare test data and labels
    load('test79.mat');
    x_test = d79;
    y_test = y_train;

    % obtained from grid-search and cross-validation
    C = 10;
    sigma = 100;

    SVMModel = svmtrain(x_train, y_train, 'kernel_function', 'rbf', 'rbf_sigma', sigma, 'boxconstraint', C);
    label = svmclassify(SVMModel, x_test);

    % evaluate and display results
    CP = classperf(y_test, label);
    CP.CorrectRate
    CP.DiagnosticTable

end
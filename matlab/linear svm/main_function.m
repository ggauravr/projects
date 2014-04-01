function [ ] = main_function( input_args )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

load('train79.mat');
X = d79;
Y = repmat(7, 1, 1000);
Y = [Y repmat(9, 1, 1000)];
Y = transpose(Y);

[rows, cols] = size(d79(:, 1));
indices = crossvalind('Kfold', Y, 10);

CP = classperf(Y);

for i = 1:3
    test = (indices == i); train = ~test;
    
    train_data = d79(train, :);
    train_labels = Y(train, :);

    test_data = d79(test, :);
    test_labels = Y(test, :);
    
    SVMModel = svmtrain(train_data,train_labels);
    predicted_labels = svmclassify(SVMModel, test_data);
    
    CP = classperf(test_labels, predicted_labels)
    
    CP.DiagnosticTable
    
end 

end
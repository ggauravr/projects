% function lsq_main
% trains a linear least-squares SVM, and evalutes on the original test data given

function [ ] = lsq_main( )

  load('train79.mat');
  x_train = d79;

  [n_samples n_dimensions] = size(x_train);
  k = 10;

  y_train = zeros(n_samples, 1);
  y_train(1:n_samples/2) = 7;
  y_train(n_samples/2+1:n_samples) = 9;

  % perform cross-validation
  cross_validate(x_train, y_train, n_samples, k);

  load('test79.mat');
  x_test = d79;
  y_test = y_train;

  % train the Linear SVM Model
  SVMStruct = svmtrain(x_train, y_train);
  label = svmclassify(SVMStruct, x_test);
  CP = classperf(y_test, label);
  CP.DiagnosticTable;
  CP.CorrectRate

  % linear least-squares SVM
  SVMStruct = svmtrain(x_train, y_train, 'method', 'LS');
  label = svmclassify(SVMStruct, x_test);
  CP = classperf(y_test, label);
  CP.DiagnosticTable;
  CP.CorrectRate

end
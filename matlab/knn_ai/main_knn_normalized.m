% function main_knn_normalized
% 
% @param
% none
% 
% @return
% none
% 
% @description
% same as main_knn, just does normalization before training

function [ ] = main_knn_normalized( )

  [x_train y_train x_test y_test] = load_data();

  % preprocess/normalize data
  % max(matrix) gives max value from each column/feature of the matrix

  x_train = normalize(x_train);
  x_test  = normalize(x_test);

  % x_train_max = max(x_train);
  % x_train = bsxfun(@rdivide, x_train, x_train_max);

  % x_test_max = max(x_test);
  % x_test = bsxfun(@rdivide, x_test, x_test_max);

  % fit k-nn on the train data using default of one neighbor
  % and euclidean distance measure
  knn_model = fitcknn(x_train, y_train);

  % predict for the test data set, using the trained model
  y_hat = predict(knn_model, x_test);

  % measure performance of the prediction
  Performance = classperf(y_hat, y_test);

  % outcome of the prediction, accuracy, error rate and confusion matrix
  Performance.CorrectRate
  Performance.ErrorRate
  Performance.DiagnosticTable

end
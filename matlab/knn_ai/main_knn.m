% function main_knn
% 
% @param
% none
% 
% @return
% none
% 
% @description
% trains a knn classifier using 1-nearest neighbor
% predicts labels for the test set
% evaluates the performance and displays the 
% accuracy, error rate and confusion matrix

function [ ] = main_knn( )

  [x_train y_train x_test y_test] = load_data();

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
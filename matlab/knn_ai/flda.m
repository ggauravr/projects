% function flda
% 
% @param
% none
% 
% @return
% none
% 
% @description
% trains a fishers linear discriminant classifier, one-vs-rest,
% that is builds k binary classifiers, where k is the number of classes in the dataset
% evaluates its performance

function [ ] = flda( )
  
  [x_train y_train x_test y_test] = load_data();

  n_classes = size(unique(y_train), 1);

  % arrays to hold k models and bias,
  % where k is the number of classes, 3 here
  models = [];
  bias     = [];
  
  for i = 1:n_classes
    
    % boolean array to divide positive and negative samples
    indicator = y_train == i-1;
    
    x_positive  = x_train(indicator, :);
    x_negative = x_train(~indicator, :);

    mu_positive   = mean(x_positive);
    mu_negative  = mean(x_negative);
    
    sw_positive  = cov(x_positive);
    sw_negative = cov(x_negative);
    sw = sw_positive + sw_negative;

    % store the model trained and bias for the i-th classifier
    model(i, :) = inv(sw) * (mu_positive - mu_negative)';
    bias(i, :)  = [-0.5*model(i, :) * (mu_positive + mu_negative)'];
  end
  
  % for every sample in the test set, run all the three(k) classifiers
  % and determine the class that gives the highest value for it
  y_predicted = [];
  for i = 1:size(x_test, 1)

    class_votes = []
    for j = 1:n_classes
      y_hat = model(j, :)*x_test(i, :)' + bias(j, :);
      class_votes = [class_votes y_hat];
    end

    [max_value index] = max(class_votes);
    y_predicted = [y_predicted; index-1];
  end

  Performance = classperf(y_predicted, y_test);

  Performance.CorrectRate
  Performance.ErrorRate
  Performance.DiagnosticTable

end
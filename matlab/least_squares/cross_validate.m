% function cross_validate
% trains and compares linear SVM and a least squares linear classifier 
% using 10-fold cross validation

% @params :
% X - complete training data
% Y - complete training labels
% n_samples - number of original training samples, in X
% k - parameter for k-fold cross-validation

function [ ] = cross_validate( X, Y, n_samples, k )
    
    % arrays to hold the cross-validation accuracies
    rate_SVM = [];
    rate_LS  = [];

    % generate random indices for cross-validation
    indices = crossvalind('Kfold', n_samples, k);
    for i = 1:10
        
        i_test = (indices == i);
        i_train = ~i_test;
        
        x_train = X(i_train, :);
        y_train = Y(i_train, :);

        x_test = X(i_test, :);
        y_test = Y(i_test, :);
        
        % linear SVM
        SVMStruct = svmtrain(x_train, y_train);
        label = svmclassify(SVMStruct, x_test);
        
        CP = classperf(y_test, label);
        rate_SVM = [rate_SVM CP.CorrectRate];

        % linear least-squares SVM
        SVMStruct = svmtrain(x_train, y_train, 'method', 'LS');
        label = svmclassify(SVMStruct, x_test);
        
        CP = classperf(y_test, label);
        rate_LS = [rate_LS CP.CorrectRate];
        
    end

    % graph plot
    plot([1:10], rate_SVM, [1:10], rate_LS)
    title('Linear SVM vs Least Squares Linear Classifier with 10-fold cross-validation')
    xlabel('k-th test sample')
    ylabel('Accuracy')
    h_legend = legend('SVM', 'Least Squares Linear')
    axis([0 10 0.5 1])

end
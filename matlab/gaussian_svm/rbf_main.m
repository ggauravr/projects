% function rbf_main:
% trains an RBF kernel SVM Classifier
% and finds the best C(gamma), and sigma(kernel width) parameter,
% using grid-search technique
function [ ] = rbf_main( )
    
    load('train79.mat');
    X = d79;

    [n_samples n_dimensions] = size(X);
    k = 10;

    % prepare the labels, first 1000 are 7s and the next 1000 are nines
    Y = zeros(n_samples, 1);
    Y(1:n_samples/2) = 7;
    Y(n_samples/2+1:n_samples) = 9;

    % generate random indices for k-fold cross-validation
    indices = crossvalind('Kfold', n_samples, k);

    % try boxconstraint C from 10^-2 to 10^2, with exponents in step size of 1
    c_list = -2:1:2;

    % try kernel-width sigma from 10^-5 to 10^5, with exponents in step size of 1
    sigma_list = -5:1:5;

    result = [];

    best_result = 0;
    best_C = 0;
    best_sigma = 0;

    % grid-search to find best C,sigma combination for SVM
    for i = 1:range(c_list)
        C = 10^c_list(i);

        for j = 1:range(sigma_list)
            sigma = 10^sigma_list(j);

            % 10-fold cross-validation
            for k = 1:10
                i_test = (indices == k);
                i_train = ~i_test;

                transpose(i_train);

                x_train = X(i_train, :);
                y_train = Y(i_train, :);

                x_test = X(i_test, :);
                y_test = Y(i_test, :);

                SVMModel = svmtrain(x_train, y_train, 'kernel_function', 'rbf', 'rbf_sigma', sigma, 'boxconstraint', C);
                label = svmclassify(SVMModel, x_test);    

                CP = classperf(y_test, label);
                
                if CP.CorrectRate > best_result
                    best_result = CP.CorrectRate;
                    best_C = C;
                    best_sigma = sigma;
                end

            end

        end
    end

    % cross-valdiation again using the best hyperparameters found above
    accuracy = [];    
    for k = 1:10
        i_test = (indices == k);
        i_train = ~i_test;

        transpose(i_train);

        x_train = X(i_train, :);
        y_train = Y(i_train, :);

        x_test = X(i_test, :);
        y_test = Y(i_test, :);

        SVMModel = svmtrain(x_train, y_train, 'kernel_function', 'rbf', 'rbf_sigma', best_sigma, 'boxconstraint', best_C);
        label = svmclassify(SVMModel, x_test);    

        CP = classperf(y_test, label);
        
        accuracy = [accuracy CP.CorrectRate];

    end

    plot([1:k], accuracy)
    title('10-fold cross-validation accuracies with best C, sigma')
    xlabel('k-th test sample')
    ylabel('accuracy rate')
    h_legend = legend('RBF Kernel with C = 10, sigma = 100')
    axis([0 10 0.5 1])

end
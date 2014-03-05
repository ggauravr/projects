/*
    main entry point of the program.. 

    calls all other helper functions

    and plots the graph of error-vs-dimensions
*/
function [ids_one, ids_three] = main_function( )
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here
errors_one = [];
errors_three = [];

 for i = 1:11
    d = i*10+1;
    
    train_points = get_data_points(200, d);
    
    test_points = get_data_points(1000, d);
    
    % 1-NN
    [ids_one, distances_one] = knnsearch(train_points, test_points);
    error_one = predict_one_nn(ids_one, train_points, test_points, d);
    
    % 3-NN
    [ids_three, distances_three] = knnsearch(train_points, test_points, 'K', 3);
    error_three = predict_three_nn(ids_three, train_points, test_points, d);
    
    errors_one = [errors_one; error_one];
    errors_three = [errors_three; error_three];
    
 end
 
 xaxis = [0:10]*10+1;
 plot(xaxis, errors_one,'-o', xaxis, errors_three, ':o')
 l = legend('1-NN', '3-NN')
 set(l,'Location','NorthWest')
 set(l,'Interpreter','none')
end


/*
    generated n data points in d dimensions, by simulating a coin flip
    to decide the distribution from which the sample is to be generated
*/
function [ data_points ] = get_data_points( n, d )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
mus = [zeros(1, d); [3 zeros(1, d-1)]];
sigma = eye(d, d);

data_points = [];

for i = 1:n
    
    if rand > 0.5
        which = 2;
    else
        which = 1;
    end
    
    data_point = mvnrnd(mus(which, :), sigma, 1);
    label = get_label(data_point, mus, sigma);
    data_points = [data_points;[ data_point label ]];
    
end

function [ label ] = get_label( data_point, mus, sigma )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
    y_one = mvnpdf(data_point, mus(1, :), sigma);
    y_two = mvnpdf(data_point, mus(2, :), sigma);
    
    label = y_one;
    
    if y_one/y_two > 1
        label = 1;
    else
        label = 2;
    end

end

/*
    predicts and evaluates the error rate/misclassifications of 1-nn classifier
*/
function [ error ] = predict_one_nn( ids_one, train_points, test_points, d )
%UNTITLED3 Summary of this function goes here
%   Detailed explanation goes here

error = 0;
predicted_ = [];
original_ = [];

for i = 1:numel(ids_one)
   index = ids_one(i);
   predicted = train_points(index, d+1);
   original  = test_points(index, d+1);
   
   predicted_ = [predicted_ predicted];
   original_  = [original_ original];
   
   if original-predicted ~= 0
       error = error +1;
   end
   
end

end

/*
    predicts and evaluates the error of 3-NN classifier
*/
function [ error_rate ] = predict_three_nn( ids_three, train_points, test_points, d )
%UNTITLED4 Summary of this function goes here
%   Detailed explanation goes here
    
    error_rate = 0;
    s = size(ids_three);
    
    for i = 1:s(1)
        row = ids_three(i, :);
        classes = [];
        
        for j = 1:numel(row)
            index = row(j);
            
            predicted = train_points(index, d+1);
            original = test_points(index, d+1);
            
            if predicted - original ~= 0
                classes = [classes 1];
            else
                classes = [classes 0];
            end
        end
        
        if length(classes(classes > 0)) >= 2
            error_rate = error_rate + 1;
    end

end
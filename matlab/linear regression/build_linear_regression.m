% function : build_linear_regression
%
% input:
%   X - samples for training
%   Y - values
%
% output:
%   model - model learned from the linear regression
%           OR the regression coefficients

function [ model ] = build_linear_regression( X, Y )

    % append bias terms to X
    X = [X ones(length(X), 1)];
    
    % mldivide
    % last element of the model will be the bias
    model = X\Y;

end


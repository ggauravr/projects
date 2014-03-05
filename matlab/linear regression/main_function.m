function [ ] = main_function( )

    % load features and the labels 
    % into matlab vectors
    X = load('xdata.txt');
    Y = load('ydata.txt');

    model = build_linear_regression(X ,Y );
    
    % print the learned model/ coefficients
    model

end


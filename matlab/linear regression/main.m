function [ ] = main_function( )

    X = load('xdata.txt');
    Y = load('ydata.txt');

    model = build_linear_regression(X ,Y );
    model

end
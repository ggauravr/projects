function [ model ] = build_linear_regression( X, Y )
  
  X = [X ones(length(X), 1)];

  model = X\Y;

end
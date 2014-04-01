% function normalize
% 
% @param x                   
% data matrix to be normalized
% 
% @return x_normalized
% data matrix normalized
% 
% @description
% takes a data matrix to be normalized
% finds the absolute max value from each column/feature
% divides each column by the max value, hence normalizing

function [ x_normalized ] = normalize( x )
  
  x_max = max(abs(x));
  x_normalized = bsxfun(@rdivide, x, x_max);

end
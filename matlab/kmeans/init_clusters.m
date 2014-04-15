% function : init_clusters
%
% input    : x - data, k - number of clusters
%
% output   : clusters - k x nDimensions matrix, each row is a mean

function [ clusters ] = init_clusters( x, k )
    
    % generate k random indices from 1 to number of rows
    indices = randsample(size(x, 1), k);
    
    % get the points from the indices
    clusters = x(indices, :);

end


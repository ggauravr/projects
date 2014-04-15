% function : assignment_score
%
% input    : x - data, 
%            cluster_means - k * nDimensions matrix, each row indicating mean of the cluster
%            cluster_covarainces - array of nDimensions * nDimensions matrix
%            priors - priors of k clusters
%            nSamples - number of samples in test dataset
%            k - number of clusters
%
% output    : assignment_score
function [ assignment_score ] = get_assignment_score( x, cluster_means, cluster_covariances, priors, nSamples, k )
    
    assignment_score = zeros(nSamples, k);

    for i = 1:k
        assignment_score(:, i) = priors(i, :) * mvnpdf(x, cluster_means{i, :}, cluster_covariances{i, :});
    end
    
    % normalize, to get the probability 
    % divide each number by the sum of the columns in that row
    for i = 1:nSamples
       % sum(X, 2) indicates sum over the row
       normalizer = sum(assignment_score(i, :), 2);
       assignment_score(i, :) = assignment_score(i, :) / normalizer;
    end

end


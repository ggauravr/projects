% function: get_likelihood
%
% input   : same as in other files
%
% output  : log likelihood

function [ log_likelihood ] = get_likelihood( x, cluster_means, cluster_covariances, priors, nSamples, k )

    likelihood_matrix = zeros(nSamples, k);
    for i = 1:k
       likelihood_matrix(:, i) = priors(i, :) * mvnpdf(x, cluster_means{i, :}, cluster_covariances{i, :});
    end
    
    likelihood = sum(likelihood_matrix, 2);
    log_likelihood = sum(log(likelihood));

end


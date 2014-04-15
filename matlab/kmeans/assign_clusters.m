% function : assign_clusters
%
% input    : x - data, clusters - k x nDimensions matrix of cluster centers
%            nSamples - number of samples, k - number of clusters
%
% output   : assignment - nSamples x 1 vector, each row indicating the
%            cluster index of that point

function [ assignment ] = assign_clusters( x, clusters, nSamples, k )
    
    assignment = zeros(nSamples, 1);

    for i = 1:nSamples
      
      % gives the difference between one test point to all the cluster points
      % (xi-mu)
      delta = bsxfun(@minus, clusters, x(i, :));
      norms = zeros(1, k);

      for j = 1:k
        % norm(xi - mu), gives the euclidean distance
        norms(1, j) = norm(delta(j, :));
      end
      
      % minimum of the norms is the assigned cluster
      [minDistance, clusterIndex] = min(norms);

      assignment(i, :) = clusterIndex;

    end
    
    % find and reassign empty clusters
    for i = 1:k
      indicator = assignment == i;
      nPointsInCluster = sum(indicator);

      % if number of points in this cluster is 0, 
      % assign some random point to it
      if nPointsInCluster == 0
        randomPoint = randsample(nSamples, 1);
        assignment(randomPoint, :) = i;
      end
    end

end


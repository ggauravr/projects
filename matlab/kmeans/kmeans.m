% function: kmeans
%
% input   : none
%
% output  : rand index

function [ rand_score ] = kmeans()
  
  x = load('test_data.txt');
  
  % dimensions of data - n x m matrix
  [nSamples, nDimensions] = size(x);
  
  % number of clusters
  k = 3;
  
  % get random points from the dataset as the initial clusters
  clusters = init_clusters(x, k);
  
  % count for number of steps to convergence
  count = 0;
  prev_clusters = zeros(k, nDimensions);
  
  % while difference between the clusters is not zero
  while sum(sum(abs(clusters - prev_clusters))) > 0
    count = count + 1;
    prev_clusters = clusters;
    
    % Expectation - Assign Clusters
    assignment = assign_clusters(x, clusters, nSamples, k);
    
    % Maximization - Recompute Clusters
    for i = 1:k
      indicator = assignment == i;
      
      % cant be zero because of the previous step
      nPointsInCluster = sum(indicator);
      pointsInCluster = x(indicator, :);
      mu = sum(pointsInCluster)/nPointsInCluster;

      clusters(i, :) = mu;
    end

  end
  
  rand_score = get_rand_score(assignment)
  
end
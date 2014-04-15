function [ rand_score ] = gmm( )
    
    x = load('test_data.txt');
    [nSamples nDimensions] = size(x);
    
    k= 3;

    % get random initial clusters
    clusters = init_clusters(x, k);
    
    % Assign Clusters
    assignment = assign_clusters(x, clusters, nSamples, k);
    
    cluster_means = cell(k, 1);
    cluster_covariances = cell(k, 1);
    priors = zeros(k, 1);
    
    % find the initial estimates of mean, covariance and priors
    for i = 1:k
      indicator = assignment == i;
      nPointsInCluster = sum(indicator);
      
      pointsInCluster = x(indicator, :);
      mu = sum(pointsInCluster)/nPointsInCluster;

      % adding the parameter for stability
      sigma_squared = cov(pointsInCluster) + .0001 * eye(nDimensions);
      
      cluster_means(i, :) = {mu};
      cluster_covariances(i, :) = {sigma_squared};
      priors(i, :) = nPointsInCluster/nSamples;
    end
    
    likelihood = get_likelihood(x, cluster_means, cluster_covariances, priors, nSamples, k);
    count = 0;
    
    prev_likelihood = 0;
    threshold = .01;
    
    % continue till convergence, till the change in likelihood is below the threshold
    while abs(likelihood - prev_likelihood) > threshold
        count = count + 1;
        prev_likelihood = likelihood;

        % [i,j] element in this matrix is the assignment score of point i, with
        % cluster k
        assignment_score = get_assignment_score(x, cluster_means, cluster_covariances, priors, nSamples, k);
        
        % get maximum elements in a row, and their index
        [max_scores, clusters] = max(assignment_score, [], 2);
        assignment = clusters;
        
        % update parameters using the assignment_score
        for i = 1:k

            % update means first
            nk = sum(assignment_score(:, i));
            cluster_means(i, :) = { sum( bsxfun(@times, x, assignment_score(:, i) ) )/nk };

            % use the updated means to update covariances
            diff = bsxfun(@minus, x, cluster_means{i, :});

            % multiplying the assignment_score of k-th class
            scaled = bsxfun(@times, diff, assignment_score(:, i));

            % scaled and diff are row vectors here
            cluster_covariances(i, :) = { ( (transpose(scaled) * diff)/nk ) + .001 * eye(nDimensions) };

            % updating priors
            priors(i, :) = nk/ nSamples;
        end    

        likelihood = get_likelihood(x, cluster_means, cluster_covariances, priors, nSamples, k);
    end
    
    rand_score = get_rand_score(assignment);
end
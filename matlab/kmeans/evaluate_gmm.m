function [ ] = evaluate_gmm()
    
    rand_scores = [];
    
    for i = 1:100
       rand_scores(i, :) = gmm();
    end
    rand_scores(rand_scores < 1)
    mean_rand_score = mean(rand_scores)

end


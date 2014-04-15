% function : evaluate_kmeans
%
% input    : none
%
% output   : none
%
% description : runs kmeans 100 times to get the mean rand index score
function [ ] = evaluate_kmeans( )

    rand_scores = [];
    
    for i = 1:100
       rand_scores(i, :) = kmeans();
    end
    
    mean_rand_score = mean(rand_scores)
end


% function : get_rand_score
%
% input    : assignment - nSamples * 1 vector, each row indicating the
%            assingment of the sample to the cluster index
function [ rand_score ] = get_rand_score( assignment )

    labels = load('test_data_labels.txt');
    matches = 0;
    n = size(assignment, 1);
    
    for i = 1:n
        for j = i+1: n
           
           if assignment(i) == assignment(j) && labels(i) == labels(j)
            matches = matches + 1;
           elseif assignment(i) ~= assignment(j) && labels(i) ~= labels(j)
            matches = matches + 1;
           end   
           
        end
    end
    
    rand_score = matches / nchoosek(n, 2);

end


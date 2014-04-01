function [ output_args ] = rbf_svm( input_args )
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here

% addpath to the libsvm toolbox
addpath('../libsvm-3.17/matlab');

% addpath to the data
dirData = '.';
addpath(dirData);

load('train79.mat');
X = d79;
Y = zeros(2000, 1);
Y(1:1000) = 7;
Y(1001:2000) = 9;

stepSize = 1;
c_exponentials = -20:stepSize:20;
g_exponentials = -20:stepSize:20;

len_c_exponentials = length(c_exponentials);
len_g_exponentials = length(g_exponentials);

cv_matrix = zeros(len_c_exponentials,len_g_exponentials);
bestcv = 0;
for i = 1:len_c_exponentials
    c_exp = c_exponentials(i);
    for j = 1:len_g_exponentials
        g_exp = g_exponentials(j);

        % -v 3 --> 3-fold cross validation
    
        param = ['-q -v 3 -c ', num2str(2^c_exp), ' -g ', num2str(2^g_exp)];
        cv = svmtrain(Y, X, param);
        cv_matrix(i,j) = cv;
    
        if (cv >= bestcv),
            bestcv = cv; bestLog2c = c_exp; bestLog2g = g_exp;
        end
    
        % fprintf('%g %g %g (best c=%g, g=%g, rate=%g)\n', c_exp, g_exp, cv, bestc, bestg, bestcv);
    end

end

cv_matrix

end


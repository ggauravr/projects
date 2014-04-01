load('train79.mat');
X = d79(1:10, 100:110)
X_test = d79(101:110, 100:110)
Y = repmat('7', 1, 10)
Y = transpose(Y);

SVMModel = fitcsvm(X, Y);
% [label, score] = predict(SVMModel, X_test)
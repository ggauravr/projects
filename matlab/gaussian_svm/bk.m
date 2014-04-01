% grid-search to find best C,sigma combination for SVM
    for i = 1:range(c_list)
        C = 10^c_list(i);

        for j = 1:range(sigma_list)
            sigma = 10^sigma_list(j);

            for k = 1:10
                i_test = (indices == k);
                i_train = ~i_test;

                transpose(i_train);

                x_train = X(i_train, :);
                y_train = Y(i_train, :);

                x_test = X(i_test, :);
                y_test = Y(i_test, :);

                SVMModel = svmtrain(x_train, y_train, 'kernel_function', 'rbf', 'rbf_sigma', sigma, 'boxconstraint', C);
                label = svmclassify(SVMModel, x_test);    

                CP = classperf(y_test, label);
                
                % result = [result; C sigma CP.CorrectRate];
                if CP.CorrectRate > best_result
                    best_result = CP.CorrectRate;
                    best_C = C;
                    best_sigma = sigma;
                end

            end

        end
    end
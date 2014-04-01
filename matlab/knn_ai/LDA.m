X = load('data/wine_train_x.txt');
Y = load('data/wine_train_y.txt');

xdata=load('data/wine_test_x.txt');
ydata=load('data/wine_test_y.txt');

Label= unique(Y);
noOfClasses= length(Label);

w = [];
bias= [];
Label
for i= 1:noOfClasses

    X_1= X(Y==Label(i),:);
    X_2= X(~(Y==Label(i)),:);
    u1= mean(X_1);
    u2= mean(X_2);
    
    s1= cov(X_1);
    s2= cov(X_2);
    

    sw= s1+s2;
    
   w(i,:)= inv(sw)*(u2-u1)';
   bias(i, :)= -0.5*w(i, :)*(u1+u2)';    
    
end

yValues=[];
for i= 1:size(xdata, 1)
    votes=[];
for j=1:noOfClasses
    vote= w(j,:)*xdata(i,:)' + bias(j,:);
    votes=[votes vote];
end
    votes;
    [maxVote index]= min(votes);
    
    yValues=[yValues; index-1];
end

yValues;
L=classperf(yValues, ydata);
L.ErrorRate;
L.DiagnosticTable
yValues;
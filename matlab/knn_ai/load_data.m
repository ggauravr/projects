 function [ x_train, y_train, x_test, y_test ] = load_data( )
  
  % load train data
  x_train = load('data/wine_train_x.txt');
  y_train = load('data/wine_train_y.txt');

  % load train data
  x_test = load('data/wine_test_x.txt');
  y_test = load('data/wine_test_y.txt');

end
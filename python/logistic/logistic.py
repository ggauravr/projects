import pandas as pd
import statsmodels.api as sm
import pylab as pl
import numpy as np

df = pd.read_csv('binary.csv')

print df.head()

df.columns = ['admit', 'gre', 'gpa', 'prestige']

dummy_ranks = pd.get_dummies(df['prestige'], prefix='prestige')

# print dummy_ranks.head()
cols_to_keep = ['admit', 'gpa', 'gre']

data = df[cols_to_keep].join(dummy_ranks.ix[:, 'prestige_2' :])

print data.head()

train_cols = data.columns[1:]

logit = sm.Logit(data['admit'], data[train_cols])

result = logit.fit()

print result.summary()


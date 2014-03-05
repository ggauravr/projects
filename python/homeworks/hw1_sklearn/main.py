import os
import sys
from bs4 import BeautifulSoup as Soup
from sklearn.feature_extraction.text import TfidfVectorizer
from nltk import stem
from sklearn.cluster import KMeans as KM
import scipy as sp

# get the directory name
# loop on all the files
    # read the content in <text> tag into an array

# pass the array to vectorizer to generate the vocabulary/tokens

stemmer = stem.SnowballStemmer('english')
class StemmedTfidfVectorizer(TfidfVectorizer):
    def build_analyzer(self):
        analyzer = super(TfidfVectorizer, self).build_analyzer()

        return lambda doc: (stemmer.stem(word) for word in analyzer(doc))


DIR = 'data'

files = [os.path.join(DIR, filename) for filename in os.listdir(DIR)]
documents = [Soup(open(filepath), from_encoding='utf-8').find_all('reuters') for filepath in files]
posts = [l_document.text for p_document in documents for l_document in p_document]


vectorizer = StemmedTfidfVectorizer(min_df=1, stop_words='english', decode_error='ignore')

features = vectorizer.fit_transform(posts)

new_post = '''
    Comissaria Smith said there is still some doubt as to how
much old crop cocoa is still available as harvesting has
practically come to an end. With total Bahia crop estimates
around 6.4 mln bags and sales standing at almost 6.2 mln there
are a few hundred thousand bags still in the hands of farmers,
middlemen, exporters and processors.
'''

new_post_2 = '''
Although BankAmerica has yet to specify the types of
equities it would offer, most analysts believed a convertible
preferred stock would encompass at least part of it.
    Such an offering at a depressed stock price would mean a
lower conversion price and more dilution to BankAmerica stock
holders, noted Daniel Williams, analyst with Sutro Group.
    Several analysts said that while they believe the Brazilian
debt problem will continue to hang over the banking industry
through the quarter, the initial shock reaction is likely to
ease over the coming weeks.
    Nevertheless, BankAmerica, which holds about 2.70 billion
dlrs in Brazilian loans, stands to lose 15-20 mln dlrs if the
interest rate is reduced on the debt, and as much as 200 mln
dlrs if Brazil pays no interest for a year, said Joseph
Arsenio, analyst with Birr, Wilson and Co.
    He noted, however, that any potential losses would not show
up in the current quarter.
'''

kmeans = KM(n_clusters=32, init='random', n_init=1, verbose=1)
kmeans.fit(features)

print kmeans.labels_

new_post_vector = vectorizer.transform([new_post_2])
new_post_label  = kmeans.predict(new_post_vector)[0]

print "new posts label", new_post_label

similar_indices = (kmeans.labels_ == new_post_label).nonzero()[0]

similar = []

for i in similar_indices:
    dist = sp.linalg.norm((new_post_vector - features[i]).toarray())

    similar.append((dist, posts[i]))

similar = sorted(similar)

print "number of data points/ documents in the cluster",len(similar)
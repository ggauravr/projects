from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk import stem
import re
import string

stopwordlist = stopwords.words('english')

def clean(text, stopwords, returnTokens=False):
	# text = remove_punctuation(text)
	
	text = filter(lambda x: x in string.printable, text)
	text = re.sub('[][0-9,.\'\"\\:;*+<>(){}/&-]+',' ', text)
	# text = re.sub(r'([^\w] | [0-9])+','  ', text)
	text = re.sub(r'\b\w\w?\b', '  ', text)
	text = re.sub(r'[\s]+', ' ', text).lower()

	if returnTokens:
		tokens = tokenize(text)
		return tokens
	else:
		return text

def tokenize(text):
	global stopwordlist
	tokens = word_tokenize(text)
	stemmer = stem.PorterStemmer()

	filtered_words = [stemmer.stem(word) for word in tokens if not word in stopwordlist]

	return filtered_words
            


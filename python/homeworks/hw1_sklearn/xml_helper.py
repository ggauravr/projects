from bs4 import BeautifulSoup

soup = 'Soup'

def get_all(tagname=None):
	
	global soup

	elements = None
	
	if tagname:
		elements = soup.find_all(tagname)

	return elements

def get(tagname=None):
	
	global soup

	element = None
	
	if tagname:
		element = soup.find(tagname)
	
	return element

def get_classes(document):

	topics_element_list	= document.topics.find_all('d')
	places_element_list  = document.places.find_all('d')

	topics = []
	places = []
	classes = set([])

	for topic in topics_element_list:
		topics.append(topic.text)

	# for place in places_element_list:
	# 	places.append(place.text)

	classes = set(topics) # | set(places)

	return classes

def initialize(filepath):

	global soup

	soup = BeautifulSoup(open(filepath), from_encoding='utf-8')
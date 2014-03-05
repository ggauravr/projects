from bs4 import BeautifulSoup as Soup
from urllib2 import urlopen
from urllib2 import Request
import re
import sys

BASE_URL = 'http://www.geeksforgeeks.org/';

def numberOfPages(content):
	lastPageLink = content.find('div', { 'class' : 'wp-pagenavi' }).find('a', { 'class' : 'last' })['href']

	nPages = re.match(r'.*page/([0-9]+).*', lastPageLink).group(1)

	return nPages

# def scrapePage():

# def findPatternInPage():

# def getContentFromPost():

# def saveContentToFile():

def makeSoup(url):
	request = Request(url, headers={'User-Agent' : 'Firefox'})
	html = urlopen(request).read()
	return Soup(html, 'lxml')


def main():
	mainContent = makeSoup(BASE_URL)

	# print mainContent.find('div', { 'class' : 'wp-pagenavi' }).find('a', { 'class' : 'last' })

	nPages = int(numberOfPages(mainContent))

	regexString = r'.*'
	filename = ''
	for arg in sys.argv[1:]:
		filename += arg
		regexString += arg+'.*'

	filename += '-interview-questions.txt'
	print regexString

	# return
	requiredLinks = []

	for index in range(nPages):
		# for every page till the last page
		url = BASE_URL+'page/'+str(index+1)+'/'
		print url

		pageSoup = makeSoup(url)

		# print type(pageSoup)

		# get all title from page
		titles = pageSoup.find_all('h2', { 'class' : 'post-title'} )

		# print titles

		links = [title.find('a')['href'] for title in titles if re.match(regexString, title.find('a')['href'])]

		if(len(links)):
			for url in links:
				requiredLinks.append(url)
				print url

	contents = []
	i = 0
	for link in requiredLinks:
		pageSoup = makeSoup(link)

		contents.append(pageSoup.find('h2', { 'class' : 'post-title' }).text)
		contents[i] += pageSoup.find('div', { 'class' : 'post-content' }).text
		i += 1

	file_h = open(filename, 'w')
	for content in contents:
		file_h.write(content.encode('utf-8'))
		# print content
	file_h.close()

main()
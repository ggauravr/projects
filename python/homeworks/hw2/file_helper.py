import os

def get_list_of_files(directory_name):
	
	return os.listdir(directory_name)

def get_filepath(filename, directory_name=None):

	filepath = filename

	if directory_name:
		filepath = os.path.join(directory_name, filename)

	return filepath
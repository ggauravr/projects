import file_helper
import xml_helper

config_file = file_helper.get_filepath('config.xml')

xml_helper.initialize(config_file)

def get(tag=None):
	if tag != None:
		return xml_helper.get(tag)

def get_text(tag=None):
	if tag != None:
		return xml_helper.get(tag).text
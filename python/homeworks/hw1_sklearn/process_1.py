
#import xml.etree.cElementTree as ET
import os
from lxml import etree as ET

dirname = 'dataset'

filenumber = 1

for filename in os.listdir(dirname):
    filepath = os.path.join(dirname, filename)

    # fileobject = open(filepath, encoding='utf-8', errors='replace')
    
    print "File Number", filenumber
    # print 'File Path', filepath
    
    filenumber = filenumber + 1

    articlecount = 1

    parser = ET.XMLParser(encoding='utf-8', recover=True)

    tree = ET.parse(filepath, parser)

    root = tree.getroot()

    print 'Sibling of Root Element: ',ET.tostring(tree, pretty_print=True)

    for child in root:
        print 'Child Tag', child.tag, child.attrib

from __future__ import division
import text_helper
import file_helper
import xml_helper
import config_helper
import graph_helper
import time

doc_term_frequency_class_map    = {}      # type : doc_id : [ [<term_id, frequency>], [ class_id ] ]
term_index_frequency_map   = {}             # type : term : [term_id, doc_count] 
class_index_map      = {}                           # type : class_name : class_id
inverted_tree_map   = {}

# parameters and default values
directory_name  = config_helper.get_text('data_directory')
title_weight        = config_helper.get_text('title_weight')
stopwords          = config_helper.get_text('stopwords')

file_term_master = config_helper.get_text('term_master')
file_class_master = config_helper.get_text('class_master')
file_doc_term_map  = config_helper.get_text('doc_term_map')
file_term_doc_map = config_helper.get_text('term_doc_map')
file_bounded_doc_term_map  = 'bounded'+config_helper.get_text('doc_term_map')
file_bounded_term_doc_map = 'bounded'+config_helper.get_text('term_doc_map')
consider_threshold = bool(config_helper.get_text('consider'))

if consider_threshold:
    upper_threshold = float(config_helper.get_text('upper_bound'))
    lower_threshold  = float(config_helper.get_text('lower_bound'))


file_list = file_helper.get_list_of_files(directory_name)

doc_id      = 1
class_id    = 1
term_id    = 1

start_time = time.time()
print 'Starting Document Processing\n'

doc_count_export = 1;
                
for filename in file_list:
    filepath = file_helper.get_filepath(filename, directory_name)

    xml_helper.initialize(filepath)
    document_list = xml_helper.get_all('reuters')

    for document in document_list:

            total_term_count = 0

            if doc_id % 100 == 0:
                print 'processed',doc_id,' documents\n'

            doc_term_frequency_class_map[doc_id] = {}

            # one indices contain class list, zero indices contain terms list
            doc_term_frequency_class_map[doc_id]['terms'] = {}
            doc_term_frequency_class_map[doc_id]['term_count'] = None
            doc_term_frequency_class_map[doc_id]['classes'] = []

            # important.. some documents don't contain title
            if document.title:
                title = document.title.text
            else:
                title = ''

            text = document.text
            classes = xml_helper.get_classes(document)

            title_tokens = text_helper.clean(title, stopwords, True)
            text_tokens = text_helper.clean(text, stopwords, True)
            tokens = title_tokens + text_tokens
            
            # prepare class_index map
            for class_item in classes:
                if class_item not in class_index_map:
                    class_index_map[class_item] = class_id
                    class_id += 1
                doc_term_frequency_class_map[doc_id]['classes'].append(class_index_map[class_item])

            # set of unique tokens from title and text
            token_set = set(title_tokens) | set(text_tokens)

            for term in token_set:
                # assign weights to titular words, according to the parameter supplied by the user
                if term in title_tokens:
                    term_frequency = title_weight*title_tokens.count(term)
                else:
                    term_frequency = text_tokens.count(term)

                term_frequency = int(term_frequency)

                total_term_count += term_frequency
                
                # prepare the term_id_frequency map
                if term not in term_index_frequency_map:
                    term_index_frequency_map[term] = [term_id, 1]
                    inverted_tree_map[term_id] = {}
                    inverted_tree_map[term_id]['total_count'] = 1
                    inverted_tree_map[term_id]['docs'] = {}
                    inverted_tree_map[term_id]['docs'][doc_id] = term_frequency
                    term_id += 1
                else:
                    term_index_frequency_map[term][1] += 1
                    inverted_tree_map[term_index_frequency_map[term][0]]['total_count'] +=1
                    inverted_tree_map[term_index_frequency_map[term][0]]['docs'][doc_id] = term_frequency

                term_index = term_index_frequency_map[term][0]
                # prepare the doc_term map
                
                doc_term_frequency_class_map[doc_id]['terms'][term_index] = term_frequency
                doc_term_frequency_class_map[doc_id]['term_count'] = total_term_count

            doc_id += 1

term_count = term_id -1
doc_count = doc_id -1

end_time = time.time()
print 'Creating Output Files\n'

# write the term, doc frequency map to file
term_id_frequency_file = open(file_term_master, 'w')
for term in sorted(term_index_frequency_map.keys()):
    term_id_frequency_file.write(term.encode('utf-8')+'#'+str(term_index_frequency_map[term][0])+'#'+str(term_index_frequency_map[term][1])+'\n')

# write the doc term frequency map to file
# write the class term_id map to file
fileh_class_master = open(file_class_master, 'w')
for class_name in sorted(class_index_map.keys()):
    fileh_class_master.write(class_name+','+str(class_index_map[class_name])+'\n')

fileh_doc_term_map = open(file_doc_term_map, 'w')
for doc_id, value in doc_term_frequency_class_map.items():
    # temp_classes = '$'.join(map(str, value['classes']))
    #print value['classes']
    fileh_doc_term_map.write(str(doc_id)+'#'+str(value['term_count'])+'#'+str(value['classes'])+'#'+str(value['terms']) +'\n')
    # print doc_id, value,

yValues = [0]
fileh_term_doc_map = open(file_term_doc_map, 'w')
for term_id in sorted(inverted_tree_map.keys()):
    item = inverted_tree_map[term_id]
    fileh_term_doc_map.write(str(term_id)+'#'+str(item['total_count'])+'#'+str(item['docs'])+'\n')
    yValues.append(item['total_count']/doc_count)

if consider_threshold :
    removed_term_ids = [term_id for term_id in range(1, term_count+1) if yValues[term_id] > upper_threshold or yValues[term_id] < lower_threshold ]
    for id in removed_term_ids:
        del inverted_tree_map[id]

    for doc_id in doc_term_frequency_class_map:
        doc = doc_term_frequency_class_map[doc_id]
        for term_id in doc['terms'].keys():
            if term_id in removed_term_ids:
                del doc['terms'][term_id]

    fileh_bounded_doc_term_map = open(file_bounded_doc_term_map, 'w')
    for doc_id, value in doc_term_frequency_class_map.items():
        fileh_bounded_doc_term_map.write(str(doc_id)+','+str(value['terms'])+','+str(value['classes'])+'\n')

#    boundedYValues = []
#    boundedXValues = []

    fileh_bounded_term_doc_map = open(file_bounded_term_doc_map, 'w')
    for term_id in sorted(inverted_tree_map.keys()):
        fileh_bounded_term_doc_map.write(str(term_id)+str(inverted_tree_map[term_id])+'\n')
 #       boundedXValues.append(term_id)
  #      boundedYValues.append(inverted_tree_map[term_id]['total_count']/doc_count)

print 'Processing Completed in ',end_time-start_time,'s \n Plotting Graph Now\n'

#graph_helper.plot_line({ 'original' : { 'yValues' : yValues, 'yLim' : [0,0.6], 'xValues' : False } }, consider_threshold, { 'bounded' : { 'yValues' : boundedYValues, 'yLim' : [0,0.6], 'xValues' : boundedXValues }} )

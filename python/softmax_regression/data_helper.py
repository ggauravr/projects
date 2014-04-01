import struct
import numpy
import array

###########################################################################################
""" Loads the images from the provided file name """

def loadMNISTImages(file_name):

    """ Open the file """

    data_file = open(file_name, 'rb')
    
    """ Read header information from the file """
    """ magic-number, n_samples, n_rows, n_cols - all 4 byte integers """
    
    magic_number = data_file.read(4)
    n_samples = data_file.read(4)
    n_rows = data_file.read(4)
    n_cols = data_file.read(4)
    
    """ Format the header information for useful data    """
    
    n_samples   = struct.unpack('>I', n_samples)[0]
    n_rows  = struct.unpack('>I', n_rows)[0]
    n_cols  = struct.unpack('>I', n_cols)[0]
    
    """ Initialize dataset as array of zeros """
    
    # zeros : @params - (rows, columns)
    dataset = numpy.zeros((n_rows*n_cols, n_samples))
    
    """ Read the actual image data """
    
    # array : @params - char_type, data
    # this will return a 1-D array of n_rows*n_cols*n_samples size
    images_raw  = array.array('B', data_file.read())
    data_file.close()
    
    """ Arrange the data in columns, i.e samples as column vectors """
    
    for i in range(n_samples):
    
        from_index = n_rows * n_cols * i
        to_index = n_rows * n_cols * (i + 1)
        
        # 28*28 elements, i.e data of one image, will be filled in at the i-th column
        dataset[:, i] = images_raw[from_index : to_index]
    
    """ Normalize and return the dataset """
            
    return dataset / 255

###########################################################################################
""" Loads the image labels from the provided file name """
    
def loadMNISTLabels(file_name):

    """ Open the file """

    label_file = open(file_name, 'rb')
    
    """ Read header information from the file """
    
    magic_number = label_file.read(4)
    n_samples = label_file.read(4)
    
    """ Format the header information for useful data """
    
    n_samples = struct.unpack('>I', n_samples)[0]
    
    """ Initialize data labels as array of zeros """
    
    labels = numpy.zeros((n_samples, 1), dtype = numpy.int)
    
    """ Read the label data """
    
    labels_raw = array.array('b', label_file.read())
    label_file.close()
    
    """ Copy and return the label data """
    
    labels[:, 0] = labels_raw[:]
    
    return labels
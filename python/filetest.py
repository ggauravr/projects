import os

def walk_directory(dirname):
    for filename in os.listdir(dirname):

        print filename, dirname

        path = os.path.join(dirname, filename)

        if os.path.isdir(path):
            print 'Directory', path
        else:
            print 'Normal file', path


walk_directory('/home/gauravr/projects/python')

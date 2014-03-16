from __future__ import division
import copy
import numpy as np
import matplotlib.pyplot as plt
from random import randint

# number of nodes
n = 4

# number of states
N = pow(2, n)

# adjacency list, to determine the markov blanket
# list of lists
adjacency_list = [[] for __ in range(n)]

# stores the count of states seen after sampling
state_probabilities = [0] * N

# initialize current state of random variables to 0000
# the states can go from 0000 to 1111, represented in integers as 0 - 15
cur_state = 0

# number of iterations
T = 1

# joint probability table
jpt = []

# partition_function
Z = 0

def loadFromFile(filename):
  jpt = []
  
  with open(filename) as fh:
    for line in fh:
      jpt.append(int(line))

  # print jpt

  return jpt

def loadAdjacencyList(filename):
  global adjacency_list

  with open(filename) as fh:
    for line in fh:
      nodes = map(int, line.split(' '))

      src = nodes[0]
      neighbors = nodes[1:]

      adjacency_list[src] = neighbors

  # print adjacency_list

def get_MB(node):
  global adjacency_list

  # return the list of neighbors
  return copy.copy(adjacency_list[node])

def getStateOf(nodes, isNumerator):
  global cur_state

  comparator = 0
  mask = 0

  if(isNumerator):
    pos = nodes[0]
    mb = nodes[1:]

    # number of shits to the right
    # higher the node number, lower its significant position
    shifts = n - pos -1
    
    bit_at_pos = cur_state >> shifts & 1
    
    temp_comparator = bit_at_pos << shifts
    comparator |= temp_comparator

    temp_mask = 1 << shifts
    mask |= temp_mask

  else:
    mb = nodes

  # each node actually represents a position, i.e 0 to 3, in this case
  for pos in mb:
    shifts = n - pos -1

    bit_at_pos = cur_state >> shifts & 1

    temp_comparator = bit_at_pos << shifts
    comparator |= temp_comparator

    temp_mask = 1 << shifts
    mask |= temp_mask

  return mask, comparator

def getEntryFromJPT(mask, comparator):
  global jpt

  isSame = True
  entry = 0

  # compare if the bits at positions set in the mask are same in comparator and i
  for i in range(N):

    # if the masked value for i and comparator are same, add the entry at i
    if( (i & mask) == (comparator & mask) ):
      entry += jpt[i]

  # print "entry: ", entry
  return entry

def setState(node, bit):
  global cur_state

  shifts = n-node-1

  if(bit == 0):
    # use bitwise &
    cur_state = cur_state & ~(1 << shifts)
  else:
    # use bitwise |
    cur_state = cur_state | (1 << shifts)

def main():
  global jpt, cur_state, adjacency_list, state_probabilities, Z

  # joint probability table(jpt)
  jpt = loadFromFile('data.txt')
  loadAdjacencyList('adjacency.txt')
  Z = sum(jpt)
  # print "Z: ", Z

  T_list = [1, 10, 100, 1000, 10000, 11000, 12000, 13000, 14000, 15000, 20000]
  error_list = []

  for T in T_list:
    cur_state = 0 # randint(0, N-1)
    state_probabilities = [0] * N
    for t in range(1, T+1):

      # looping over all random variables, 0 to n-1
      for z in range(n):
        
        # get the markov blanket of current random variable, z
        mb = get_MB(z)
        # print "node, MB(node): ", z, mb

        # order is important because mb is changed after insert/prepend
        d_mask, d_comparator = getStateOf(mb, False)
        mb.insert(0, z)
        n_mask, n_comparator = getStateOf(mb, True)

        numerator = getEntryFromJPT(n_mask, n_comparator)
        denominator = getEntryFromJPT(d_mask, d_comparator)

        probability = numerator / denominator

        # print "cur_state: ", cur_state
        setState(z, np.random.choice(2, p=[probability, 1-probability]))
        # print "cur_state, after: ", cur_state

      # increase of counter of the new state/ sampled
      state_probabilities[cur_state] += 1

    sum_entries = sum(state_probabilities)
    print "num_entries: ", sum_entries
    state_probabilities = [ x/sum_entries for x in state_probabilities ]
    # for i in range(len(state_probabilities)):
    #   state_probabilities[i] = state_probabilities[i]/T
    # print state_probabilities

    print "T, Z: ", T, Z

    if(T == 10000):
      print "state_probabilities: ", state_probabilities
      print "true probabilities: ", [x/Z for x in jpt]

    error = 0
    for i in range(N):
      error += abs(state_probabilities[i] - (jpt[i]/Z))

    error_list.append(error)
    print "T, error: ", T, error
  plt.plot(T_list, error_list)
  # plt.show()


if __name__  == '__main__':
  main()

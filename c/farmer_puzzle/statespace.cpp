#include <iostream>
#include <cstdarg>
#include "prototypes.h"

using namespace std;

StateSpace::StateSpace(int nStates): _size(nStates), _originState(1), _goalState(15)/*, _queue(nStates)*/ {
	_states = new State * [nStates+1];

	_states[1] = new State(1, 0, 0);
	_states[1]->setNeighbors(4, 2,3,4,5);
}
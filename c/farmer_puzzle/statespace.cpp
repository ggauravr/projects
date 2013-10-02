#include <iostream>
#include <cstdarg>
#include "prototypes.h"

using namespace std;

StateSpace::StateSpace(int nStates): _size(nStates), _originState(1), _goalState(15), _queue(nStates) {
	_states = new State * [nStates+1];

	_states[1] = new State(1, "fwsc|----");
	_states[1]->setNeighbors(2, 3,5);

	_states[2] = new State(2, "-ws-|f--c");
	_states[2]->setNeighbors(2, 3,5);

	_states[3] = new State(3, "-w-c|f-s-");
	_states[3]->setNeighbors(2, 1,7);

	_states[4] = new State(4, "--sc|fw--");
	_states[4]->setNeighbors(2, 3,5);

	_states[5] = new State(5, "-wsc|f---");
	_states[5]->setNeighbors(1, 1);

	_states[6] = new State(6, "fws-|---c");
	_states[6]->setNeighbors(2, 9, 10);

	_states[7] = new State(7, "fw-c|--s-");
	_states[7]->setNeighbors(3, 3, 9, 11);

	_states[8] = new State(8, "f-sc|-w--");
	_states[8]->setNeighbors(2, 10, 11);

	_states[9] = new State(9, "-w-|f-sc");
	_states[9]->setNeighbors(1, 6);

	_states[10] = new State(10, "--s-|fw-c");
	_states[10]->setNeighbors(2, 8, 13);

	_states[11] = new State(11, "---c|fws-");
	_states[11]->setNeighbors(1, 7);

	_states[12] = new State(12, "fw--|--sc");
	_states[12]->setNeighbors(2, 3,5);

	_states[13] = new State(13, "f-s-|-w-c");
	_states[13]->setNeighbors(2, 10, 15);

	_states[14] = new State(14, "f--c|-ws-");
	_states[14]->setNeighbors(2, 3,5);

	_states[15] = new State(15, "----|fwsc");
	_states[15]->setNeighbors(2, 13, 16);

	_states[16] = new State(16, "f---|-wsc");
	_states[16]->setNeighbors(1, 15);

}

void StateSpace::printStateSpace(){

	for(int i=1; i <= _size ; ++i){
		cout << "State " << _states[i]->getID() << " its neighbors " << endl;
		int * neighbors = _states[i]->getNeighbors();

		for(int j=0, length = sizeof(neighbors) / sizeof(int); j < length ; j++){
			cout << "Neighbor Node " << neighbors[j] << ", ";
		}

		cout << endl;
	}

}

void StateSpace::startSearch(){

	State * currentState;
	bool isGoal = false;
	int * neighbors;

	_queue.insert(_states[_originState]);
	
	// printStateSpace();

	while(!_queue.isEmpty()){
		currentState = _queue.remove();
		currentState->setStatus(true);

		if(currentState->getID() == _goalState){
			isGoal = true;
			break;
		}

		cout << "Current Node Expanding .. " << currentState->getID() << endl;

		neighbors = currentState->getNeighbors();

		for(int i=0, length = currentState->getNeighborsLength(); i < length ; ++i){
			// if the neighbor is not already in the queue, add it
			// cout << "State ,.. Neighbors length " << currentState->getID() << ".. " << length << endl;
			if( !_states[neighbors[i]]->isVisited() ){
				_queue.insert(_states[neighbors[i]]);
				_states[neighbors[i]]->setStatus(true);
				_states[neighbors[i]]->setParent(currentState->getID());
			}
		}
	}

	if(isGoal){
		cout << "\n\nGoal Reached ! "<< endl;

		printPath(_goalState);
	}
	else if(_queue.isEmpty()){
		cout << "Goal could not be reached.." << endl;
	}

}

void StateSpace::printPath(int state){

	if(state == 0){
		cout << "\nPrinting optimal path found " << endl;
		return;
	}

	printPath(_states[state]->getParent());

	cout << "State " << _states[state]->getRepresentation() << endl;

}
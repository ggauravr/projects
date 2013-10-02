#include <iostream>
#include <cstdarg>
#include <string>
#include "prototypes.h"

using namespace std;

StateSpace::StateSpace(int nStates, string algorithm): _size(nStates), _originState(1), _goalState(15), _queue(nStates) {

	int * hcost_array = new int[nStates+1];

	if(algorithm == "bfs"){
		// assign h_cost all to zero
		for(int i =1; i <= nStates ; ++i){
			hcost_array[i] = 0;
		}
	}
	else if(algorithm == "astar"){
		hcost_array[1] = 5;
		hcost_array[2] = 4;
		hcost_array[3] = 4;
		hcost_array[4] = 4;
		hcost_array[5] = 6;
		hcost_array[6] = 3;
		hcost_array[7] = 3;
		hcost_array[8] = 3;
		hcost_array[9] = 2;
		hcost_array[10] = 2;
		hcost_array[11] = 2;
		hcost_array[12] = 1;		
		hcost_array[13] = 1;		
		hcost_array[14] =1;		
		hcost_array[15] = 0;		
		hcost_array[16] = 1;		
	}

	_states = new State * [nStates+1];

	_states[1] = new State(1, "fwsc|----", hcost_array[1]);
	_states[1]->setNeighbors(2, 3,5);
	_states[_originState]->setCost(0);

	_states[2] = new State(2, "-ws-|f--c", hcost_array[2]);
	_states[2]->setNeighbors(2, 3,5);

	_states[3] = new State(3, "-w-c|f-s-", hcost_array[3]);
	_states[3]->setNeighbors(2, 1,7);

	_states[4] = new State(4, "--sc|fw--", hcost_array[4]);
	_states[4]->setNeighbors(2, 3,5);

	_states[5] = new State(5, "-wsc|f---", hcost_array[5]);
	_states[5]->setNeighbors(1, 1);

	_states[6] = new State(6, "fws-|---c", hcost_array[6]);
	_states[6]->setNeighbors(2, 9, 10);

	_states[7] = new State(7, "fw-c|--s-", hcost_array[7]);
	_states[7]->setNeighbors(3, 3, 9, 11);

	_states[8] = new State(8, "f-sc|-w--", hcost_array[8]);
	_states[8]->setNeighbors(2, 10, 11);

	_states[9] = new State(9, "-w-|f-sc", hcost_array[9]);
	_states[9]->setNeighbors(1, 6);

	_states[10] = new State(10, "--s-|fw-c", hcost_array[10]);
	_states[10]->setNeighbors(2, 8, 13);

	_states[11] = new State(11, "---c|fws-", hcost_array[11]);
	_states[11]->setNeighbors(1, 7);

	_states[12] = new State(12, "fw--|--sc", hcost_array[12]);
	_states[12]->setNeighbors(2, 3,5);

	_states[13] = new State(13, "f-s-|-w-c", hcost_array[13]);
	_states[13]->setNeighbors(2, 10, 15);

	_states[14] = new State(14, "f--c|-ws-", hcost_array[14]);
	_states[14]->setNeighbors(2, 3,5);

	_states[15] = new State(15, "----|fwsc", hcost_array[15]);
	_states[15]->setNeighbors(2, 13, 16);

	_states[16] = new State(16, "f---|-wsc", hcost_array[16]);
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

		cout << "Current Node Expanding .. and its path cost .." << currentState->getID() << ".. " << currentState->getCost()+currentState->getHCost() << endl;

		neighbors = currentState->getNeighbors();

		for(int i=0, length = currentState->getNeighborsLength(); i < length ; ++i){
			// if the neighbor is not already in the queue, add it
			// cout << "State ,.. Neighbors length " << currentState->getID() << ".. " << length << endl;
			if( !_states[neighbors[i]]->isVisited() ){

				_states[neighbors[i]]->setCost( currentState->getCost() + _states[neighbors[i]]->getCost() );
				_queue.insert(_states[neighbors[i]]);
				_states[neighbors[i]]->setStatus(true);
				_states[neighbors[i]]->setParent(currentState->getID());
			}
		}

		_queue.heapify();
		// _queue.printQueue();
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
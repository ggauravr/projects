#include <iostream>
#include "prototypes.h"

using namespace std;

StateSpace::StateSpace(int nStates): originState(1), goalState(15), nStates(nStates), queue(nStates){

	states = new State[nStates+1]; // start index of state from 1, so n+1

	states[1] 	= new State(1, 0);
	states[1].setNeighbors(4, 2,3,4,5);
	// states[2] 	= new State(2, {1,6}, INFINITY);
	// states[3] 	= new State(3, {1,7}, 1);
	// states[4] 	= new State(4, {3,8}, INFINITY);
	// states[5] 	= new State(5, {1}, 2);
	// states[6] 	= new State(6, {2,9, 10}, 1);
	// states[7] 	= new State(7, {3, 9, 11}, 1);
	// states[8] 	= new State(8, {4, 10, 11}, 1);
	// states[9] 	= new State(9, {6, 12}, 1);
	// states[10] 	= new State(10, {6, 8, 13}, 1);
	// states[11] 	= new State(11, {7, 14}, 1);
	// states[12] 	= new State(12, {9, 15}, INFINITY);
	// states[13] 	= new State(13, {10, 15}, 1);
	// states[14] 	= new State(14, {11, 15}, INFINITY);
	// states[15] 	= new State(15, {12, 13, 14, 16}, 1);
	// states[16] 	= new State(16, {15}, 2);

	queue.insert(states[1]);
}

void StateSpace::startSearch(){

	int currentState = queue.remove().getID(),
		neighbor;
	bool isGoal = currentState == goalState ? true : false;
	int * neighbors;


	while(! isGoal && ! queue.isEmpty() ){

		// mark the node as visited
		states[currentState].setStatus(true);

		neighbors = states[currentState].getNeighbors();

		for(int i=0, length = sizeof(neighbors) / sizeof(int); i < length; i++){
			neighbor = neighbors[i];

			if(!states[neighbor].isVisited()){
				// path cost of current node = path cost of parent node + state cost of current node
				states[neighbor].setCost( states[currentState].getCost() + states[neighbor].getCost() );
				states[neighbor].setParent(currentState);
				queue.insert(states[neighbor]);
			}
		}

		currentState = queue.remove().getID();
		isGoal = currentState == goalState ? true : false;
	}

	states[currentState].setStatus(true);

	if(isGoal){
		cout << "Path to GOAL in reverse order .. " << endl;
		while(currentState != 0){
			cout << currentState << ", ";
			currentState = states[currentState].getParent();
		}

		cout << endl;
	}
	else{
		cout << "GOAL could not be reached.." << endl;
	}
}
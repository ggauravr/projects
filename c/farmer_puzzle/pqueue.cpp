#include <iostream>
#include "prototypes.h"

PQueue::PQueue(int nStates): currentIndex(1), size(0) {
	states = new State[nStates+1];
}

bool PQueue::isEmpty(){
	return size == 0;
}

void PQueue::insert(State state){

	int bestPosition, leftChildPos;
	State tempState;

	// insert a node in the end.. heapify.. increment the size
	states[currentIndex] = state;

	for(int i = (size-currentIndex)/2; i <= currentIndex; i = i/2){

		leftChildPos = 2*i; // right child will be at 2*i +1
		bestPosition = leftChildPos;

		if( leftChildPos+1 <= size ){
			if(states[leftChildPos].getCost() < states[leftChildPos+1].getCost()){
				bestPosition = leftChildPos;
			}
			else{
				bestPosition = leftChildPos + 1;
			}
		}

		// parent positon after this will have the least cost states
		if(states[bestPosition].getCost() < states[i].getCost()){
			// swap child and parent
			tempState = states[bestPosition];
			states[bestPosition] = states[i];
			states[i] = tempState;
		}

	}

	++size;
}

State PQueue::remove(){

	// return state with the lowest path cost.. increment currentIndex
	return states[currentIndex++];
}
#include <iostream>
#include <cstdarg>
#include "prototypes.h"

using namespace std;

PQueue::PQueue(int n): _size(0), _currentIndex(0){
	_states = new State * [n];
}

void PQueue::insert(State * newState){

	int parentPos;
	State * tempState;

	_states[_size++] = newState;

	// cout << "new state " << _states[0]->getID();

	// printQueue();

	// insert the state.. heapify.. increment size
	for(int i = _size-1; i > _currentIndex; i = i/2){
		// cout << " int loop " << endl;
		parentPos = i/2;
		// compare with its parent, if not in order, push up
		if(_states[i]->getCost() < _states[parentPos]->getCost()){
			// swap child and parent
			tempState = _states[i];
			_states[i]   = _states[parentPos];
			_states[parentPos] = tempState;
		}

	}

}

void PQueue::printQueue(){
	cout << "Queue contents " << endl;

	for(int i= _currentIndex; i < _size; ++i){
		cout << "States in Queue are " << _states[i]->getID() << endl;
	}
}

bool PQueue::isEmpty(){
	return _size == 0;
}

State * PQueue::remove(){

	return _states[_currentIndex++];
}


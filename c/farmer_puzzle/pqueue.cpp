#include <iostream>
#include <cstdarg>
#include "prototypes.h"

using namespace std;

PQueue::PQueue(int n): _size(0), _currentIndex(0){
	_states = new State * [n];
}

void PQueue::insert(State * newState){

	_states[_currentIndex + _size++] = newState;

	// cout << "new state " << _states[0]->getID();

	// printQueue();

	// insert the state.. heapify.. increment size

}

void PQueue::heapify(){

	int parentPos;
	State * tempState;

	for(int i = _currentIndex+_size-1; i > _currentIndex; i = i/2){
		// cout << " int loop " << endl;
		parentPos = i/2 < _currentIndex ? _currentIndex : i/2;
		
		// compare with its parent, if not in order, push up
		if(_states[i]->getCost() + _states[i]->getHCost() < _states[parentPos]->getCost() + _states[parentPos]->getHCost()){
			// cout << "swapping child " << _states[i]->getID() << " with cost " << _states[i]->getCost() + _states[i]->getHCost() << " with parent " << _states[parentPos]->getID() << " with cost " << _states[parentPos]->getCost() + _states[parentPos]->getHCost() << endl;
			// swap child and parent
			tempState = _states[i];
			_states[i]   = _states[parentPos];
			_states[parentPos] = tempState;
		}

	}
}

void PQueue::printQueue(){
	cout << "Queue contents " << endl;

	for(int i= _currentIndex; i < _currentIndex+_size; ++i){
		cout << "state .. " << _states[i]->getID() << " cost(path+heuristic) .. " << _states[i]->getCost()+_states[i]->getHCost() << endl;
	}
}

bool PQueue::isEmpty(){
	return _size == 0;
}

State * PQueue::remove(){
	// cout << "size " << _size << "currentIndex " << _currentIndex << endl;
	_size--;
	return _states[_currentIndex++];
}


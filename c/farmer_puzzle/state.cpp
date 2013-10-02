#include <iostream>
#include <cstdarg>
#include "prototypes.h"

using namespace std;
State::State(): _id(0), _parent(0), _cost(0), _visited(false) {}
State::State(int id): _id(id), _parent(0), _cost(0), _visited(false) {}

State::State(State & state){
	_id 			= state.getID();
	_parent 	= state.getParent();
	_cost 		= state.getCost();
	_neighbors	= state.getNeighbors();
}

int State::getID(){
	return _id;
}

int State::getParent(){
	return _parent;
}

int State::getCost(){
	return _cost;
}

int State::getNeighborsLength(){
	return _nNeighbors;
}

int * State::getNeighbors(){
	return _neighbors;
}

bool State::isVisited(){
	return _visited;
}

void State::setNeighbors(int n, ...){

	_neighbors = new int[n];

	_nNeighbors = n;

	va_list arguments;
	va_start(arguments, n);

	for(int i=0; i < n; ++i){

		_neighbors[i] = va_arg(arguments, int);
		
	}
}

void State::setStatus(bool status){
	_visited = status;
}

void State::setParent(int parent){
	_parent = parent;
}
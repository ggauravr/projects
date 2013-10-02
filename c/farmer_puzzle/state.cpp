#include <iostream>
#include <cstdarg>
#include "prototypes.h"

using namespace std;

State::State(int id, int parent, int cost): _id(id), _parent(parent), _cost(cost), _visited(false) {}

int State::getID(){
	return _id;
}

int State::getParent(){
	return _parent;
}

int State::getCost(){
	return _cost;
}

bool State::isVisited(){
	return _visited;
}

void State::setNeighbors(int n, ...){

	_neighbors = new int[n];

	va_list arguments;
	va_start(arguments, n);

	for(int i=0; i < n; ++i){
		_neighbors[i] = va_arg(arguments, int);
	}
}
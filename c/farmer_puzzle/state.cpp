#include <iostream>
#include <cstdarg>
#include "prototypes.h"

State::State(): id(0), cost(0), parentState(0), visited(false), neighbors(NULL) {}

State::State(int id, int cost): id(id), visited(false), parentState(0), cost(cost) {}

int State::getID(){
	return id;
}

bool State::isVisited(){

	return visited;
}

int State::getCost(){
	return cost;
}

int State::getParent(){
	return parentState;
}

int * State::getNeighbors(){

	return neighbors;
}

void State::setStatus(bool status){
	visited = status;
}

void State::setCost(int cost){
	this->cost = cost;
}

void State::setParent(int parent){
	parentState = parent;
}

void setNeighbors(int n, ...){
	
	va_list arguments;

	va_start(arguments, n);

	neighbors = new int[n];

	for(int i=0; i < n; i++){
		neighbors[i] = va_arg(arguments, int);
	}

	cout << "Neighbors for State " << id << " are " << neighbors << endl;

}
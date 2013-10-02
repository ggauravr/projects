#include <iostream>
#include <string>
#include <cstdarg>

using namespace std;

class State{

	int 	_id,
		_parent,
		_cost,
		_nNeighbors,
		// heuristic cost
		_h_cost;

	string _figure;

	bool _visited;

	int * _neighbors;

	public:

		State();
		State(int id, string representation, int h_cost);
		State(State&);

		int getID();
		int getParent();
		int getCost();
		int getNeighborsLength();
		bool isVisited();
		int * getNeighbors();
		string getRepresentation();
		int getHCost();

		// void setID(int);
		void setParent(int);
		void setCost(int);
		void setStatus(bool);
		void setNeighbors(int n, ...);
};

class PQueue{

	State **_states;
	int 	_currentIndex,
		_size;

	public:
		PQueue(int);
		void insert(State *);
		State * remove();
		bool isEmpty();
		void printQueue();

		int getCurrentIndex();
		int getSize();
};

class StateSpace{

	State ** _states;
	PQueue _queue;
	int 	_size,
		_originState,
		_goalState;

	public:
		StateSpace(int, string);
		void startSearch();
		void printStateSpace();
		void printPath(int);
		// int getOriginState();
		// int getGoalState();
		// int getSize();
};
#include <iostream>
#include <cstdarg>

class State{

	int 	_id,
		_parent,
		_cost,
		_nNeighbors;

	bool _visited;

	int * _neighbors;

	public:

		State();
		State(int id);
		State(State&);

		int getID();
		int getParent();
		int getCost();
		int getNeighborsLength();
		bool isVisited();
		int * getNeighbors();

		// void setID(int);
		void setParent(int);
		// void setCost(int);
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
		StateSpace(int);
		void startSearch();
		void printStateSpace();
		// int getOriginState();
		// int getGoalState();
		// int getSize();
};
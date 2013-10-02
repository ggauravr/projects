#include <iostream>
#include <cstdarg>

class State{

	int 	_id,
		_parent,
		_cost;

	bool _visited;

	int * _neighbors;

	public:

		State();
		State(int id, int parent, int cost);

		int getID();
		int getParent();
		int getCost();
		bool isVisited();
		// int * getNeighbors();

		// void setID(int);
		// void setParent(int);
		// void setCost(int);
		// void setStatus(bool);
		void setNeighbors(int n, ...);
};

// class PQueue{

// 	States * _queue;
// 	int 	_currentIndex,
// 		_size;

// 	public:
// 		PQueue(int);
// 		void insert(State *);
// 		State remove();

// 		int getCurrentIndex();
// 		int getSize();
// };

class StateSpace{

	State ** _states;
	// PQueue _queue;
	int 	_size,
		_originState,
		_goalState;

	public:
		StateSpace(int);

		// int getOriginState();
		// int getGoalState();
		// int getSize();
};
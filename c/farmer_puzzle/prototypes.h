#include <iostream>
#include <cstdarg>

class State{

	private	:
		bool visited;
		int	cost;
		int	parentState;
		int id;

		int * neighbors;

	public	:

		State();
		State(int, int);

		// getters
		bool isVisited();
		int getCost();
		int getParent();
		int * getNeighbors();
		int getID();

		// setters
		void setStatus(bool);
		void setCost(int);
		void setParent(int);
		void setNeighbors(int x, ...);

};

class PQueue{

	private :
		// holds the states in queue, waiting to be expanded, in order of their priority
		State * states;
		int currentIndex;
		int size;

	public :
		PQueue(int);
		State remove();
		void insert(State);
		bool isEmpty();
};

class StateSpace{

	int originState,
		goalState,
		nStates;
	State * states;
	PQueue queue;

	public	:
		StateSpace();
		StateSpace(int);
		void startSearch();
};
/*

State and ID Map:
	
	fwsc|NULL - 1 	-> origin state
	NULL|fwsc - 15	-> goal state

	ws|fc - 2
	wc|fs - 3
	sc|fw - 4
	wsc|f - 5
	fws|c - 6
	fwc|s - 7
	fsc|w - 8
	w|fsc - 9
	s|fwc - 10
	c|fws - 11
	fw|sc - 12
	fs|wc - 13
	fc|ws - 14
	f|swc - 16


*/

#include <iostream>
#include <string>
#include <cstdarg>

using namespace std;

class State{

	int 	
		// integer id of the state
		_id,
		// integer id of the parent state, that is the state from which this state is reached
		_parent,
		_cost,
		// number of neighbors
		_nNeighbors,
		// heuristic cost (0 for all nodes in BFS, relaxed heuristic for A*)
		// heuristic cost for various states can be found in the StateSpace constructor
		_h_cost;

	string _figure; // string representation of the state

	bool _visited; // boolean flag to know if the state is already visited - Graph Seach property

	int * _neighbors; // holds the integer state-ids of the adjacent states i.e states reachable with one move

	public:
		// constructors
		State();
		State(int id, string representation, int h_cost);
		State(State &);

		// getters
		int getID();
		int getParent();
		int getCost();
		int getNeighborsLength();
		bool isVisited();
		int * getNeighbors();
		string getRepresentation();
		int getHCost();

		// setters
		void setParent(int);
		void setCost(int);
		void setStatus(bool);
		void setNeighbors(int n, ...); // takes variable number of parameters, as the neighbors for different nodes arr different in number
};

/*
	Priority Queue to get nodes/ states in a pre-defined priority order

	BFS - no priority as the heuristic for each node is 0 and cost = depth
	A*   - priority is the sum of path cost and the heuristic cost
*/
class PQueue{

	State **_states; // pointer to an array of states

	int 	_currentIndex, // advances the root/ head of the queue after deletion
		_size; // keeps track of the number of states in the queue

	public:
		PQueue(int);
		void insert(State *);
		State * remove();
		bool isEmpty();
		void printQueue();
		void heapify(); // this function creates the priority queue, and brings the most priority element to the root of the heap

		int getCurrentIndex();
		int getSize();
};

class StateSpace{

	State ** _states; // pointer to an array of states
	PQueue _queue; // maintains a priority queue of states to be expanded
	int 	_size, // number of states in the state-space
		_originState,
		_goalState;

	public:
		StateSpace(int, string);
		void startSearch();
		void printStateSpace();
		void printPath(int);
};
#include <iostream>
#include <string>

// declares the class and mthod prototypes
#include "prototypes.h"

using namespace std;

int main(int argc, char ** argv){

	// argument from the command-line { bfs, astar}
	string algorithm = argv[1];

	StateSpace stateSpace(16, algorithm); // 16 states

	stateSpace.startSearch();

	return 0;

}
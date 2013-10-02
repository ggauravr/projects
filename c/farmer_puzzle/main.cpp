#include <iostream>
#include <string>
#include "prototypes.h"

using namespace std;

int main(int argc, char ** argv){

	string algorithm = argv[1];

	StateSpace stateSpace(16, algorithm); // 16 states

	stateSpace.startSearch();

	return 0;

}
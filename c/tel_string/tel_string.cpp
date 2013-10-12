/*
	Program to print all possible words that can indicate a seven digit number, from a typical
	3-letters to 1-digit map of a cell phone key

	Example : 	2 -> [ABC]
				9 -> [WXY]

	Author : Gaurav Ramesh

	Framework for Backtracking : Steven Skiena's wonderful general model
	
	Solution Vector
		7-letter string

*/

#include <iostream>
#include <string>
#include <vector>

#define BOARDSIZE 8

using namespace std;

int nSolutions = 0;

struct t_point{
	int x, y;
};

bool isASolution(vector<t_point> solution, int k, int n){

	t_point last_position = solution[k];

	// when the rook is at the destination, origin is assumed to be 1,1 and destination is n,n
	return last_position.x == n && last_position.y == n;
}

void constructCandidates(vector<t_point> solution, int k, int n, vector<t_point> &candidates, int * nCandidates){


}

void processSolution(vector<t_point> solution, int k, int n){
	int i;
		
	nSolutions++;
	cout << "Path " << endl;
	for(i=0; i <= k; i++){
		cout << "(" << solution[i].x << "," << solution[i].y << ")";
	}
	cout << endl;
}

void computePath(vector<t_point> solution, int k, int n){
	
	vector<t_point> candidates;
	int nCandidates, i;
	
	if(isASolution(solution, k, n)){
		processSolution(solution, k, n);
	}
	else{
		k += 1;
		constructCandidates(solution, k, n, candidates, &nCandidates);

		for(i=0; i < nCandidates; ++i){

		}
	}
}

int main(){
	
	vector<t_point> solution;

	// starting position
	t_point origin = {1,1};

	// push the starting point to the solution vector
	solution.push_back(origin);

	computePath(solution, 0, BOARDSIZE);
	
	cout << "number of shortest paths " << nSolutions << endl;

	return 0;
}
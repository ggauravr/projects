/*
	Program to find all possible paths of a rook from one corner of a chessboard to the diagonally opposite corner

	Author : Gaurav Ramesh

	Framework for Backtracking : Steven Skiena's wonderful general model

	Output
		prints all possible paths as a set list of (x, y) coordinates
		prints the number of solutions / paths obtained

	Solution Vector
		array / vector of coordinates of the squares the rook visited

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

	t_point last_position = solution[k-1];
	t_point temp_pos;

	*nCandidates = 0;

	// if x-coordinate is < n, it can move one step right
	if(last_position.x < n){
		
		temp_pos.x = last_position.x + 1; 
		temp_pos.y = last_position.y;
		candidates.push_back(temp_pos);
			
		*nCandidates = *nCandidates + 1;
	}

	// if y-coordinate is < n, it can move one step up/ forward
	if(last_position.y < n){
		temp_pos.x = last_position.x;
		temp_pos.y = last_position.y+1;
		candidates.push_back(temp_pos);
		
		*nCandidates = *nCandidates + 1;
	
	}

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
	vector<t_point>::iterator iter;

	if(isASolution(solution, k, n)){
		processSolution(solution, k, n);
	}
	else{
		k += 1;
		constructCandidates(solution, k, n, candidates, &nCandidates);

		// for all candidates, push the candidate, continue on the path(recurse)
		// remove the candidate, try some other one
		for(i=0; i < nCandidates; ++i){
			solution.push_back(candidates[i]);
			computePath(solution, k, n);
			solution.pop_back();
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

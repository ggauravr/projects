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

using namespace std;

int nSolutions = 0;

bool isASolution(vector<char> solution, size_t size, int n){

	return size == n;
}

void constructCandidates(int size, int &count_open, int &count_close, int n, vector<char> &candidates, int &nCandidates){

	nCandidates = 0;
	if(count_open > count_close){
		// close paren can be a candidate
		cout << "count open .. count close " << count_open << ".." << count_close << endl;
		candidates.push_back(')');
		++count_close ;
		nCandidates += 1;
	}

	if(count_open < n){
		cout << "count open .. n " << count_open << ".." << n << endl;
		candidates.push_back('(');
		++count_open ;	
		nCandidates += 1;
	}

}

void processSolution(vector<char> solution){
	int i,
		length = solution.size();

	for(i=0; i < length ; ++i){
		cout << solution[i];
	}

	cout << endl;
}

void constructParens(vector<char> solution, size_t size, int count_open, int count_close, int n){
	
	vector<char> candidates;
	int nCandidates;

	if(isASolution(solution, size, n)){
		processSolution(solution);
	}
	else{
		size += 1;

		constructCandidates(size, count_open, count_close, n, candidates, nCandidates);

		for(int i=0; i < nCandidates; ++i){
			solution.push_back(candidates[i]);
			constructParens(solution, size, count_open, count_close, n);
			solution.pop_back();
		}
	}
	
}

int main(){
	
	int n;
	vector<char> solution;

	cout << "enter the number of paren-pairs " << endl;
	cin >> n;

	constructParens(solution, 0, 0, 0, n);

	return 0;
}

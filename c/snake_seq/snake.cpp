#include <iostream>
#include <string>
#include <vector>
#include <cstdlib>

using namespace std;

struct Point{
	int x, y;
};	

vector< vector<Point> > results;
int max_length_so_far = 0;

bool isASolution(vector<Point> solution){

	cout << "solution size " << solution.size() << endl;

	if(solution.size() > max_length_so_far){
		max_length_so_far = solution.size();
		results.clear();
		results.push_back(solution);
		return true;
	}
	else if(solution.size() == max_length_so_far){
		results.push_back(solution);
		return true;	
	}

	return false;

}

void processSolution(){
	// no processing of intermediate solutions..
}

void constructCandidates(vector<Point> solution, size_t k, int rows, int cols, int input[][5], vector<Point> &candidates, int *nCandidates){

	// for the last element in solution vector, check the cell to the right and bottom of it, if they differ by +/- 1, add them to candidates
	Point last_cell = solution[k-1],
			temp;

	// cout << "x -> "<< last_cell.x << " y->" << last_cell.y << ".." <<input[last_cell.y][last_cell.x] - input[last_cell.y][last_cell.x+1] << ".. " << last_cell.y << " .. " << "rows .. " << rows << " cols.. " << cols << endl;

	*nCandidates = 0;
	if( 	last_cell.x < cols-1 && abs(input[last_cell.y][last_cell.x] - input[last_cell.y][last_cell.x+1]) == 1 ){

		// add right cell to candidates
		temp.x = last_cell.x+1;
		temp.y = last_cell.y;

		candidates.push_back(temp);
		*nCandidates = *nCandidates + 1;
	}

	if( 	last_cell.y < rows-1 && abs(input[last_cell.y][last_cell.x] - input[last_cell.y+1][last_cell.x]) == 1 ){
		// add right cell to candidates
		temp.x = last_cell.x;
		temp.y = last_cell.y+1;

		candidates.push_back(temp);
		*nCandidates = *nCandidates + 1;
	}

}

void generateSequence(vector<Point> solution, size_t size, int rows, int cols, int input[][5]){
	// n is the dimension of the matrix

	vector<Point> candidates;
	int 	nCandidates,
		i;

	if(isASolution(solution)){
		processSolution();
	}
	
	size += 1;

	constructCandidates(solution, size, rows, cols, input, candidates, &nCandidates);

	cout << "solution " << candidates.size() << ".. candidates " << nCandidates << endl;

	if(!nCandidates){
		return;
	}
	// cout << "candidate " << candidates[0].x << ".." << candidates[0].y << endl;
	for(i=0; i < nCandidates; ++i){
		// cout << "candidate " << candidates[i].x << ".." << candidates[i].y << endl;
		solution.push_back(candidates[i]);
		generateSequence(solution, size, rows, cols, input);
		solution.pop_back();
	}

	
}

int main(){

	// multiple solutions possible, so solution vector will be a vector or vectors
	vector<Point> solution, temp_solution;
	int i, j;

	int input[][5] = {
		{1,3,2,6,8},
		{-9,7,1,-1,2},
		{1,5,0,1,9}
	};

	Point temp;

	// call the generate seuqnce for all the cells in the matrix
	for(i=0; i < 3; ++i){
		for(j=0; j < 5; ++j){
			temp.x = j;
			temp.y = i;
			solution.push_back(temp);
			// cout << " temp " << temp.x << "," << temp.y << endl;
			generateSequence(solution, 0 , 3, 5, input);
			solution.pop_back();
		}
	}

	// print the final set of longest snake paths
	cout << "path is " << endl;

	for(i=0; i < results.size(); ++i){

		for(j=0; j < results[i].size(); ++j){
			// cout << "results i size " << results.size() << endl;
			temp_solution = results[i];

			cout << input[temp_solution[j].x][temp_solution[j].y];
		}
	}

	cout << endl;

	return 0;
}
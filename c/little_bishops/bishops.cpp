#include <iostream>
#include <string>
#include <cstdlib>

#define MAXCANDIDATES 64

using namespace std;

struct Point{
	int x, y;
};

int nSolutions = 0;

bool isASolution(int size, int k){

	// cout << "size " << size << " k " << k << endl;
	return size == k;
}

void processSolution(Point solution_vector[], int k){
	int i;
	nSolutions++;
	// for(i=0; i < k; i++){
	// 	cout << solution_vector[i].x << ", " << solution_vector[i].y << endl;
	// }
	// cout << endl;
}

void constructCandidates(Point solution_vector[], int size, int n, int k, Point candidates[], int *nCandidates){

	bool is_valid[n][n];
	int i, j, p;

	for(i=0;i<n;i++){
		for(j=0; j<n;j++){
			is_valid[i][j] = true;
		}
	}

	for(i=0;i<n;i++){
		for(j=0; j<n;j++){
			for(p=0; p < size; p++ ){
				
				if(abs(i-solution_vector[p].x) == abs(j-solution_vector[p].y) ){
					is_valid[i][j] = false;
				}

			}
		}
	}	

	*nCandidates = 0;
	for(i=0;i<n;i++){
		for(j=0; j<n;j++){
			if(is_valid[i][j]){
				candidates[*nCandidates].x = i;
				candidates[*nCandidates].y = j;

				*nCandidates = *nCandidates + 1;
			}
		}
	}

	// cout << "nCandidates " << *nCandidates << endl;

}

void checkPositions(Point solution_vector[], int size, int n, int k){

	Point candidates[MAXCANDIDATES];
	int nCandidates, i;

	if(isASolution(size, k)){
		processSolution(solution_vector, k);
	}
	else{
		size += 1;
		cout << " size " << size << endl;
		constructCandidates(solution_vector, size, n, k, candidates, &nCandidates);
		// cout << "solution vector " << endl;
		// for(i=0; i < sizeof(solution_vector)/sizeof(Point); i++){
		// 	cout << solution_vector[i].x << ", "  << solution_vector[i].y << "candidates " << nCandidates << endl;
		// }

		for(i=0; i < nCandidates; i++){
			solution_vector[size-1] = candidates[i];

			cout << "candidates " << candidates[i].x << ", " << candidates[i].y << " and size " << size << endl;

			checkPositions(solution_vector, size, n, k);
		}
	}

}

int main(){

	int k, n;
	Point * solution_vector;

	cout << "enter n and k " << endl;
	cin >> k >> n;

	solution_vector = new Point[k];

	checkPositions(solution_vector, 0, n, k);

	cout << "Total Solutions " << nSolutions << endl;

	return 0;
}
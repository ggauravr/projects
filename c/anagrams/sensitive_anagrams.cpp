#include <iostream>
#include <string>
// #include <algorithm>

#define CHARSMAX 26

using namespace std;

int nSolutions = 0;

bool isASolution(char solution_vector[], int k, int n){
	return k == n;
}

void processSolution(char solution_vector[], int k, int n){
	nSolutions++;
	cout << "Solution " << solution_vector << endl;
	// cout << "n Solutions " << nSolutions << endl;
}

void constructCandidates(char solution_vector[], int k, int n, char candidates[], int * nCandidates, string input){

	bool is_used[CHARSMAX];
	int i, index;

	for(i=0; i < CHARSMAX; i++){
		is_used[i] = false;
	}

	// cout << " i " << i << " k " << k << endl; 
 	for(i=0; i < k; ++i){
 		// cout << " i " << i << " k " << k << endl; 
		// mark the integer equivalent i.e the index as true
		if(solution_vector[i]){
			is_used[ solution_vector[i] - 'a' ] = true;
		}
		// cout << solution_vector[i] << endl;
	}
	// cout << " candidates " << input << endl;

	*nCandidates = 0;
	for(i=0; i < n; ++i){
		index = input[i] - 'a';
		// cout << "index " << index << endl;
		if(!is_used[index]){
			candidates[*nCandidates] = 'a' + index; // restore thr character back
			// cout << " cndiaes " << candidates[*nCandidates] << endl;
			*nCandidates = *nCandidates + 1;
		}
	}

	// cout << "ncandidates " << *nCandidates << endl;
	candidates[*nCandidates] = '\0';

}

void generateAnagrams(char solution_vector[], int k, int n, const string input){

	char 	candidates[n+1];	// max n characters 
	int 		nCandidates,
			i;

	if(isASolution(solution_vector, k, n)){
		processSolution(solution_vector, k, n);
	}
	else{
		k += 1;

		constructCandidates(solution_vector, k, n, candidates, &nCandidates, input);
		// cout << " n candidates  " << nCandidates << endl;

		// add every possible candidate to solution vector and continue along the path
		for(i=0; i < nCandidates ; ++i){
			solution_vector[k-1] = candidates[i];
			solution_vector[k] = '\0';

			// cout << "solution vector " << solution_vector << " candidates " << candidates[i] << " k " << k << endl;
			generateAnagrams(solution_vector, k, n, input);

			// solution_vector = solution_vector.substr(0, solution_vector.length() -1);
		}
	}

}

int main(){

	string input;
	char solution_vector[CHARSMAX];

	// take string input
	cout << "Please enter the string you wish anagrams for " << endl;
	cin >> input;

	// convert the input string to lower or upper case for assigning integer values.. call anagrams function
	// input = transform(input.begin(), input.end(), input.begin(), ::tolower);

	generateAnagrams(solution_vector, 0, input.length(), input);
	cout << " number of solutions " << nSolutions << endl;

	return 0;
}
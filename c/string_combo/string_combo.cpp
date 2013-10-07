/*
*	Program to pring all possible combinations(of length 1 to n) of a given string input
*	Author : Gaurav Ramesh
*	
*	Example:
*		Input : abc
*		Output : c, b, bc, a, ac, ab, abc
*/

#include <iostream>
#include <string>
#include <vector>

using namespace std;

vector<string> results;

void generate(string input, size_t size){

	vector<string>::iterator iter = results.begin();
	string temp;
	vector<string> temp_vector;

	/*
		for the already generated items, prefix the current character

		doesn't enter the loop the very first time.. 
	*/
	for( ; iter != results.end(); ++iter){
		temp += input[size] + *iter;
		temp_vector.push_back(temp);
		temp = "";
	}

	// add this single character to the list of results too 
	temp += input[size];
	results.push_back(temp);

	results.insert(results.end(), temp_vector.begin(), temp_vector.end());

	if(size == 0){
		return;
	}
	else{
		generate(input, size-1);
	}

}

int main(){

	string input;
	vector<string>::iterator iter;

	cout << "please enter the string input " << endl;
	cin >> input;

	generate(input, input.size()-1);

	cout << "the " << results.size() << " combinations of the given string " << input << " are .. " << endl;
	for(iter = results.begin() ; iter != results.end() ; ++iter){
		cout << *iter << " " ;
	}

	cout << endl;

	return 0;
}
#include <iostream>
#include <string>

using namespace std;

string findLongestNRPSubtring(string input, size_t size){

	string result;

	if(input.size() == 1){
		return input;
	}

	// slice the last character of the string
	result = findLongestNRPSubtring(input.substr(0, input.size() - 1), input.size() - 1);

	cout << "result " << result << " current character " << input[size-1] << endl;
	cout << "find result " << std::string::npos << endl;

	if( result.find(input[size-1]) != std::string::npos ){
		return result;
	}
	else{
		return result+input[size-1];
	}

}

int main(){

	string input;
	string output;

	cout << "please enter the string input " << endl;
	cin >> input;

	output = findLongestNRPSubtring(input, input.size());

	cout << " longest non-repeating substring is " << output << endl;

	return 0;
}
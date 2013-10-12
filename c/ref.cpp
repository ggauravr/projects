#include <iostream>
#include <string>

using namespace std;

int main(){
	
	string one = "string one";
	string &two = one;
	
	string three = two;

	cout << "the strings in order.. 1, 2, 3 " << one << ".." << two << ".." << three << endl; 
	
	three = "string three";
	two = &three;

	cout << "third string and first string " << three << ".. " << one  << endl;
	
	return 0;
}

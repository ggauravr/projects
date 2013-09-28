#include <iostream>
#include <string>
#include "strnum.h"

using namespace std;

int main(){
	
	string input_string;
	long int result;	
	StringNumber num_object;

	cout << "Enter the string you want to convert(Octal beginning with 0, Hex beginning with 0x/0X, Decimal otherwise)" << endl;
	cin >> input_string;

	num_object.setInput(input_string);

	result = num_object.convert();
	
	cout << "Result of conversion is .. " << result << endl;

	return 0;
}


#include <iostream>
#include <string>
#include <climits>
#include "strnum.h"

using namespace std;

StringNumber::StringNumber() : value(0), base(DECIMAL){}

int StringNumber::getCharacterValue(const char c){

	// check for character range compared to base.. return int equivalent of A-F for hex
	int char_value = -1;

	if(c >= '0' && c <= '9'){
		char_value = c - '0';
	}
	else if(c >= 'A' && c <= 'Z'){
		char_value = c - 'A' + 10;
	}
	else if(c >= 'a' && c <= 'z'){
		char_value = c - 'a' + 10;
	}
	
	// invalid character for given base	
	if(char_value >= base){ 
		char_value = -1; 
	}
	
	return char_value;
}

void StringNumber::setInput(const std::string input_string){
	input = input_string;
}

long int StringNumber::convert(){
	
	bool 		isNegative = false;	
	int  		start	= 0;
	char 		initial	= input[start];
	
	long int 	threshold, 
				last_digit, 
				char_value;


	cout << "Max-Range of long int is : " << LONG_MAX << ", Min-Range of long int is : " << LONG_MIN << endl;

	// handle +/- sign in the beginning
	if(initial == '-'){
		isNegative = true;
		start += 1;
	}
	else if(initial == '+'){
		start += 1;
	}

	// handle bases : 0 for octal, 0x/0X for hex and nothing for decimal
	if(input[start] == '0'){
		// might be octal or hex
		start += 1;
		if(input[start] == 'x' || input[start] == 'X'){
			base = HEXADECIMAL;
			start += 1;
		}
		else{
			base = OCTAL;
		}
	}
	
	threshold 	= isNegative ? LONG_MIN / DECIMAL : LONG_MAX / DECIMAL;	// digits in the min/max number except last digit
	last_digit 	= isNegative ? -1 * (LONG_MIN % DECIMAL) : LONG_MAX % DECIMAL;	// last digit in the min/max range

	
	for(int i = start; i < input.size(); ++i){
		
		char_value = getCharacterValue(input[i]);
		
		if(char_value == -1){
			value = char_value;
			break;
		}

		// handle overflow
		if(isNegative){
			if( value < threshold || ( value == threshold && char_value > last_digit ) ){
				value = -1;
				break;
			}
			else{
				value = value * base - char_value;
			}
		}
		else{
			if( value > threshold || ( value == threshold && char_value > last_digit ) ){
				value = -1;
				break;
			}	
			else{
				value = value * base + char_value;
			}
		}
	
	}

	return value;

}
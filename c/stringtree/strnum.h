#ifndef  GUARD_STRNUM
#define GUARD_STRNUM

#include <string>

enum Base_Type { DECIMAL = 10, OCTAL = 8, HEXADECIMAL = 16 };

class StringNumber{

	private:
		std::string input;
		long int value;
		Base_Type base;
		
		/*
			input 	-
				character currently being processed by the function

			output 	-
				the character value in int, [0-15], depending on the base, -1 if invalid
		*/
		int getCharacterValue(char);

	public:
		StringNumber();
		
		/*
			convert
				output	-
					integer value of the string entered, -1 if invalid/error
		*/
		long int convert();

		/*
			setter for input
		*/
		void setInput(std::string);

};

#endif
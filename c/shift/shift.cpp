#include <iostream>

using namespace std;

int main(){

	int x = -1;
	int y = -1;

	cout << "x shifted left 31 bits is " << sizeof(int) << ".." << (x << 31) << endl;
	cout << "x shifted right 31 bits is " << (x >> 31) << endl;


	return 0;
}
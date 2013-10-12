#include <iostream>

using namespace std;

int main(){
	
//	char c;
	int  c;

	cout << "enter the int" << endl;	
	while(cin >> c){
		if(c > 9 ){
			cin.clear();
			cout << "invalid number .. please enter again" << endl;
			continue;
		}
		cout << "int entered " << c << " its char value " << (int)c-'0' << endl;
	}

	
	return 0;
}

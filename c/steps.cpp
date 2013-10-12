#include <iostream>
#include <string>

using namespace std;

int nWays = 0;

int calculateSteps(int n){
	
	if(n==0){
		return 1;
	}

	nWays += calculateSteps(n-1);
	nWays += n-2 >=0 ? calculateSteps(n-2) : 0;
	nWays += n-3 >=0 ? calculateSteps(n-3) : 0;
	nWays += n-4 >=0 ? calculateSteps(n-4) : 0;
		
	return 0;
}

int main(){
	
	int n;

	cout << "please enter the number of steps" << endl;
	cin  >> n;

	calculateSteps(n);

	cout << " number of ways to go " << n << " steps with 1, 2, or 3 steps at a time..  " << nWays << endl;
	
	return 0;
}

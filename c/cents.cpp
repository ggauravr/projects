#include <iostream>
#include <string>

using namespace std;

int nWays = 0;

int calculateCents(int n){
	
	if(n==0){
		return 1;
	}

	nWays += calculateCents(n-1);
	nWays += n-5 >=0 ? calculateCents(n-5) : 0;
	nWays += n-10 >=0 ? calculateCents(n-10) : 0;
	nWays += n-25 >=0 ? calculateCents(n-25) : 0;
		
	return 0;
}

int main(){
	
	int n;

	cout << "please enter the number of cents" << endl;
	cin  >> n;

	calculateCents(n);

	cout << " number of ways to go " << n << " cents with 1, ,5, 10, 25 cents at a time..  " << nWays << endl;
	
	return 0;
}

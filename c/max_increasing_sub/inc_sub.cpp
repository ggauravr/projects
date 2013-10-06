#include <iostream>
#include <vector>

using namespace std;
	
int 	max_index_so_far = 0,
	max_length_so_far = 1;

vector<int> length_array;

int findMaxIncreasingSubsequence(const vector<int> & sequence){
	int i;
	// recursive implementation.. memoize the results

	length_array[0] = 1;

	for(i=1; i < sequence.size(); i++){
		if(sequence[i] > sequence[max_index_so_far]){
			max_index_so_far = i;
			length_array[i] = max_length_so_far + 1;
			max_length_so_far += 1;

		}
		else if(sequence[i] > sequence[i-1]){
			length_array[i] = length_array[i-1] + 1;
		}
		else{
			length_array[i] = 1;

		}
		cout << "i = " << i << " sequence[i] " << sequence[i] << " length_array[i] " << length_array[i] << endl;  
	}

	cout << "max length is " << max_length_so_far << endl;

	return max_length_so_far;

}

int main(){

	vector<int> my_sequence;
	int input;

	while(cin >> input){
		my_sequence.push_back(input);

		cin.clear();
	}

	// cout << " length of sequence " << my_sequence.size() << endl;

	length_array.resize(my_sequence.size(), 0);

	// call find subsequence
	int length = findMaxIncreasingSubsequence(my_sequence);

	cout << "length of maximum incrasing subsequence is " << length << endl;

	return 0;
}
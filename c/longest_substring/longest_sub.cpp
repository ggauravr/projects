#include <iostream>
#include <vector>
#include <string>

using namespace std;

vector< vector<int> > positions = vector< vector<int> >(26);

vector<int> getPositions(char c, string text){

	int index = c - 'a'; // transform to 0 index

	for(int i=0; i < text.length(); ++i){
		if(text[i] == c){
			positions[index].push_back(i);
		}
	}

	return positions[index];
}

string getSubstring(string text, string pattern){

	int p_length = pattern.length();

	int max_so_far = 0, index_so_far = -1, k;

	vector<int> pos;

	// have a global position array..
	for(int i=0; i < p_length; ++i){
		pos = getPositions(pattern[i], text);

		for(int j=0; j < pos.size(); ++j){

			k = 0; // matched at this position
			while(true){

				// compare pattern[i] with text[pos[j]]
				if(pattern[i] == text[pos[j]]){
					++k;
					if(k > max_so_far){
						max_so_far = k;
						index_so_far = i;
					}
				}
				else{
					break; // break from while loop, start at a different text position
				}
			}

		}
	}

	cout << "length of max substring found is " << max_so_far << " at the pattern index " << index_so_far << endl;
}

int main(){

	string string_1, string_2;
	string text, pattern, longest_substring;

	cout << "enter string one and then string two " << endl;
	cin >> string_2 >> string_2;

	text = string_1.length() > string_2.length() ? string_1 : string_2;
	pattern = string_1.length() < string_2.length() ? string_1 : string_2;

	/*longest_substring = */getSubstring(text, pattern);

	// cout << "the longest substring of text (" << text << ") and pattern (" << pattern << ") is " << longest_substring << endl;

	return 0;
}
#include <iostream>
#include "trinary.h"

using namespace std;

int main(int argc, char **argv){
	
	TTree<int> tree;
	int value, choice, to_be_removed;
	bool toExit = false;

	while(!toExit){
		cout << "\nSelect an action \n1.insert, 2.remove, 3.print, 4.exit " << endl;
		cin >> choice;
		cout << "\noption selected : " << choice << endl;

		switch(choice){

			case 1: 
				cout << "Enter the keys to be inserted.. separated by spaces.. to terminate input, press any alphabet [Enter]" << endl;
				
				while(cin >> value){
					tree.insert(tree.getRoot(), value);
				}	
				cin.clear();
				cin.ignore(1);
				break;
			case 2:
				cout << "Enter the key of the element to be deleted.. " << endl;
				cin >> to_be_removed;
				tree.remove(NULL, tree.getRoot(), to_be_removed);
				break;
			case 3: 
				cout << "Printing the tree in in-order" << endl;
				tree.printTree(tree.getRoot());
				break;
			case 4:
				cout << "Exiting the program" << endl;
				// no break here
			default:
				toExit = true;
				tree.printTree(tree.getRoot());
		}
	}

	return 0;
}

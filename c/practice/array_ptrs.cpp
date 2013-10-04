#include <iostream>

using namespace std;

class Node{

	int data;

	public:

		Node(int data){
			this->data = data;
		}

		int getData(){
			return data;
		}

};

class ListNodes{

	Node ** listOfNodes;
	int n;

	public:
		ListNodes(int n): n(n){
			listOfNodes = new Node * [n];
		}

		void fillNodes(){

			for(int i=0; i < n;i++){
				listOfNodes[i] = new Node(i);

				cout << "Node number " << i << " has the data " << listOfNodes[i]->getData() << endl;
			}

		}

};

int main(){

	ListNodes list(10);

	list.fillNodes();

	return 0;
}
#include <iostream>

using namespace std;

template <typename T>
TTree<T>::TTree() : size(0), root(NULL){ }

template <typename T>
int TTree<T>::getSize(){

	return size;
}

template <typename T>
Node<T> * TTree<T>::getRoot(){
	
	return root;
}

template <typename T>
Node<T> * TTree<T>::getNewNode(T key){

	try{

		Node<T> * new_node = new Node<T>;

		new_node->data  = key;
		new_node->left  = NULL;
		new_node->middle = NULL;
		new_node->right     = NULL;

		return new_node;
	}
	catch(bad_alloc & allocation_error){
		cout << "Memory allocation failed.. exiting program" << endl;
		return NULL;
	}
}

template <typename T>
Node<T> * TTree<T>::insert(Node<T> * ptr_subtree, T key){
	
	Node<T> * new_node;

	if(ptr_subtree == NULL){
		new_node = getNewNode(key);
		
		if(root == NULL){
			root = new_node;
		}
		size += 1;
		return new_node;
	}

	if(ptr_subtree->data == key){
		ptr_subtree->middle = insert(ptr_subtree->middle, key);
	}
	else if(ptr_subtree->data > key){
		ptr_subtree->left  = insert(ptr_subtree->left, key);
	}
	else{
		ptr_subtree->right  = insert(ptr_subtree->right, key);
	}

	return ptr_subtree;

}


template <typename T>
Node<T> * TTree<T>::remove(Node<T> ** ptr_ptr_subtree, Node<T> * ptr_subtree, T key){
	
	Node<T> * ptr_successor;
	Node<T> ** ptr_ptr_successor;
	
	if(ptr_subtree == NULL){
		cout << "The element to be removed was not found in the tree" << endl;
		return NULL;
	}
	
	if(ptr_subtree == root){
		ptr_ptr_subtree = &root;
	}
	
	if(ptr_subtree->data == key){

		size -= 1;

		/*
			if the node to be removed has middle children,
			replace the node with with first middle-child, and update its pointers
		*/
		if(ptr_subtree->middle != NULL){
			
			*ptr_ptr_subtree = ptr_subtree->middle;
			(*ptr_ptr_subtree)->left = ptr_subtree->left;
			(*ptr_ptr_subtree)->right = ptr_subtree->right;

			if(root){
				cout << "root node is " << root->data << endl;
				cout << "size of the tree now is " << size << endl;
			}

			if(*ptr_ptr_subtree){
				cout << "parent pointer of the deleted node now pointing to .. " << (*ptr_ptr_subtree)->data << endl;

			}
			delete ptr_subtree;
			return *ptr_ptr_subtree;
		}
		else{
			/*
				if the node to be removed has only one child(left or right),
				update its parent pointer to its only child and free the node
			*/
			if(ptr_subtree->left == NULL){
				*ptr_ptr_subtree = ptr_subtree->right;
				
				if(root){
					cout << "root node is " << root->data << endl;
					cout << "size of the tree now is " << size << endl;
				}

				if(*ptr_ptr_subtree){
					cout << "parent pointer of the deleted node now pointing to .. " << (*ptr_ptr_subtree)->data << endl;

				}
				delete ptr_subtree;
				return *ptr_ptr_subtree;
			}

			if(ptr_subtree->right == NULL){
				*ptr_ptr_subtree = ptr_subtree->left;
				if(root){
					cout << "root node is " << root->data << endl;
					cout << "size of the tree now is " << size << endl;
				}

				if(*ptr_ptr_subtree){
					cout << "parent pointer of the deleted node now pointing to .. " << (*ptr_ptr_subtree)->data << endl;

				}
				delete ptr_subtree;
				return *ptr_ptr_subtree;    
			}
			
			/*
				it the node to be removed has both left and right children,
				find the successor node, that replaces our target node and update the pointers

				successor node may be the in-order predecessor or the in-order successor
			*/
			ptr_ptr_successor = &ptr_subtree->left;
			ptr_successor = findSuccessor(ptr_subtree->left, ptr_ptr_successor);

			/*
				the successor node, the way we have chosen, can only have a left-child
			*/
			*ptr_ptr_successor = ptr_successor->left;
				
			*ptr_ptr_subtree = ptr_successor;
			(*ptr_ptr_subtree)->left = ptr_subtree->left;
			(*ptr_ptr_subtree)->right = ptr_subtree->right;
			if(root){
				cout << "root node is " << root->data << endl;
				cout << "size of the tree now is " << size << endl;
			}

			if(*ptr_ptr_subtree){
				cout << "parent pointer of the deleted node now pointing to .. " << (*ptr_ptr_subtree)->data << endl;

			}
			delete ptr_subtree;
			return *ptr_ptr_subtree;
		}
	}
	else if(ptr_subtree->data > key){
		ptr_subtree = remove(&ptr_subtree->left, ptr_subtree->left, key);
	}
	else{
		ptr_subtree = remove(&ptr_subtree->right, ptr_subtree->right, key);
	}
	

}

template <typename T>
Node<T> * TTree<T>::findSuccessor(Node<T> * ptr_subtree, Node<T> ** ptr_ptr_subtree){
	
	if(ptr_subtree == NULL){
		return NULL;
	}
	
	if(ptr_subtree->right == NULL){
		return ptr_subtree;
	}

	findSuccessor(ptr_subtree->right, &ptr_subtree->right);
}

template <typename T>
void TTree<T>::printTree(Node<T> * ptr){
	
	if(root == NULL){
		cout << "Empty Tree.. nothing to print" << endl;
		return;
	}

	if(ptr == NULL){
		return;
	}

	printTree(ptr->left);
	cout << ptr->data << ", ";
	printTree(ptr->middle);
	printTree(ptr->right);
}
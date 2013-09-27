#ifndef  GUARD_TTREE_H
#define GUARD_TTREE_H

template <typename S>
struct Node{
	S data;
	Node<S> * left;
	Node<S> * middle;
	Node<S> * right;
};

template <typename T>
class TTree{
	
	private:
		Node<T> * root;
		int size;

		/*
			findSuccessor
				input 	- 
					pointer to the node where the successor search should begin
					pointer to the pointer where the successor search should begin
				output 	- 
					pointer to the successor node
					pointer to the pointer to the successor node(via double dereferencing, not return value)

		*/
		Node<T> * findSuccessor(Node<T> *, Node<T> **);

		/*
			getNewNode
				input 	- 
					data value of the new node to be created
				output	- 
					returns pointer to a newly created node
		*/
		Node<T> * getNewNode(T);

	public:
		TTree();
		
		/*
			insert 
				input	-
					pointer to the root of the subtree where a node is to be inserted
				output	-
					pointer to the parent of the newly inserted node
		*/
		Node<T> * insert(Node<T> *, T);
		
		/*
			remove
				input	-
					pointer to the pointer of the of the root of the subtree, where element is to be removed, null initially
					pointer to the root of the subtree, where element is to be removed
				output	-
					pointer to the successor of the deleted node.. null if a leaf is removed

		*/
		Node<T> * remove(Node<T> **, Node<T> *, T);

		/*
		getRoot
				output	-
					returns the root node
		*/
		Node<T> * getRoot();

		/*
			printTree
				output	-
					prints the tree in in-order (left-child, parent, middle-child, right-child)
		*/
		void printTree(Node<T> *);

		int getSize();

};

#include "trinary.cpp"

#endif
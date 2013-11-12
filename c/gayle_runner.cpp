/*
  File : gayle_runner.cpp

  the problem statement was found in Cracking the coding Interview, by Gayle Laakman, in the Linked List chapter, hence the name

  the program builds a linked list of generic type and of length 2*n, where n is integer and greater than 1,
  and modifies the list such that, i-th element should be pointing to n+i -th node

  e.g : 
  input : 1->2->3->4->5->6
  output : 1>4->2->5>3>6

  input : 1->2->3->4
  output : 1>3->2->4

*/

#include <iostream>

using namespace std;

/*
  * Class : Node
  *
  * Attributes: 
  *   _data - generic type that holds the data in the node
  *   _next - pointer to the next node in the list
  *
  * Methods:
  *   Node() - no parameter constructor
  *   Node(<T> data) - parameterized constructor
  *     @param:
  *       data - generic data to be filled in the new node
  */
template <class T>
class Node{

  private:
    T _data;
    Node * _next;

  public:
    Node();
    Node(T data);

  template <class S>
  friend class SList;

};

template <class T>
Node<T>::Node(): _data(0), _next(NULL) {}

template <class T>
Node<T>::Node(T data): _data(data), _next(NULL) {}


/*
  * Class : SList - singly-linked list
  *
  * Attributes: 
  *   _head - pointer to the head of the list
  *   _length - integer holding the length of the list
  *
  * Methods:
  *   SList() - default constructor
  *   getLength() - getter to return the length of the list
  *   setAlternateLinks() - to do the main logic, alternate the links, given a list of length 2*n, n(int) > 1
  *   
  */
template <class T>
class SList{
  private:
    Node<T> * _head;
    size_t _length;
    
  public:
    SList();
    void insert(T data);
    size_t getLength();
    void setAlternateLinks();
    void display();

};

template <class T>
SList<T>::SList(): _head(NULL), _length(0) {}

template <class T>
void SList<T>::insert(T data){
  Node<T> * newNode = new Node<T>(data);

  _length++;

  if(!_head){
    _head = newNode;
    return;
  }

  newNode->_next = _head;
  _head = newNode;

}

template <class T>
size_t SList<T>::getLength(){
  return _length;
}

template <class T>
void SList<T>::setAlternateLinks(){
  
  size_t count;

  // find the n-th element, in the list of 2*n elements
  Node<T>
    * walker = _head, 
    * follower = _head,
    
    * target_after = _head,
    * target_before = target_after->_next,

    * replace_node,
    * replace_previous,
    * replace_next;

  count = 1;

  // return if length is <= 2 or odd
  if(_length <= 2 || _length % 2 ){
    cout << "Please ensure the list is of even length, and contains more than 2 nodes \n\n" << endl;
    return;
  }

  while(count != _length/2){
    walker = walker->_next;
    count++;
  }
  cout << "Walker found the n-th node " << walker->_data << endl;

  // walker is pointing to the n-th node here

  while(target_after != walker){
    cout << "target after data " << target_after->_data << " replace data " << walker->_next->_data << endl;

    replace_previous = walker;
    replace_node = walker->_next;
    replace_next = replace_node->_next;

    replace_previous->_next = replace_next;
    target_after->_next = replace_node;
    replace_node->_next = target_before;

    target_after = target_before;
    target_before = target_before->_next;
  }

}

template <class T>
void SList<T>::display(){
  Node<T> * walker = _head;

  while(walker){
    cout << walker->_data << " ";
    walker = walker->_next;
  }
  cout << endl;
}

void drive(){

  int data, choice;
  SList<int> listObject;

  cout << "Enter the choice of the action you wanna make:" << endl;
  cout << "1. Insert\t2. Remove\t3.Alternate Links\t4.Get Length\t5.Exit" << endl;

  // cin >> choice;

  while(cin >> choice){

    switch(choice){
      case 1:
        cout << "Enter the data of the node to be inserted" << endl;
        cin >> data;
        listObject.insert(data);
        break;
      case 3:
        cout << "Alternating Links.. " << endl;
        listObject.setAlternateLinks();
        cout << "List after alternating links" << endl;
        listObject.display();
        break;
      case 4:
        cout << "Length of the list is " << listObject.getLength() << endl;
        break;
      case 5:
      default:
        return;

    }
    cout << "\nEnter the choice of the action you wanna make:" << endl;
    cout << "1. Insert\t2. Remove\t3.Alternate Links\t4.Get Length\t5.Exit" << endl;

  }

}

int main(){

  drive();

  return 0;
}
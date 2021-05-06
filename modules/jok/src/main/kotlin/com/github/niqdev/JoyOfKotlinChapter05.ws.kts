// in computing, `data structures` referring to the multiple occurrences of data of a given common type are generally represented as a whole by the term `collections`
// a collection is a group of data items that have some relationship to each other: in its simplest form, this relationship is that they belong to the same group
// a data structure is a structured piece of data

// data collections are one specific `category` of data structures
// - `linear collections` are collections in which elements are related along a single dimension:
//   * each element has a relationship to the next element
//   * e.g. List
// - `associative collections` are collections that can be viewed as a function:
//   * given an object `o`, a function `f(o)` returns `true` or `false` according to whether this object belongs to the collection or not
//   * there's no relationship between the elements of the collection
//   * these collections aren't ordered
//   * e.g. set and the associative array (also called map or dictionary)
// - `graphs` are collections in which each element is related to multiple other elements
//   * e.g. binary tree (related to two other elements)

// ------------------------------

// lists can be further classified
// - access
//   * you can access some lists from one end only and others from both ends
//   * you can access any element using its position in the list, which is also called its `index`
// - type of ordering
//   * elements are read in the same order in which they were inserted: First In First Out
//   * the order of retrieval is the inverse of the order of insertion: Last In First Out
// - implementation
//   * the concepts of access type and ordering are strongly related to the implementation

// ------------------------------

// performance is often expressed in Big O notation
// - when used in computing: it indicates the way the complexity of an algorithm changes when responding to a change of input size
// - when used to characterize the performance of list operations: this notation shows how the performance varies as a function of the length of the list
// e.g. the time needed for an operation is O(1), O(log(n)), O(n), O(n^2)
// - this way of measuring performance has a real meaning for structures that can be scaled infinitely
// - trading time against memory space and complexity
// - most data structures change over time: mutable (update in place) vs immutable

// immutable techniques
// defensive copy: making a copy of the data structure before inserting an element is a time-costly operation that leads to poor performance
// data sharing: no copying occurs

// ------------------------------

// see MyList

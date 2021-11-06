// the Day-Stout-Warren algorithm is a simple method for efficiently balancing binary search trees: it's designed for in-place modifications
// one of the most efficient self-balancing tree designs is the red-black tree:
// - it's a binary search tree with some additions to its structure and a modified insertion algorithm that also balances the result
// - http://www.cs.cmu.edu/~rwh/theses/okasaki.pdf
// - http://matt.might.net/papers/germane2014deletion.pdf
// - https://matt.might.net/articles/red-black-delete
// - https://matt.might.net/articles/implementation-of-immutable-purely-functional-okasaki-red-black-tree-maps-in-scala
// - https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-046j-introduction-to-algorithms-sma-5503-fall-2005/video-lectures/lecture-10-red-black-trees-rotations-insertions-deletions

// a queue is a kind of list with a specific access protocol
// - single-ended: the access protocol is last in, first out (LIFO) e.g. singly linked list
// - double-ended: the access protocol is first in, first out (FIFO) access protocol
// - alternatively, priority queue access protocol:
//   * values can be inserted in a priority queue in any order, but those can only be retrieved in a specific order
//   * all values have a priority level, and only the element with the highest priority is available
//   * priority is represented by an ordering of the elements, which implies that the elements must be comparable in some way
//   * the priority corresponds to the position of the elements in a theoretical waiting queue
//   * the highest priority belongs to the element with the lowest position (the first element). By convention, the highest priority is represented by the lowest value
//   * because a priority queue contains comparable elements, this makes it a good fit for a tree-like structure
//   * from the user's perspective, the priority queue is seen as a list with a head (the element with the highest priority, meaning the lowest value) and a tail (the rest of the queue)
//   use case: insert elements into a priority queue in random order and retrieve them sorted (small data set only)
//   use case: reordering elements after asynchronous parallel processing

// leftist heap as a heap-ordered tree with an additional leftist property
// - a heap-ordered tree is a tree in which each branch of an element is greater than or equal to the element itself.
//   This guarantees that the lowest element in the tree is always the root element, making access to the lowest value instantaneous
// - with the leftist property, for each element, the left branch rank is greater than or equal to the right branch rank
// - the rank of an element is the length of the right path (also called the right spine) to an empty element.
//   The leftist property guarantees that the shortest path from any element to an empty element is the right path.
//   A consequence of this is that elements are always found in ascending order along any descending path
// - retrieving the highest priority element is possible in constant time because it'll always be the root of the tree

// TODO pag 318

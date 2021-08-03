// - binary trees: each element (node) is linked to two elements (branches), each branch leads to a new tree (subtree), terminal elements are called leaves
// - a tree in which all elements have either two branches or no branches is called a full tree
// - a perfectly balanced tree is a tree in which the two branches of all subtrees contain the same number of elements
// - a singly linked list can be seen as a special case of a totally unbalanced tree
// - the number of elements is called the `size`
// - the number of levels, not counting the root, is called the `height`
// - the `height` is also used to characterize individual elements: it refers to the length of the longest path from an element to a leaf
// - the `depth` of an element is the length of the path from the first element `root` to the element
// - a `leafy trees` is a tree has branches that that don't hold values: only the terminal nodes hold values

// - Ordered Binary Tree, also called a Binary Search Tree, or simply BST
//   * it contains elements that can be ordered
//   * all elements in one branch have a lower value than the root element (usually left branch)
//   * all elements in the other branch have a higher value than the root element (usually right branch)
//   * the same condition holds for all subtrees
//   * they can never contain duplicates
//   * allow for fast retrieval of elements
//   * searching a perfectly balanced ordered binary tree takes an amount of time proportional to the height of the tree
//   * a search takes a time proportional to log2(n), with `n` being the size (number of elements) of the tree
//   * by contrast, the search time in a singly linked list is proportional to the number of elements

// the balance of the tree depends on the order in which elements are inserted
// recursive traverse: depth-first pre-order|in-order|post-order
// non-recursive traverse: breadth-first

// if the elements inserted are ordered you end up with an unbalanced tree and it will probably overflow the stack

package DataStructures;

import java.util.*;


/**
 *  Implementation of a red-black tree, a kind of self-balancing binary
 *  search tree where balance is achieved through maintaining certain
 *  node colouring properties.  Search, insertion and deletion are all
 *  performed in O(log n) time.
 *
 *
 *  In addition to the requirements imposed on a binary search tree the following must be satisfied by a red–black tree:
 *
 *  1. Each node is either red or black.
 *  2. The root is black. This rule is sometimes omitted. Since the root can always be changed from red to black, but
 *  not necessarily vice versa, this rule has little effect on analysis.
 *  3. All leaves (NIL) are black.
 *  4. If a node is red, then both its children are black.
 *  5. Every path from a given node to any of its descendant NIL nodes contains the same number of black nodes.
 *  Some definitions: the number of black nodes from the root to a node is the node's black depth; the uniform number of
 *  black nodes in all paths from root to the leaves is called the black-height of the red–black tree.
 *
 */
public class RedBlackTree {
    private RedBlackNode root;

    // This is a sentinel node which plays the role of every null-leaf in the tree.
    private final static RedBlackNode LEAF = new RedBlackNode();

    /*************** GENERAL HELPERS **********************/

    /**
     * Performs a tree rotation either leftward or rightward on the given node
     *
     * @param lead Node being rotated on
     * @param toLeft Whether the desired rotation is leftward, false implies rightward
     */
    private void rotate(Node lead, boolean toLeft) {
        assert lead != LEAF : "Trying to rotate on a LEAF as lead.";
        Node parent = lead.parent;
        Node centre = toLeft ? lead.right : lead.left;
        assert(centre != LEAF);

        // Re-route the links touching the lead node
        if (toLeft) {
            lead.right = centre.left;
            if (centre.left != null && centre.left != LEAF) {
                centre.left.parent = lead;
            }
        } else {
            lead.left = centre.right;
            if (centre.right != null && centre.right != LEAF) {
                centre.right.parent = lead;
            }
        }
        lead.parent = centre;

        // Re-route the links touching the centre node
        centre.left = toLeft ? lead : centre.left;
        centre.right = toLeft ? centre.right : lead;
        centre.parent = parent;

        // Finally, re-assign the link from lead's original parent.
        if (parent == null) {
            root = (RedBlackNode) centre;
        } else {
            assert lead == parent.left || lead == parent.right :
                    String.format("rotate: Attempting rotate on %s but found inconsistent links with parent %s", lead, parent);
            if (lead == parent.left) {
                parent.left = centre;
            } else {
                parent.right = centre;
            }
        }

    }

    /************ SEARCH METHODS *********************/

    /**
     * Searches for a given value in the tree
     *
     * @param value The value to search the tree for
     * @return Whether the given value was found in the tree or not
     */
    public boolean search(int value) {
        return searchRec(root, value) != null;
    }

    /**
     * Searches for a given value in a subtree
     *
     * @param root Root of the subtree to search through
     * @param value The value to search the tree for
     * @return The target node if it exists in the tree, otherwise null
     */
    private RedBlackNode searchRec(RedBlackNode root, int value) {
        if (root == null || root.equals(LEAF)) {
            return null;
        }
        if (root.data.equals(value)) {
            return root;
        }
        return searchRec(root.data < value ? root.right() : root.left(), value);
    }

    /************ INSERT METHODS *********************/

    /**
     * If the given node does not already exist in the tree, inserts it.
     *
     * @param n Node to be inserted
     */
    void insert(RedBlackNode n) {
        // Performs a simple binary search tree insertion
        if (insertRec(root, n)) {
            // Repair any red-black tree conditions that were broken by the insertion
            insertRepair(n);

            // The root may have changed due to tree rotations, so we'll have to re-find it just in case
            root = n;
            while (root.parent != null) {
                root = (RedBlackNode) root.parent;
            }
        }
    }

    /**
     * Performs a simple binary search tree insertion without regard for red-black tree conditions
     *
     * @param root Root of the subtree to perform the insertion on
     * @param n Node to be inserted
     * @return true if the insertion was successful, false if the given node was already included in the tree
     */
    private boolean insertRec(RedBlackNode root, RedBlackNode n) {
        if (root != null) {
            if (n.data.equals(root.data)) {
                return false;
            } else if (n.data > root.data) {
                if (root.right == LEAF) {
                    root.right = n;
                } else {
                    return insertRec(root.right(), n);
                }
            } else if (n.data < root.data) {
                if (root.left == LEAF) {
                    root.left = n;
                } else {
                    return insertRec(root.left(), n);
                }
            }
        }

        n.parent = root;
        n.left = LEAF;
        n.right = LEAF;
        n.colour = RedBlackNode.Colour.RED;
        return true;
    }

    /**
     * Repairs red-black tree conditions that have been broken by performing a simple BST
     * insertion with n.
     * In case 3, a tail-recursive call is performed which could run all the way up to the tree root in thw worst case.
     * This gives insert an O(log n) time complexity.
     *
     * @param n Node that was inserted and where repair-checks should start from
     */
    private void insertRepair(RedBlackNode n) {
        RedBlackNode parent = (RedBlackNode) n.parent;
        if (n.parent == null) {
            // Case 1 - n is the new functional root
            // Then assign the tree's root field.
            root = n;
            n.colour = RedBlackNode.Colour.BLACK;
        } else if (parent.colour == RedBlackNode.Colour.BLACK) {
            // Case 2 - n's parent is black
            // Then we're done because no red-black tree conditions are broken
        } else {
            RedBlackNode uncle = n.getUncle();
            if (uncle != null && uncle.colour == RedBlackNode.Colour.RED) {

                // Case 3 - parent and uncle are red
                // Then recolour parent, uncle and grandparent
                RedBlackNode grandParent = n.getGrandParent();
                assert(grandParent != null && n.getUncle() != null);
                parent.colour = RedBlackNode.Colour.BLACK;
                uncle.colour = RedBlackNode.Colour.BLACK;
                grandParent.colour = RedBlackNode.Colour.RED;

                // Grandparent might now be violating property 2 so we have to recursively run the
                // repair algorithm on it.
                insertRepair(grandParent);
            } else {
                // Case 4 - parent is red and uncle is black
                insertCase4(n);
            }
        }
    }

    /**
     * Handles case 4 of the insert repair step.
     * The parent P is red but the uncle U is black.
     *
     * @param n Node that was inserted
     */
    private void insertCase4(Node n) {
        Node parent = n.parent;
        Node grandParent = n.getGrandParent();
        // step 1 - if n is on the "inside" of the tree, rotate it to the outside
        if (n == grandParent.left.right) {
            rotate(parent, true);
            n = n.left;
        } else if (n == grandParent.right.left) {
            rotate(parent, false);
            n = n.right;
        }

        insertCase4Step2((RedBlackNode) n);
    }

    /**
     * Now that n is on the "outside" of the tree, we can perform step 2.
     *
     * @param n Node that was inserted
     */
    private void insertCase4Step2(RedBlackNode n) {
        RedBlackNode parent = (RedBlackNode) n.parent;
        RedBlackNode grandParent = n.getGrandParent();

        // Peform a rotation towards the opposite side that n is on
        rotate(grandParent, n == parent.right);
        // Then recolour to maintain red-black tree properties.
        parent.colour = RedBlackNode.Colour.BLACK;
        grandParent.colour = RedBlackNode.Colour.RED;
    }

    /************ DELETE METHODS *********/

    /**
     * Deletes the given value from the tree, if it exists, and maintain all red-black tree properties
     *
     * @param value Value of the node that should be deleted
     */
    void delete(int value) {
        RedBlackNode current = root;
        // Perform a simple BST search
        while (current != null && current != LEAF && value != current.data) {
            current = value > current.data ? current.right() : current.left();
        }

        if (current != null && current != LEAF) {
            if (current.left != LEAF && current.right != LEAF) {
                // Special case: the delete candidate is an internal node (it has two non-leaf children)
                // Then swap it's inorder predecessor/successor's value into the candidate node
                RedBlackNode swapNode = (RedBlackNode) this.getInOrderPredecessor(current);
                if (swapNode == null) {
                    swapNode = (RedBlackNode) this.getinOrderSucessor(current);
                }
                current.data = swapNode.data;

                // Now the problem is reduced to deleting a node that has at most one non-leaf child, the swapNode
                deleteOneChild(swapNode);
            } else {
                // Delete a node that has at most one non-leaf child
                deleteOneChild(current);
            }
        }
        else {
            System.err.println("Node with data  " + value + " not found. Nothing to delete.");
        }
    }

    /**
     * Retrieve a node's in-order predecessor.  That is, the maximal element in that node's left subtree
     *
     * @param n Node to find the in-order predecessor for
     * @return Node's in-order predecessor
     */
    private Node getInOrderPredecessor(RedBlackNode n) {
        Node current = n.left;
        while (current != null && current.right != LEAF) {
            current = current.right;
        }
        return current;
    }

    /**
     * Retrieve a node's in-order successor.  That is, the minimal element in that node's right subtree
     *
     * @param n Node to find the in-order sucessor for
     * @return Node's in-order successor
     */
    private Node getinOrderSucessor(RedBlackNode n) {
        Node current = n.right;
        while (current != null && current.left != LEAF) {
            current = current.left;
        }
        return current;
    }

    /**
     * Delete a node that has at most one non-leaf child
     *
     * @param toDelete Node to be deleted
     */
    private void deleteOneChild(RedBlackNode toDelete) {
        assert toDelete != LEAF;
        if (toDelete.left != LEAF) assert toDelete.right == LEAF : "deleteOneChild: attempting to delete node with more than one non-leaf child " + toDelete;
        if (toDelete.right != LEAF) assert toDelete.left == LEAF : "deleteOneChild: attempting to delete node with more than one non-leaf child " + toDelete;

        RedBlackNode child = toDelete.right == LEAF ? toDelete.left() : toDelete.right();

        // substitute child into toDelete's place in the tree
        Node parent = toDelete.parent;
        if (parent == null) {
            root = child != LEAF ? child : null;
        } else {
            assert toDelete == parent.left || toDelete == parent.right;
            if (toDelete == parent.left) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }
        child.parent = parent;

        if (toDelete.colour == RedBlackNode.Colour.BLACK) {
            if (child.colour == RedBlackNode.Colour.RED) {
                child.colour = RedBlackNode.Colour.BLACK;
            } else {
                // Some repairs are needed to maintain red-black tree properties
                deleteCase1(child);
            }
        }
    }

    /**
     * Handles case 1 of after-deletion repairs, where the given node is the root of the tree.
     *
     * @param n Child of the node that was deleted
     */
    private void deleteCase1(RedBlackNode n) {
        // If n is the root, then we're done and don't need to do anything
        if (n.parent != null) {
            deleteCase2(n);
        }
    }

    /**
     * Handles case 2 of after-deletion repairs, where the given node's sibling is red.
     *
     * @param n Child of the node that was deleted
     */
    private void deleteCase2(RedBlackNode n) {
        /*
            In this case, n's sibling is red
            Perform colour change and rotation necessary so that n has a black sibling
            and then go to the next step
        */
        RedBlackNode sibling = n.getSibling();
        if (sibling.colour == RedBlackNode.Colour.RED) {
            n.parent().colour = RedBlackNode.Colour.RED;
            sibling.colour = RedBlackNode.Colour.BLACK;
            rotate(n.parent, n == n.parent.left);
        }

        deleteCase3(n);
    }

    /**
     * Handles case 3 of after-deletion repairs, where the given node's children are all black.
     *
     * @param n Child of the node that was deleted
     */
    private void deleteCase3(RedBlackNode n) {
        /*
            If n, n's parent, and n's sibling's children are all black
            then colour n's sibling red and perform a rebalancing on n's parent
            starting at case 1.
            This rebalancing is a tail-recursive call that could reach the tree root in the worst case, giving
            this deletion an O(log n) time complexity.
        */

        RedBlackNode sibling = n.getSibling();

        if ((n.parent().colour == RedBlackNode.Colour.BLACK) &&
                (sibling.colour == RedBlackNode.Colour.BLACK) &&
                (sibling.left().colour == RedBlackNode.Colour.BLACK) &&
                (sibling.right().colour == RedBlackNode.Colour.BLACK)) {
            sibling.colour = RedBlackNode.Colour.RED;
            deleteCase1(n.parent());
        } else {
            deleteCase4(n);
        }
    }

    /**
     * Handles case 4 of after-deletion repairs, where the given node's sibling and sibling's children are black
     * but the parent is red
     *
     * @param n Child of the node that was deleted
     */
    private void deleteCase4(RedBlackNode n) {
        // n's sibling and sibling's children are black but n's parent is red
        // then swap the colours of the sibling and parent

        RedBlackNode sibling = n.getSibling();

        if ((n.parent().colour == RedBlackNode.Colour.RED) &&
                (sibling.colour == RedBlackNode.Colour.BLACK) &&
                (sibling.left().colour == RedBlackNode.Colour.BLACK) &&
                (sibling.right().colour == RedBlackNode.Colour.BLACK)) {
            sibling.colour = RedBlackNode.Colour.RED;
            n.parent().colour = RedBlackNode.Colour.BLACK;
        } else {
            deleteCase5(n);
        }
    }

    /**
     * Handles case 5 of after-deletion repairs, where the given node's sibling is black and the sibling's children
     * are red AND black.
     *
     * @param n Child of the node that was deleted
     */
    private void deleteCase5(RedBlackNode n) {
        /*
            Sibling is black, sibling's children are red and black
            Rotate so that n has a black sibling whose child towards the outside of the subtree at n's parent is red
            Then we can fall into case 6
          */
        RedBlackNode sibling = n.getSibling();

        if (sibling.colour == RedBlackNode.Colour.BLACK) {
            if ((n == n.parent.left) &&
                    (sibling.right().colour == RedBlackNode.Colour.BLACK) &&
                    sibling.left().colour == RedBlackNode.Colour.RED) {
                sibling.colour = RedBlackNode.Colour.RED;
                sibling.left().colour = RedBlackNode.Colour.BLACK;
                rotate(sibling, false);
            } else if ((n == n.parent.right) &&
                    (sibling.left().colour == RedBlackNode.Colour.BLACK) &&
                    (sibling.right().colour == RedBlackNode.Colour.RED)) {
                sibling.colour = RedBlackNode.Colour.RED;
                sibling.right().colour = RedBlackNode.Colour.BLACK;
                rotate(sibling, true);
            }
        }
        deleteCase6(n);
    }

    /**
     * Handles case 6 of after-deletion repairs, where the given node's sibling is black and the sibling's "outer" child
     * is red.
     *
     * @param n Child of the note that was deleted
     */
    private void deleteCase6(RedBlackNode n) {
        /*
            Sibling is black, sibling's child towards the "outside" of the tree is red.
            Then perform an inward rotation and recolour to restore red-black properties.
         */
        RedBlackNode sibling = n.getSibling();

        sibling.colour = n.parent().colour;
        n.parent().colour = RedBlackNode.Colour.BLACK;

        if (n == n.parent().left) {
            sibling.right().colour = RedBlackNode.Colour.BLACK;
            rotate(n.parent, true);
        } else {
            sibling.left().colour = RedBlackNode.Colour.BLACK;
            rotate(n.parent, false);
        }
    }

    /************ TESTING METHODS *******/

    /**
     * Validates the properties of a red-black tree
     */
    public void validate() {
        assert root != LEAF;
        validateRec(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Validates the properties of a red-black tree and returns the black-height of this tree
     *
     * @param root Root of the subtree to perform validation on
     * @param lowerBound Minimum value that this subtree's nodes may have that satisfies the binary search tree
     * @param higherBound Maximum value that this subtree's nodes may have that satisfies the binary search tree
     * @return The number of black nodes in all paths from root to the leaves, i.e. the black-height
     */
    private int validateRec(RedBlackNode root, int lowerBound, int higherBound) {
        if (root == null) { return 0; }
        if (root == LEAF) { return 1; }

        assert root.parent == null || root.parent.left == root || root.parent.right == root :
                String.format("validateRec: %s shows parent as %s but %s shows left %s and right %s",
                        root,
                        root.parent,
                        root.parent,
                        root.parent.left,
                        root.parent.right);

        // Binary search tree property check
        assert(root.data > lowerBound && root.data < higherBound);

        // Red-black properties checks
        // Property 1
        assert root.colour == RedBlackNode.Colour.BLACK || root.colour == RedBlackNode.Colour.RED;
        // Properties 2 and 3
        assert root != this.root && root != LEAF || (root.colour == RedBlackNode.Colour.BLACK);
        // Property 4
        if (root.colour == RedBlackNode.Colour.RED) {
            assert(root.left != null && root.left().colour == RedBlackNode.Colour.BLACK);
            assert(root.right != null && root.right().colour == RedBlackNode.Colour.BLACK);
        }
        // Property 5
        // Every path from a given node to any of its LEAF nodes must contain the same number of black nodes.
        int leftBlackHeight = validateRec(root.left(), lowerBound, root.data);
        int rightBlackHeight = validateRec(root.right(), root.data, higherBound);
        assert leftBlackHeight == rightBlackHeight :
            String.format("Non-matching black-heights: %s's left %s with black-height %s and right %s with black-height %s",
                    root,
                    root.left,
                    leftBlackHeight,
                    root.right,
                    rightBlackHeight);
        return leftBlackHeight + root.colour.getValue(); // BLACK nodes should have a value of 1
    }

    // Prints a representation of the tree, each line representing one level of the tree starting at the root.
    public void printTree() {
        if (root == null) {
            System.err.println("Tree is empty.  Nothing to print.");
            return;
        }
        // Breadth-first search to gather nodes at each level
        List<List<RedBlackNode>> levels = new ArrayList<>(1);
        Queue<RedBlackNode> toVisit = new LinkedList<>();
        toVisit.add(root);

        int size = 0;
        int level = 0;
        while (!toVisit.isEmpty()) {
            List<RedBlackNode> children = new ArrayList<>();
            while (!toVisit.isEmpty()) {
                RedBlackNode current = toVisit.remove();
                size += current == LEAF ? 0 : 1;
                // add this node to the current level
                if (levels.size() <= level) {
                    levels.add(level, new LinkedList<>());
                }
                levels.get(level).add(current);

                // add this node's children to the list to be traversed for the next level
                if (current.left != null) {
                    children.add(current.left());
                }
                if (current.right != null) {
                    children.add(current.right());
                }

            }
            toVisit.addAll(children);
            level++;
        }

        System.err.println("Printing tree with " + size + " nodes");
        for (int i = 0; i < levels.size(); i++) {
            System.err.println(levels.get(i).toString());

        }
    }

}
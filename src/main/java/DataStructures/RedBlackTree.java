package DataStructures;

import java.util.*;

public class RedBlackTree {
    public Node root;
    final static Node LEAF = new Node(null);

    /*************** GENERAL HELPERS **********************/

    private void rotate(Node lead, boolean toLeft) {
        assert lead != LEAF : "Trying to rotate on a LEAF as lead.";
        Node parent = lead.parent;
        Node centre = toLeft ? lead.right : lead.left;
        assert(centre != LEAF);

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

        centre.left = toLeft ? lead : centre.left;
        centre.right = toLeft ? centre.right : lead;
        centre.parent = parent;

        if (parent != null) {
            assert lead == parent.left || lead == parent.right :
                    String.format("rotate: Attempting rotate on %s but found inconsistent links with parent %s", lead, parent);
            if (lead == parent.left) {
                parent.left = centre;
            } else if (lead == parent.right) {
                parent.right = centre;
            }
        } else {
            this.root = centre;
        }

    }

    /************ INSERT METHODS *********************/

    Node insert(Node n) {
        if (insertRec(root, n)) {
            insertRepair(n);

            root = n;
            while (root.parent != null) {
                root = root.parent;
            }
            return root;
        }

        return null;
    }

    boolean insertRec(Node root, Node n) {
        if (root != null) {
            if (n.data == root.data) {
                return false;
            }
            else if (n.data > root.data) {
                if (root.right == LEAF) {
                    root.right = n;
                } else {
                    return insertRec(root.right, n);
                }
            } else if (n.data < root.data) {
                if (root.left == LEAF) {
                    root.left = n;
                } else {
                    return insertRec(root.left, n);
                }
            }
        }

        n.parent = root;
        n.left = LEAF;
        n.right = LEAF;
        n.colour = Node.Colour.RED;
        return true;
    }

    void insertRepair(Node n) {
        if (n.parent == null) {
            // case 1 - n is the root
            // make the assignment explicit, for the initial case
            root = n;
            n.colour = Node.Colour.BLACK;
        } else if (n.parent.colour == Node.Colour.BLACK) {
            // case 2 - n's parent is black
        } else if (n.getUncle().colour == Node.Colour.RED) {
            // case 3 - parent and uncle are red
            Node grandParent = n.getGrandParent();
            assert(grandParent != null && n.getUncle() != null);
            n.parent.colour = Node.Colour.BLACK;
            n.getUncle().colour = Node.Colour.BLACK;
            grandParent.colour = Node.Colour.RED;
            insertRepair(grandParent);
        } else {
            // case 4 - parent is red and uncle is black
            insertCase4(n);
        }
    }

    private void insertCase4(Node n) {
        Node grandParent = n.getGrandParent();

        // step 1 - if n is on the inside of the tree, rotate it to the outside
        if (n == grandParent.left.right) {
            rotate(n.parent, true);
            n = n.left;
        } else if (n == grandParent.right.left) {
            rotate(n.parent, false);
            n = n.right;
        }

        insertCase4Step2(n);
    }

    private void insertCase4Step2(Node n) {
        // step 2 - rotate grandparent to maintain tree balance
        Node parent = n.parent;
        Node grandParent = n.getGrandParent();
        if (n == parent.left) { rotate(grandParent, false); }
        else { rotate(grandParent, true); }

        parent.colour = Node.Colour.BLACK;
        grandParent.colour = Node.Colour.RED;
    }

    /************ DELETE METHODS *********/

    void delete(int value) {
        Node current = this.root;
        while (current != null && current != LEAF && value != current.data) {
            current = value > current.data ? current.right : current.left;
        }

        if (current != null && current != LEAF) {
            if (current.left != LEAF && current.right != LEAF) {
                Node swapNode = this.getInOrderPredecessor(current);
                if (swapNode == null) {
                    swapNode = this.getinOrderSucessor(current);
                }
                current.data = swapNode.data;
                deleteOneChild(swapNode);
            } else {
                deleteOneChild(current);
            }
        }
        else {
            System.out.println("Node with data  " + value + " not found. Nothing to delete.");
        }
    }

    Node getInOrderPredecessor(Node n) {
        // retrieve the maximal element in n's left subtree
        Node current = n.left;
        while (current != null && current.right != LEAF) {
            current = current.right;
        }
        return current;
    }

    Node getinOrderSucessor(Node n) {
        // retrieve the minimal element in n's right subtree
        Node current = n.right;
        while (current != null && current.left != LEAF) {
            current = current.left;
        }
        return current;
    }

    void deleteOneChild(Node toDelete) {
        // precondition: toDelete has at most one non-leaf child
        assert toDelete != LEAF;
        if (toDelete.left != LEAF) assert toDelete.right == LEAF : "deleteOneChild: attempting to delete node with more than one non-leaf child " + toDelete;
        if (toDelete.right != LEAF) assert toDelete.left == LEAF : "deleteOneChild: attempting to delete node with more than one non-leaf child " + toDelete;

        Node child = toDelete.right == LEAF ? toDelete.left : toDelete.right;

        // substitute child into toDelete's place in the tree
        Node parent = toDelete.parent;
        if (parent == null) {
            this.root = child != LEAF ? child : null;
        } else {
            assert toDelete == parent.left || toDelete == parent.right;
            if (toDelete == parent.left) {
                parent.left = child;
            } else if (toDelete == parent.right) {
                parent.right = child;
            }
        }
        child.parent = parent;

        if (toDelete.colour == Node.Colour.BLACK) {
            if (child.colour == Node.Colour.RED) {
                child.colour = Node.Colour.BLACK;
            } else {
                deleteCase1(child);
            }
        }
    }

    void deleteCase1(Node n) {
        // if n is the root, then we're done and don't need to do anything
        // otherwise....
        if (n.parent != null) {
            deleteCase2(n);
        }
    }

    void deleteCase2(Node n) {
        // In this case, n's sibling is red
        // Perform colour change and rotation necessary so that n has a black sibling
        // and then go to the next step
        Node sibling = n.getSibling();
        if (sibling.colour == Node.Colour.RED) {
            n.parent.colour = Node.Colour.RED;
            sibling.colour = Node.Colour.BLACK;
            rotate(n.parent, n == n.parent.left);
        }

        deleteCase3(n);
    }

    void deleteCase3(Node n) {
        // if n, n's parent, and n's sibling's children are all black
        // then colour n's sibling red and perform a rebalancing on n's parent
        // starting at case 1

        Node sibling = n.getSibling();

        if ((n.parent.colour == Node.Colour.BLACK) &&
                (sibling.colour == Node.Colour.BLACK) &&
                (sibling.left.colour == Node.Colour.BLACK) &&
                (sibling.right.colour == Node.Colour.BLACK)) {
            sibling.colour = Node.Colour.RED;
            deleteCase1(n.parent);
        } else {
            deleteCase4(n);
        }
    }

    void deleteCase4(Node n) {
        // n's sibling and sibling's children are black but n's parent is red
        // then swap the colours of the sibling and parent

        Node sibling = n.getSibling();

        if ((n.parent.colour == Node.Colour.RED) &&
                (sibling.colour == Node.Colour.BLACK) &&
                (sibling.left.colour == Node.Colour.BLACK) &&
                (sibling.right.colour == Node.Colour.BLACK)) {
            sibling.colour = Node.Colour.RED;
            n.parent.colour = Node.Colour.BLACK;
        } else {
            deleteCase5(n);
        }
    }

    void deleteCase5(Node n) {
        // Sibling is black, sibling's children are red and black
        // Rotate so that n has a black sibling whose child towards the outside of the subtree at n's parent is red
        // Then we can fall into case 6

        Node sibling = n.getSibling();

        if (sibling.colour == Node.Colour.BLACK) {
            if ((n == n.parent.left) &&
                    (sibling.right.colour == Node.Colour.BLACK) &&
                    sibling.left.colour == Node.Colour.RED) {
                sibling.colour = Node.Colour.RED;
                sibling.left.colour = Node.Colour.BLACK;
                rotate(sibling, false);
            } else if ((n == n.parent.right) &&
                    (sibling.left.colour == Node.Colour.BLACK) &&
                    (sibling.right.colour == Node.Colour.RED)) {
                sibling.colour = Node.Colour.RED;
                sibling.right.colour = Node.Colour.BLACK;
                rotate(sibling, true);
            }
        }
        deleteCase6(n);
    }

    void deleteCase6(Node n) {
        // sibling is black, sibling's child is red
        Node sibling = n.getSibling();

        sibling.colour = n.parent.colour;
        n.parent.colour = Node.Colour.BLACK;

        if (n == n.parent.left) {
            sibling.right.colour = Node.Colour.BLACK;
            rotate(n.parent, true);
        } else {
            sibling.left.colour = Node.Colour.BLACK;
            rotate(n.parent, false);
        }
    }

    /************ TESTING METHODS *******/

    void validate() {
        assert this.root != LEAF;
        validateRec(this.root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    // validates the properties of a RedBlackTree and returns the black-height of this tree
    int validateRec(Node root, int lowerBound, int higherBound) {
        if (root == null) { return 0; }
        if (root == LEAF) { return 1; }

        if (root.parent != null) {
            assert root.parent.left == root || root.parent.right == root :
                    String.format("validateRec: %s shows parent as %s but %s shows left %s and right %s",
                            root,
                            root.parent,
                            root.parent,
                            root.parent.left,
                            root.parent.right);
        }

        // Binary search tree property check
        assert(root.data > lowerBound && root.data < higherBound);

        // RedBlack properties checks
        assert(root.colour != null);
        if (root == this.root || root == LEAF) { assert(root.colour == Node.Colour.BLACK); }
        if (root.colour == Node.Colour.RED) {
            assert(root.left != null && root.left.colour == Node.Colour.BLACK);
            assert(root.right != null && root.right.colour == Node.Colour.BLACK);
        }
        // Every path from a given node to any of its LEAF nodes must contain the same number of black nodes.
        int leftBlackHeight = validateRec(root.left, lowerBound, root.data);
        int rightBlackHeight = validateRec(root.right, root.data, higherBound);
        assert leftBlackHeight == rightBlackHeight :
            String.format("Non-matching black-heights: %s's left %s with black-height %s and right %s with black-height %s",
                    root,
                    root.left,
                    leftBlackHeight,
                    root.right,
                    rightBlackHeight);
        return leftBlackHeight + root.colour.getValue(); // BLACK nodes should have a value of 1
    }



    void printTree() {
        if (this.root == null) {
            System.out.println("Tree is empty.  Nothing to print.");
            return;
        }
        // breadth-first search and print
        List<List<Node>> levels = new ArrayList<>(1);
        Queue<Node> toVisit = new LinkedList<>();
        toVisit.add(root);

        int size = 0;
        int level = 0;
        while (!toVisit.isEmpty()) {
            List<Node> children = new ArrayList<>();
            while (!toVisit.isEmpty()) {
                Node current = toVisit.remove();
                size += current == LEAF ? 0 : 1;
                // add this node to the current level
                if (levels.size() <= level) {
                    levels.add(level, new LinkedList<>());
                }
                levels.get(level).add(current);

                // add this node's children to the list to be traversed for the next level
                if (current.left != null) {
                    children.add(current.left);
                }
                if (current.right != null) {
                    children.add(current.right);
                }

            }
            toVisit.addAll(children);
            level++;
        }

        System.out.println("Printing tree with " + size + " nodes");
        for (int i = 0; i < levels.size(); i++) {
            // System.out.print(String.join("", Collections.nCopies(75 - i*8, " ")));
            System.out.println(levels.get(i).toString());

        }
    }

}
package DataStructures;

import java.util.*;

public class RedBlackTree {
    public Node root;
    final static Node LEAF = new Node(null);

    /*************** GENERAL HELPERS **********************/

    private void rotate(Node lead, boolean toLeft) {
        Node parent = lead.parent;
        Node centre = toLeft ? lead.right : lead.left;
        assert(centre != LEAF);

        if (toLeft) {
            lead.right = centre.left;
            if (centre.left != null) {
                centre.left.parent = lead;
            }
        } else {
            lead.left = centre.right;
            if (centre.right != null) {
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
        System.out.println("Printing tree...");
        // breadth-first search and print
        List<List<Node>> levels = new ArrayList<>(1);
        Queue<Node> toVisit = new LinkedList<>();
        toVisit.add(root);

        int level = 0;
        while (!toVisit.isEmpty()) {
            List<Node> children = new ArrayList<>();
            while (!toVisit.isEmpty()) {
                Node current = toVisit.remove();
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

        for (int i = 0; i < levels.size(); i++) {
            // System.out.print(String.join("", Collections.nCopies(75 - i*8, " ")));
            System.out.println(levels.get(i).toString());

        }
    }

}
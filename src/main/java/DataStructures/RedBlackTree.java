package DataStructures;

import java.util.*;

public class RedBlackTree {
    public Node root;
    final static Node LEAF = new Node(null);

    /*************** GENERAL HELPERS **********************/

    private void rotateLeft(Node n) {
        Node parent = n.parent;

        Node centre = n.right;
        assert(centre != LEAF);
        n.right = centre.left;
        centre.left = n;
        centre.parent = n.parent;
        n.parent = centre;

        if (parent != null) {
            if (n == parent.left) {
                parent.left = centre;
            } else if (n == parent.right) {
                parent.right = centre;
            }
        }
    }

    private void rotateRight(Node n) {
        Node parent = n.parent;

        Node centre = n.left;
        assert(centre != LEAF);
        n.left = centre.right;
        centre.right = n;
        centre.parent = n.parent;
        n.parent = centre;

        if (parent != null) {
            if (n == parent.left) {
                parent.left = centre;
            } else if (n == parent.right) {
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
            rotateLeft(n.parent);
            n = n.left;
        } else if (n == grandParent.right.left) {
            rotateRight(n.parent);
            n = n.right;
        }

        insertCase4Step2(n);
    }

    private void insertCase4Step2(Node n) {
        // step 2 - rotate grandparent to maintain tree balance
        Node parent = n.parent;
        Node grandParent = n.getGrandParent();
        if (n == parent.left) {
            rotateRight(grandParent);
        } else {
            rotateLeft(grandParent);
        }
        parent.colour = Node.Colour.BLACK;
        grandParent.colour = Node.Colour.RED;
    }

    /************ TESTING METHODS *******/

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
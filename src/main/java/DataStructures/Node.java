package DataStructures;

public abstract class Node {
    protected Node parent;
    protected Integer data;
    protected Node left;
    protected Node right;

    Node() {

    }

    Node(Integer data) {
        this.data = data;
    }

    protected Node getGrandParent() {
        return parent == null ? null : parent.parent;
    }

    protected Node getSibling() {
        if (parent == null) {
            return null;
        }
        assert(this == parent.left || this == parent.right);
        return this == parent.left ? parent.right : parent.left;
    }

    protected Node getUncle() {
        Node grandParent = getGrandParent();
        if (grandParent == null) {
            return null;
        }
        return this.parent == grandParent.left ? grandParent.right : grandParent.left;
    }
}

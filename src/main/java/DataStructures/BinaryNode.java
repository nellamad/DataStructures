package DataStructures;

public abstract class BinaryNode {
    protected BinaryNode parent;
    protected Integer data;
    protected BinaryNode left;
    protected BinaryNode right;

    BinaryNode() {

    }

    BinaryNode(Integer data) {
        this.data = data;
    }

    protected BinaryNode getGrandParent() {
        return parent == null ? null : parent.parent;
    }

    protected BinaryNode getSibling() {
        if (parent == null) {
            return null;
        }
        assert(this == parent.left || this == parent.right);
        return this == parent.left ? parent.right : parent.left;
    }

    protected BinaryNode getUncle() {
        BinaryNode grandParent = getGrandParent();
        if (grandParent == null) {
            return null;
        }
        return this.parent == grandParent.left ? grandParent.right : grandParent.left;
    }
}

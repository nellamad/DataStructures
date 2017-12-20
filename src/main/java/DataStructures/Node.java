package DataStructures;

public class Node {
    Node parent;
    Integer data;
    Node left;
    Node right;
    Colour colour = Colour.BLACK;

    enum Colour {
        RED(0),
        BLACK(1);

        private int value;

        Colour(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    Node(Integer data) {
        this.data = data;
    }

    Node getGrandParent() {
        return this.parent == null ? null : this.parent.parent;
    }

    public Node getSibling() {
        if (parent == null) {
            return null;
        }
        assert(this == parent.left || this == parent.right);
        return this == parent.left ? parent.right : parent.left;
    }

    public Node getUncle() {
        Node grandParent = getGrandParent();
        if (grandParent == null) {
            return null;
        }
        return this.parent == grandParent.left ? grandParent.right : grandParent.left;
    }

    @Override
    public String toString() {
        return data != null ? String.format("%s(%s)", data.toString(), colour): "LEAF";
    }
}

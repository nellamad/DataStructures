package DataStructures;

public class RedBlackNode extends BinaryNode {
    Colour colour;

    RedBlackNode() {
        super();
        this.colour = Colour.BLACK;
    }

    RedBlackNode(int data) {
        super(data);
        this.colour = Colour.BLACK;
    }

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

    public RedBlackNode parent() {
        return (RedBlackNode) parent;
    }

    public RedBlackNode left() {
        return (RedBlackNode) left;
    }

    public RedBlackNode right() {
        return (RedBlackNode) right;
    }

    @Override
    public RedBlackNode getGrandParent() {
        return (RedBlackNode) super.getGrandParent();
    }

    @Override
    public RedBlackNode getSibling() {
        return (RedBlackNode) super.getSibling();
    }

    @Override
    public RedBlackNode getUncle() {
        return (RedBlackNode) super.getUncle();
    }

    @Override
    public String toString() {
        return data != null ? String.format("%s(%s)", data.toString(), colour): "LEAF";
    }
}

public class Tree {

    private Node root;
    private Node end;

    public Tree() {
        this.root = new Node();
    }

    public Node getRoot() {
        return this.root;
    }

    public void add (String id) {
        Node currentNode = this.root;
        for (int i = 0; i < id.length(); i++) {
            Character c = id.charAt(i);
            Node child = currentNode.getChild(c);
            if (child == null) {
                child = currentNode.addChild(c);
            }
            currentNode = child;
        }
        Node finalNode = currentNode.addChild('$');
        finalNode.setFinalNode();
    }

    public void print() {
        this.root.print();
    }
}

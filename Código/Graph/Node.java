import java.util.HashMap;
import java.util.Map;

import java.util.Iterator;

public class Node {

    HashMap<Character, Node> children = new HashMap<Character, Node>();
    Character character;
    Boolean isFinal;

    public Node() {
        this.isFinal = false;
        this.character = '+';
    }

    public Node( Character c ) {
        this.isFinal = false;
        this.character = c;
    }

    public Node getChild( Character c ) {
        return children.get(c);
    }

    public Node addChild( Character c ) {
        Node child = new Node(c);
        children.put(c, child);
        return child;
    }

    public Boolean hasFinalChild() {
        Iterator entries = children.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Node child = (Node)entry.getValue();
            if (child.isFinal())
                return true;
        }
        return false;

    }

    public Boolean isFinal() {
        return this.isFinal;
    }

    public void setFinalNode() {
        this.isFinal = true;
    }

    public void print() {
        System.out.println("\n");
        print("", true);
        System.out.println("\n");
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + this.character);

        Iterator entries = children.entrySet().iterator();

        Node lastChild = null;

        if (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            lastChild = (Node)entry.getValue();
        }
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Node child = (Node)entry.getValue();
            child.print(prefix + (isTail ? "    " : "│   "), false);
        }
        if (lastChild != null) {
            lastChild.print(prefix + (isTail ?"    " : "│   "), true);
        }
    }

    public void printRec( int level ) {
        for (int i = 0; i < level; i++) System.out.print("--");
        System.out.println(this.character);
        for (Map.Entry<Character, Node> entry : children.entrySet())
            entry.getValue().printRec(level + 1);
    }

}

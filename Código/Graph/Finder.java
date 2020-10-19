import java.io.*;
import java.util.ArrayList;

public class Finder {

    private BufferedReader reader;
    private Tree tree;
    private ArrayList<String> ids = new ArrayList<String>();

    public Finder( BufferedReader reader, Tree tree ) {
        this.reader = reader;
        this.tree = tree;
    }

    private Boolean isBeginning( String line, Integer index ) {
        if (index == 0)
            return true;
        Character previous = line.charAt(index - 1);
        if (previous == ' ' || previous == '\t')
            return true;
        return false;
    }

    private Boolean isEnding( String line, Integer index ) {
        if (index == line.length())
            return true;
        Character c = line.charAt(index);
        if (c == ' '
        || c == '\t'
        || c == ','
        || c == '.'
        || c == ':'
        || c == ';'
        || c == '!'
        || c == '?'
        || c == '/'
        || c == '(')
            return true;
        return false;
    }

    private Integer findInTree( String line, Integer index) {
        Node currentNode = tree.getRoot();
        Integer currentIndex = index;
        while (currentIndex < line.length()) {
            Character character = line.charAt(currentIndex);
            Node child = currentNode.getChild(character);
            if (child == null) {
                if (currentNode.hasFinalChild() && isEnding(line, currentIndex)) {
                    ids.add(line.substring(index, currentIndex));
                    return currentIndex;
                }
                else {
                    break;
                }
            }
            currentNode = child;
            currentIndex = currentIndex + 1;
        }
        return -1;
    }

    public void find() throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                if (isBeginning(line, i)) {
                    Integer index = findInTree(line, i);
                    if (index != -1) {
                        i = index;
                    }
                }
            }
        }
    }

    public ArrayList<String> getLinks() {
        return this.ids;
    }
}

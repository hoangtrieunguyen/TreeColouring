import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MerkleTree {
    private Node root;
    private String[] transactions;
    private int height;
    private int[] allColours;
    private int[] colourSequence;

    public MerkleTree(String[] transactions) {
        this.transactions = transactions;
        createNodes(transactions);

        this.allColours = new int[this.height];
        for (int i = 0; i < this.allColours.length; i++)
            this.allColours[i] = i;
    }

    public void createNodes(String[] trans) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < trans.length; i++)
            nodes.add(new Node(trans[i], null, null));

        if (trans.length % 2 != 0)
            nodes.add(new Node(trans[trans.length - 1], null, null)); // Duplicate of the last node

        createNodesRecursive(nodes);
    }

    public void createNodesRecursive(List<Node> nodes) {
        if (nodes.size() == 1) {
            root = nodes.get(0);
            return;
        }

        List<Node> parents = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += 2) {
            Node parent = new Node(nodes.get(i).getValue().concat(nodes.get(i + 1).getValue()), nodes.get(i), nodes.get(i + 1));
            nodes.get(i).setParent(parent);
            nodes.get(i + 1).setParent(parent);
            parents.add(parent);
        }

        if (parents.size() > 1 && parents.size() % 2 != 0) {
            Node parent = new Node(nodes.get(nodes.size() - 1).getValue().concat(nodes.get(nodes.size() - 1).getValue()), null, null); // Duplicate of the last node, has no child
            parents.add(parent);
        }

        this.height++;
        createNodesRecursive(parents);
    }

    public void print() {
        System.out.println("Tree height: " + this.height);
        printRecursive(root);
    }

    public void printRecursive (Node current) {
        if (current == null)
            return;

        System.out.println(current.getValue() + "::" + current.getColourGroup());
        printRecursive(current.getLeft());
        printRecursive(current.getRight());
    }

    public void colour(int[] colourSequence) {
        if (colourSequence.length != this.allColours.length) {
            System.out.println("Invalid colour sequence.");
            System.exit(2);
        }

        this.colourSequence = colourSequence;
        int[] avaiColours = this.allColours.clone();
        colourRecursive(root, 0, avaiColours);
    }

    public void colourRecursive(Node current, int h, int[] avaiColours) {
        if (current == null)
            return;

        if (h > 0) {
            // Always pick the first colour (colour with smallest number)
            int selectColourIdx;
            if (current.getLeft() == null & h < height) {
                selectColourIdx = minColourSequenceIndex(allColours);
            } else {
                selectColourIdx = minColourSequenceIndex(avaiColours);
            }
            current.setColourGroup(selectColourIdx);
            this.colourSequence[selectColourIdx]--;
            int[] tempAvaiColours = avaiColours;
            int tempSelectColourIdx = selectColourIdx;
            avaiColours = IntStream.range(0, avaiColours.length).filter(i -> tempAvaiColours[i] != tempSelectColourIdx).map(i -> tempAvaiColours[i]).toArray();
        }

        h++;
        colourRecursive(current.getLeft(), h, avaiColours.clone());
        colourRecursive(current.getRight(), h, avaiColours.clone());
    }

    public int minColourSequenceIndex(int[] avaiColours) {
        int minIdx = 0;

        for (int i = 0; i < avaiColours.length; i++) {
            if (colourSequence[avaiColours[i]] < colourSequence[avaiColours[minIdx]] && colourSequence[avaiColours[i]] > 0)
                minIdx = i;
        }

        // If this happens, then the algorithm cannot colour the tree using the given colour sequence
        if (colourSequence[avaiColours[minIdx]] == 0) {
            System.out.println("Cannot colour");
            System.exit(3);
        }

        return avaiColours[minIdx];
    }

    public String[] getTransactions() {
        return transactions;
    }

    public void setTransactions(String[] transactions) {
        this.transactions = transactions;
    }

    public int[] getColourSequence() {
        return colourSequence;
    }

    public void setColourSequence(int[] colourSequence) {
        this.colourSequence = colourSequence;
    }
}

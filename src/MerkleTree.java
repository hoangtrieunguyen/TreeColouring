import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private Node root;
    private String[] transactions;

    public MerkleTree(String[] transactions) {
        this.transactions = transactions;
        createNodes(transactions);
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

        createNodesRecursive(parents);
    }

    public void print() {
        printRecursive(root);
    }

    public void printRecursive (Node current) {
        if (current == null)
            return;

        System.out.println(current.getValue());
        printRecursive(current.getLeft());
        printRecursive(current.getRight());
    }
}

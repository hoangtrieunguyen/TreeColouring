import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    private Node root;
    private String[] transactions;
    private int height;

    public MerkleTree(String[] transactions) {
        this.transactions = transactions;
        this.height = (int)Math.ceil(Math.log(transactions.length) / Math.log(2));
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

    public String[] getTransactions() {
        return transactions;
    }

    public void setTransactions(String[] transactions) {
        this.transactions = transactions;
    }

    public void colourSplitting(int[] colourSequence) {
        List<Colour> sequence = new ArrayList<>();
        for (int i = 0; i < colourSequence.length; i++)
            sequence.add(new Colour(i, colourSequence[i]));

        int bottomNodes = this.transactions.length;
        if (bottomNodes % 2 != 0) // The tree will automatically replicate the last transaction when applicable, but the initial transaction length remains the same
            bottomNodes += 1;
        colourSplittingRecursive(this.root, this.height, sequence, bottomNodes);
        System.out.println("Coloured successfully.");
    }

    public void colourSplittingRecursive(Node node, int height, List<Colour> sequence, int bottomNodes) {
        if (height >= 1) {
            Node left = node.getLeft();
            Node right = node.getRight();
            if (left != null && right != null) {
                Colour c1 = sequence.get(0);
                if (c1.getCount() == 2) {
                    left.setColourGroup(c1.getColourCode());
                    right.setColourGroup(c1.getColourCode());
                } else if (c1.getCount() == 1) { // Imperfect case
                    left.setColourGroup(c1.getColourCode());
                    right.setColourGroup(sequence.get(sequence.size() - 1).getColourCode()); // get the last colour (highest number colour)
                } else {
                    left.setColourGroup(c1.getColourCode());
                    right.setColourGroup(sequence.get(1).getColourCode()); // second colour
                }

                if (height >= 2) {
                    List<Colour>[] sequences = feasibleSplit(height, sequence, bottomNodes);
                    if (sequences == null)
                        return;
                    int leftBottomNodes = Utility.getLeftBottomNodes(height, bottomNodes);
                    int rightBottomNodes = bottomNodes - leftBottomNodes;

                    colourSplittingRecursive(left, height - 1, sequences[0], leftBottomNodes);
                    colourSplittingRecursive(right, height - 1, sequences[1], rightBottomNodes);
                }
            }
        }
    }

    public List<Colour>[] feasibleSplit(int height, List<Colour> sequence, int bottomNodes) {
        Colour c1 = sequence.get(0);
        Colour c2 = sequence.get(1);
        List<Colour> sequenceA = new ArrayList<>();
        List<Colour> sequenceB = new ArrayList<>();
        int rightBottomNodes = Utility.getRightBottomNodes(height, bottomNodes);
        boolean noRedistribution = false;
        if (rightBottomNodes == 0) {
            noRedistribution = true;
            for (int i = 1; i < height; i++) {
                Colour c = sequence.get(i);
                if (c1.getCount() == 1) {
                    if (i == height - 1) // last colour
                        sequenceA.add(new Colour(c.getColourCode(), c.getCount() - 1));
                    else
                        sequenceA.add(new Colour(c.getColourCode(), c.getCount()));
                } else if (c1.getCount() == 2) {
                    sequenceA.add(new Colour(c.getColourCode(), c.getCount()));
                } else {
                    if (i == 1) // second colour
                        sequenceA.add(new Colour(c.getColourCode(), c.getCount() - 1));
                    else
                        sequenceA.add(new Colour(c.getColourCode(), c.getCount()));
                }
            }
        } else if (c1.getCount() == 2) {
            int sumA = 0;
            int sumB = 0;
            for (int i = 1; i < height; i++) {
                Colour c = sequence.get(i);
                int a;
                int b;
                if (sumA < sumB) {
                    a = (int)Math.ceil(c.getCount() / 2.0);
                    b = (int)Math.floor(c.getCount() / 2.0);
                } else {
                    a = (int)Math.floor(c.getCount() / 2.0);
                    b = (int)Math.ceil(c.getCount() / 2.0);
                }
                sumA += a;
                sumB += b;
                sequenceA.add(new Colour(c.getColourCode(), a));
                sequenceB.add(new Colour(c.getColourCode(), b));
            }
        } else if (c1.getCount() == 1) {// Imperfect only case
            noRedistribution = true;
            for (int i = 1; i < height; i++) {
                Colour c = sequence.get(i);
                if (i == height - 1) // last colour
                    sequenceA.add(new Colour(c.getColourCode(), c.getCount() - 1));
                else
                    sequenceA.add(new Colour(c.getColourCode(), c.getCount()));
            }
        } else {
            int a2 = c2.getCount() - 1;
            int b2 = c1.getCount() - 1;
            sequenceA.add(new Colour(c2.getColourCode(), a2));
            sequenceB.add(new Colour(c1.getColourCode(), b2));
            if (height >= 3) {
                Colour c3 = sequence.get(2);
                int a3 = (int)Math.ceil((c3.getCount() + c1.getCount() - c2.getCount()) / 2.0);
                int b3 = c2.getCount() - c1.getCount() + (int)Math.floor((c3.getCount() + c1.getCount() - c2.getCount()) / 2.0);
                sequenceA.add(new Colour(c3.getColourCode(), a3));
                sequenceB.add(new Colour(c3.getColourCode(), b3));
                int sumA = a2 + a3;
                int sumB = b2 + b3;
                for (int i = 3; i < height; i++) {
                    Colour ci = sequence.get(i);
                    int ai;
                    int bi;
                    if (sumA < sumB) {
                        ai = (int)Math.ceil(ci.getCount() / 2.0);
                        bi = (int)Math.floor(ci.getCount() / 2.0);
                    } else {
                        ai = (int)Math.floor(ci.getCount() / 2.0);
                        bi = (int)Math.ceil(ci.getCount() / 2.0);
                    }
                    sumA += ai;
                    sumB += bi;
                    sequenceA.add(new Colour(ci.getColourCode(), ai));
                    sequenceB.add(new Colour(ci.getColourCode(), bi));
                }
            }
        }

        List<Colour>[] result = new List[2];
        Collections.sort(sequenceA);
        Collections.sort(sequenceB);
        result[0] = sequenceA;
        result[1] = sequenceB;

        boolean isPerfectTree = Utility.isPerfectTree(height, bottomNodes);
        if (!isPerfectTree && !noRedistribution) { // If this is an imperfect tree, then redistribute the colour sequences
            return redistributeSequences(height, bottomNodes, result);
        }
        return result;
    }

    /* Validate left sequence against C1 until finding the index of invalid colour, then redistribute from index 0 to that index
    Sort and validate against feasible colour sequence after each redistribution */
    public List<Colour>[] redistributeSequences(int height, int bottomNodes, List<Colour>[] sequences) {
        int childHeight = height - 1;
        int leftBottomNodes = Utility.getLeftBottomNodes(height, bottomNodes);
        int rightBottomNodes = bottomNodes - leftBottomNodes;
        int leftTotalNodes = Utility.calculateTreeNodes(leftBottomNodes, childHeight);
        int totalRedistributionRequired = leftTotalNodes - sequences[0].stream().mapToInt(c -> c.getCount()).sum(); // total left node - total colour in left sequence
        for (int i = 0; i < totalRedistributionRequired; i++) {
            int invalidIndex = findC1InvalidIndex(sequences[0], leftBottomNodes, childHeight);
            if (invalidIndex == -1) {
                System.out.println("Something is wrong!");
                //System.exit(2);
                return null;
            }
            // Take any possible colour from index 0 to invalidIndex from the right sequence
            List<Integer> requiredColour = new ArrayList<>();
            for (int j = 0; j <= invalidIndex; j++)
                requiredColour.add(sequences[0].get(j).getColourCode());

            Colour s1Colour = null;
            Colour s2Colour = null;
            for (int k = sequences[1].size() - 1; k >= 0; k--) { // Loop in reverse order to pick up the largest count colour, otherwise picking up from the smallest can break the 2nd sequence
                Colour colour = sequences[1].get(k);
                int minimumS2ValidColour = Utility.getRequiredNodesAtDepth(rightBottomNodes, childHeight, sequences[1].indexOf(colour) + 1);
                if (requiredColour.contains(colour.getColourCode()) && colour.getCount() > minimumS2ValidColour) {
                    s1Colour = sequences[0].stream().filter(c -> c.getColourCode() == colour.getColourCode()).findFirst().get();
                    s2Colour = colour;
                    break;
                }
            }

            if (s1Colour == null) {
                System.out.println("Cannot find suitable colour for redistribution!");
                //System.exit(2);
                return null;
            }
            s1Colour.setCount(s1Colour.getCount() + 1); // Increase 1 in sequence 1 and decrease 1 in sequence 2
            s2Colour.setCount(s2Colour.getCount() - 1);
            Collections.sort(sequences[0]);
            Collections.sort(sequences[1]);
        }
        int[] tempS1 = sequences[0].stream().mapToInt(c -> c.getCount()).toArray();
        int[] tempS2 = sequences[1].stream().mapToInt(c -> c.getCount()).toArray();
        boolean isValidS1 = Utility.isValidColourSequence(tempS1, leftBottomNodes, childHeight);
        boolean isValidS2 = Utility.isValidColourSequence(tempS2, rightBottomNodes, childHeight);
        if (!(isValidS1 && isValidS2)) {
            System.out.println("Invalid redistribution!!");
            //System.exit(2);
            return null;
        }
        return sequences;
    }

    public void validateTreeColouring() {
        int[] sequence = new int[this.height + 1];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = -2;
        }
        validateTreeColouringRecursive(this.root, 0, sequence);
    }

    public void validateTreeColouringRecursive(Node current, int height, int[] sequence) {
        if (current == null)
            return;
        if (Arrays.stream(sequence).anyMatch(c -> c == current.getColourGroup())) {
            System.out.println("Invalid colouring!");
            System.exit(2);
        }
        sequence[height] = current.getColourGroup();
        validateTreeColouringRecursive(current.getLeft(), height + 1, sequence.clone());
        validateTreeColouringRecursive(current.getRight(), height + 1, sequence.clone());
    }

    public int findC1InvalidIndex(List<Colour> sequence, int t, int h) {
        int requiredSum = 0;
        int actualSum = 0;
        for (int i = 1; i <= h; i++ ) {
            int requiredNodes = Utility.getRequiredNodesAtDepth(t, h, i);
            requiredSum += requiredNodes;
            actualSum += sequence.get(i - 1).getCount();
            if (actualSum < requiredSum) {
                return i - 1;
            }
        }
        return -1; // -1 means no invalid index
    }
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    private Node root;
    private String[] transactions;
    private int height;
    private List<Colour> currentSequence; // For testing purpose

    public MerkleTree(String[] transactions) {
        this.transactions = transactions;
        this.height = (int)Math.ceil(Math.log(transactions.length) / Math.log(2));
        createNodes(transactions);
    }

    public void createNodes(String[] transactions) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < transactions.length; i++)
            nodes.add(new Node(transactions[i], null, null));
        if (transactions.length % 2 != 0)
            nodes.add(new Node(transactions[transactions.length - 1], null, null)); // Duplicate of the last node
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
        if (Arrays.stream(sequence).anyMatch(c -> c == current.getColourGroup()) || (current != this.root && current.getColourGroup() == -1)) {
            System.out.println("Invalid colouring! Transaction: " + this.transactions.length);
            Utility.printSequence(this.currentSequence);
            return;
        }
        sequence[height] = current.getColourGroup();
        validateTreeColouringRecursive(current.getLeft(), height + 1, sequence.clone());
        validateTreeColouringRecursive(current.getRight(), height + 1, sequence.clone());
    }

    public void colourSplitting(int[] seq) {
        List<Colour> sequence = new ArrayList<>();
        for (int i = 0; i < seq.length; i++)
            sequence.add(new Colour(i, seq[i]));
        this.currentSequence = sequence;

        int bottomNodes = this.transactions.length;
        colourSplittingRecursive(this.root, this.height, sequence, bottomNodes);
    }

    public void colourSplittingRecursive(Node node, int h, List<Colour> seq, int t) {
        if (h >= 1) {
            Node left = node.getLeft();
            Node right = node.getRight();
            if (left != null && right != null) {
                Colour c1 = seq.get(0);
                if (c1.getCount() == 2) {
                    left.setColourGroup(c1.getColourCode());
                    right.setColourGroup(c1.getColourCode());
                } else if (c1.getCount() == 1) { // Imperfect case
                    int maxIdx = Utility.getMaxAvailableColourIndex(seq, t, h);
                    left.setColourGroup(c1.getColourCode());
                    right.setColourGroup(seq.get(maxIdx).getColourCode()); // get the colour-able colour with at highest position (highest index)
                } else {
                    left.setColourGroup(c1.getColourCode());
                    right.setColourGroup(seq.get(1).getColourCode()); // second colour
                }

                if (h >= 2) {
                    List<Colour>[] seqs = null;
                    if (Utility.isPerfectTree(h, t))
                        seqs = feasibleSplit(h, seq);
                    else
                        seqs = imperfectSplit(h, seq, t);

                    if (seqs == null) {
                        System.out.println("Cannot split correctly.");
                        Utility.printSequence(this.currentSequence);
                        System.exit(2);
                    }

                    int leftBottomNodes = Utility.getLeftBottomNodes(h, t);
                    int rightBottomNodes = t - leftBottomNodes;

                    List<Colour> seqA = seqs[0];
                    List<Colour> seqB = seqs[1];
                    int[] tempSeqA = seqA.stream().mapToInt(e -> e.getCount()).toArray();
                    int[] tempSeqB = seqB.stream().mapToInt(e -> e.getCount()).toArray();
                    boolean isValidSA = Utility.isValidSequenceExceptC5(tempSeqA, leftBottomNodes, h - 1);
                    boolean isValidSB = (rightBottomNodes > 0) ? Utility.isValidSequenceExceptC5(tempSeqB, rightBottomNodes, h - 1) : tempSeqB.length == 0;
                    if (!(isValidSA && isValidSB)) {
                        System.out.println("Invalid splitting!");
                        Utility.printSequence(this.currentSequence);
                        System.out.println("N: " + this.transactions.length);
                        System.exit(2);
                    }

                    colourSplittingRecursive(left, h - 1, seqs[0], leftBottomNodes);
                    colourSplittingRecursive(right, h - 1, seqs[1], rightBottomNodes);
                }
            }
        }
    }

    public List<Colour>[] feasibleSplit(int h, List<Colour> seq) {
        Colour c1 = seq.get(0);
        Colour c2 = seq.get(1);
        List<Colour> sequenceA = new ArrayList<>();
        List<Colour> sequenceB = new ArrayList<>();
        if (c1.getCount() == 2) {
            int sumA = 0;
            int sumB = 0;
            for (int i = 1; i < h; i++) {
                Colour c = seq.get(i);
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
        } else {
            int a2 = c2.getCount() - 1;
            int b2 = c1.getCount() - 1;
            sequenceA.add(new Colour(c2.getColourCode(), a2));
            sequenceB.add(new Colour(c1.getColourCode(), b2));
            if (h >= 3) {
                Colour c3 = seq.get(2);
                int a3 = (int)Math.ceil((c3.getCount() + c1.getCount() - c2.getCount()) / 2.0);
                int b3 = c2.getCount() - c1.getCount() + (int)Math.floor((c3.getCount() + c1.getCount() - c2.getCount()) / 2.0);
                sequenceA.add(new Colour(c3.getColourCode(), a3));
                sequenceB.add(new Colour(c3.getColourCode(), b3));
                int sumA = a2 + a3;
                int sumB = b2 + b3;
                for (int i = 3; i < h; i++) {
                    Colour ci = seq.get(i);
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
        Collections.sort(sequenceA);
        Collections.sort(sequenceB);

        List<Colour>[] result = new List[2];
        result[0] = sequenceA;
        result[1] = sequenceB;
        return result;
    }

    public List<Colour>[] imperfectSplit(int h, List<Colour> seq, int t) {
        Colour c1 = seq.get(0);
        Colour c2 = seq.get(1);
        List<Colour> sequenceA = new ArrayList<>();
        List<Colour> sequenceB = new ArrayList<>();
        int childH = h - 1;
        int leftBottomNodes = Utility.getLeftBottomNodes(h, t);
        int rightBottomNodes = t - leftBottomNodes;
        if (rightBottomNodes == 0) { // When rightBottomNodes equals to 0, do not need to split, just reduce the colour(s) has been used to colour the current layer. Then add everything to the left branch sequence
            if (c1.getCount() == 1) {
                int maxIdx = Utility.getMaxAvailableColourIndex(seq, t, h);
                for (int i = 1; i < h; i++) {
                    if (i == maxIdx)
                        sequenceA.add(new Colour(seq.get(i).getColourCode(), seq.get(i).getCount() - 1));
                    else
                        sequenceA.add(new Colour(seq.get(i).getColourCode(), seq.get(i).getCount()));
                }
            } else if (c1.getCount() == 2) {
                for (int i = 1; i < h; i++) {
                    sequenceA.add(new Colour(seq.get(i).getColourCode(), seq.get(i).getCount()));
                }
            } else {
                System.out.println("This case does not suppose to be happened.");
                return null;
            }
        } else { // When rightBottomNodes is greater than 0, left branch is always a perfect tree, and right branch is always an imperfect tree
            if (c1.getCount() == 1) { // If c1 == 1, then minus 1 from the other selected colour. The rest is similar to c1 == 2
                int maxIdx = Utility.getMaxAvailableColourIndex(seq, t, h);
                int sumB = 0;
                int missing = 0; // Use to prevent the last colour will be over-picked, if there is an invalid colour in the middle, we should aware of that and redistribute later instead of over-picking and did not trigger the redistribution => create an invalid sequence
                for (int i = 1; i < h; i++) {
                    Colour c = seq.get(i);
                    int available = c.getCount();
                    if (i == maxIdx)
                        available -= 1;
                    int minReqNodes = (i == h - 1) ? Utility.getTotalNodes(rightBottomNodes, childH) - missing : Utility.getRequiredNodesUpToDepth(rightBottomNodes, childH, i); // If this is the last colour, then take up to the total sequence required nodes. Else just up to the current depth required
                    int toPick = minReqNodes - sumB;
                    int actualPick = Utility.feasiblePick(sequenceA, leftBottomNodes, childH, c, toPick);
                    missing = toPick - actualPick;
                    sumB += actualPick;
                    sequenceA.add(new Colour(c.getColourCode(), available - actualPick));
                    sequenceB.add(new Colour(c.getColourCode(), actualPick));
                }
            } else if (c1.getCount() == 2) {
                int sumB = 0;
                int missing = 0;
                for (int i = 1; i < h; i++) {
                    Colour c = seq.get(i);
                    int available = c.getCount();
                    int minReqNodes = (i == h - 1) ? Utility.getTotalNodes(rightBottomNodes, childH) - missing : Utility.getRequiredNodesUpToDepth(rightBottomNodes, childH, i); // If this is the last colour, then take up to the total sequence required nodes. Else just up to the current depth required
                    int toPick = minReqNodes - sumB;
                    int actualPick = Utility.feasiblePick(sequenceA, leftBottomNodes, childH, c, toPick);
                    missing = toPick - actualPick;
                    sumB += actualPick;
                    sequenceA.add(new Colour(c.getColourCode(), available - actualPick));
                    sequenceB.add(new Colour(c.getColourCode(), actualPick));
                }
            } else { // In this case, there is an initial colour and we cannot modify it. Thus, we need to check which layer that colour belongs to, then select colours for other layers
                int sumB = 0;
                int missing = 0;
                int initIdx = 0;
                int initBColourCount = c1.getCount() - 1;
                int initBColourIdx = Utility.getInitColourIndex(rightBottomNodes, childH, initBColourCount);
                boolean addedInitCount = false;
                sequenceA.add(new Colour(c2.getColourCode(), c2.getCount() - 1));
                sequenceB.add(new Colour(c1.getColourCode(), initBColourCount));
                for (int i = 2; i < h; i++) { // ~ 3rd colour
                    if (initIdx == initBColourIdx || (i == h - 1 && !addedInitCount)) {
                        sumB += initBColourCount;
                        initIdx++;
                        addedInitCount = true;
                    }
                    Colour c = seq.get(i);
                    int available = c.getCount();
                    int minReqNodes = (i == h - 1) ? Utility.getTotalNodes(rightBottomNodes, childH) - missing : Utility.getRequiredNodesUpToDepth(rightBottomNodes, childH, initIdx + 1); // If this is the last colour, then take up to the total sequence required nodes. Else just up to the current depth required
                    int toPick = minReqNodes - sumB;
                    int actualPick = Utility.feasiblePick(sequenceA, leftBottomNodes, childH, c, toPick);
                    missing = toPick - actualPick;
                    sumB += actualPick;
                    sequenceA.add(new Colour(c.getColourCode(), available - actualPick));
                    sequenceB.add(new Colour(c.getColourCode(), actualPick));
                    initIdx++;
                }
            }
        }
        Collections.sort(sequenceA);
        Collections.sort(sequenceB);
        List<Colour>[] result = new List[2];
        result[0] = sequenceA;
        result[1] = sequenceB;
        int sumB = sequenceB.stream().mapToInt(e -> e.getCount()).sum();
        int totalBNodes = Utility.getTotalNodes(rightBottomNodes, childH);
        if (sumB < totalBNodes) { // Do further redistribution to make sure both sequences are valid
            int require = totalBNodes - sumB;
            result = redistributeSequences(sequenceA, leftBottomNodes, sequenceB, rightBottomNodes, childH, require);
        }
        return result;
    }

    public List<Colour>[] redistributeSequences(List<Colour> seqA, int tA, List<Colour> seqB, int tB, int h, int require) {
        boolean excludeC1 = false;
        for (int i = 0; i < require; i++) {
            int invalidIdx = Utility.findC1InvalidIndex(seqB, tB, h);
            // Check C3 abd C4 to see if colour 1 is valid for redistribution
            if (h > 1 && !excludeC1) {
                int c1 = seqB.get(0).getCount();
                int cn = seqB.get(seqB.size() - 1).getCount();
                int bRightLeafNodes = Utility.getRightLeafNodes(tB, h);
                int bDuplicateNodes = Utility.getDuplicateNodes(tB, h);
                int bRightBottomNodes = Utility.getRightBottomNodes(h, tB);
                int secondLastLayerReqNodes = (int)Math.ceil(bRightBottomNodes / 2.0);
                boolean isValidC3 = c1 < bRightLeafNodes + 1;
                boolean isValidC4 = (c1 + cn) < tA + tB + bDuplicateNodes + secondLastLayerReqNodes + 1;
                if (!isValidC3 || !isValidC4 || (bRightBottomNodes == 0 && c1 == 2))
                    excludeC1 = true;
            }

            if (invalidIdx == -1) {
                System.out.println("Cannot find invalid index.");
                return null;
            }

            List<Integer> requiredColour = new ArrayList<>(); // Take any possible colour from index 0 to invalidIdx from the left sequence
            for (int j = (excludeC1) ? 1 : 0; j <= invalidIdx; j++) {
                requiredColour.add(seqB.get(j).getColourCode());
            }

            Colour seqAColour = null;
            Colour seqBColour = null;
            for (int k = seqA.size() - 1; k >= 0; k--) {
                Colour c = seqA.get(k);
                int minReqNodes = Utility.getRequiredNodesUpToDepth(tA, h, k);
                int finalK = k;
                int sumA = seqA.stream().filter(e -> seqA.indexOf(e) <= finalK).mapToInt(e -> e.getCount()).sum();
                if (sumA > minReqNodes && requiredColour.contains(c.getColourCode())) {
                    seqAColour = c;
                    seqBColour = seqB.stream().filter(e -> e.getColourCode() == c.getColourCode()).findFirst().get();
                    break;
                }
            }

            if (seqBColour == null) {
                System.out.println("Cannot find suitable colour for redistribution!");
                return null;
            }

            seqAColour.setCount(seqAColour.getCount() - 1);
            seqBColour.setCount(seqBColour.getCount() + 1);
            Collections.sort(seqA);
            Collections.sort(seqB);
        }

        List<Colour>[] result = new List[2];
        result[0] = seqA;
        result[1] = seqB;
        return result;
    }
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
    /*
        * seq: colour sequence
        * t: the number of transactions
        * h: height of the tree
     */
    public static boolean isValidSequence(int[] seq, int t, int h) {
        boolean validC1 = isValidCondition1(seq, t, h);
        boolean validC2 = Arrays.stream(seq).sum() == getTotalNodes(t, h);
        boolean validC3 = h == 1 || isValidCondition3(seq[0], t, h); // if h == 1, then if C1 and C2 are valid, then C3 && C4 is valid
        boolean validC4 = h == 1 || isValidCondition4(seq[0], seq[seq.length - 1], t, h);
        boolean validC5 = isValidCondition5(seq, t, h);
        boolean isSequenceSorted = isSequenceSorted(seq);
        return validC1 && validC2 && validC3 && validC4 && validC5 && isSequenceSorted;
    }

    public static boolean isValidSequenceExceptC5(int[] seq, int t, int h) {
        boolean validC1 = isValidCondition1(seq, t, h);
        boolean validC2 = Arrays.stream(seq).sum() == getTotalNodes(t, h);
        boolean validC3 = h == 1 || isValidCondition3(seq[0], t, h); // if h == 1, then if C1 and C2 are valid, then C3 && C4 is valid
        boolean validC4 = h == 1 || isValidCondition4(seq[0], seq[seq.length - 1], t, h);
        boolean isSequenceSorted = isSequenceSorted(seq);
        return validC1 && validC2 && validC3 && validC4 && isSequenceSorted;
    }

    public static boolean isValidCondition1(int[] seq, int t, int h) {
        int requiredSum = 0;
        int actualSum = 0;
        for (int i = 1; i <= h; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(t, h, i);
            requiredSum += requiredNodes;
            actualSum += seq[i - 1];
            if (actualSum < requiredSum)
                return false;
        }
        return true;
    }

    // c1: the first colour
    public static boolean isValidCondition3(int c1, int t, int h) {
        int rightLeafNodes = getRightLeafNodes(t, h);
        if (c1 > (rightLeafNodes + 1))
            return false;
        return true;
    }

    public static boolean isValidCondition4(int c1, int cn, int t, int h) {
        int duplicateNodes = getDuplicateNodes(t, h);
        int rightBottomNodes = getRightBottomNodes(h, t);
        int secondLastLayerReqNodes = (int)Math.ceil(rightBottomNodes / 2.0);
        return (c1 + cn) <= (t + duplicateNodes + secondLastLayerReqNodes + 1);
    }

    public static boolean isValidCondition5(int[] seq, int t, int h) {
        if (h >= 4) {
            int actualLeftSum = (seq[0] == 2) ? 0 : -1;
            int expectedLeftSum = 0;
            int leftBottomNodes = Utility.getLeftBottomNodes(h, t);
            int rightBottomNodes = t - leftBottomNodes;
            boolean isPerfect = isPerfectTree(h, t);
            if (rightBottomNodes > 0 && !isPerfect) {
                for (int i = 1; i < h - 1; i++) {
                    int rightReqNodes = Utility.getRequiredNodesAtDepth(rightBottomNodes, h - 1, i);
                    actualLeftSum += seq[i];
                    actualLeftSum -= rightReqNodes;
                    expectedLeftSum += Utility.getRequiredNodesAtDepth(leftBottomNodes, h - 1, i);
                    if (actualLeftSum < expectedLeftSum)
                        return false;
                }
            }
        }
        return true;
    }

    public static int getRightLeafNodes(int t, int h) {
        int leftBottomNodes = getLeftBottomNodes(h, t);
        int rightBottomNodes = t - leftBottomNodes;
        int rightLeafNodes = rightBottomNodes + getDuplicateNodes(t, h);
        return rightLeafNodes;
    }

    public static boolean isSequenceSorted(int[] seq) {
        for (int i = 0; i < seq.length - 1; i++) {
            if (seq[i] > seq[i + 1])
                return false;
        }
        return true;
    }

    // This function will ignore the root node of the tree as this node will not be coloured
    public static int getTotalNodes(int t, int h) {
        int sum = 0;
        for (int i = 1; i <= h; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(t, h, i);
            sum += 2 * (int)Math.ceil(requiredNodes / 2.0); // Round up when the number of nodes is an odd number
        }
        return sum;
    }

    // Duplicate nodes are nodes that being replicated itself
    public static int getDuplicateNodes(int t, int h) {
        int duplicateNodes = 0;
        for (int i = 1; i <= h; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(t, h, i);
            if (requiredNodes % 2 != 0)
                duplicateNodes += 1;
        }
        return duplicateNodes;
    }

    public static int roundUpToEvenNumber(int number) {
        int result = number;
        if (result % 2 != 0)
            result += 1;
        return result;
    }

    public static int getTreeHeight(int t) {
        return (int)Math.ceil(Math.log(t) / Math.log(2));
    }

    public static int getRequiredNodesAtDepth(int t, int h, int i) {
        return (int)Math.ceil(t / Math.pow(2, h - i));
    }

    public static int getLeftBottomNodes(int h, int t) {
        int expectedLeft = (int)(Math.pow(2, h) / 2.0);
        if (t >= expectedLeft)
            return expectedLeft;
        else
            return t;
    }

    public static int getRightBottomNodes(int h, int t) {
        int expectedLeft = (int)(Math.pow(2, h) / 2);
        if (t > expectedLeft)
            return t - expectedLeft;
        else
            return 0;
    }

    public static boolean isPerfectTree(int h, int t) {
        t = roundUpToEvenNumber(t);
        return h == (Math.log(t) / Math.log(2));
    }

    public static int getRequiredNodesUpToDepth(int t, int h, int i) {
        int sum = 0;
        for (int j = 1; j <= i; j++) {
            sum += getRequiredNodesAtDepth(t, h, j);
        }
        return sum;
    }

    public static int getMaxAvailableColourIndex(List<Colour> seq, int t, int h) {
        for (int i = seq.size() - 1; i >= 0; i--) {
            int minRequiredNode = Utility.getRequiredNodesAtDepth(t, h, i + 1);
            if (seq.get(i).getCount() > minRequiredNode)
                return i;
        }
        return -1; // -1 means no invalid index
    }

    public static int getInitColourIndex(int t, int h, int colourCount) {
        for (int i = 0; i < h; i++) {
            int minReqNode = getRequiredNodesAtDepth(t, h, i + 1);
            if (colourCount < minReqNode)
                return i - 1;
        }
        return h - 1;
    }

    public static int findC1InvalidIndex(List<Colour> seq, int t, int h) {
        int requiredSum = 0;
        int actualSum = 0;
        for (int i = 1; i <= h; i++ ) {
            int requiredNodes = Utility.getRequiredNodesAtDepth(t, h, i);
            requiredSum += requiredNodes;
            actualSum += seq.get(i - 1).getCount();
            if (i == h)
                requiredSum = getTotalNodes(t, h);
            if (actualSum < requiredSum) {
                return i - 1;
            }
        }
        return -1; // -1 means no invalid index
    }

    // Used to identify how many a colour from sequence A can be picked
    // need: the number sequence B need to pick
    public static int feasiblePick(List<Colour> seq, int t, int h, Colour colour, int need) {
        int originalVal = colour.getCount();
        seq.add(colour);
        colour.setCount(colour.getCount() - need);
        Collections.sort(seq);
        int reqSum = 0;
        int actualSum = 0;
        int result = need;
        for (int i = 0; i < seq.size(); i++) {
            int reqNodes = Utility.getRequiredNodesAtDepth(t, h, i + 1);
            reqSum += reqNodes;
            actualSum += seq.get(i).getCount();
            if (actualSum < reqSum) {
                int diff = reqSum - actualSum;
                result -= diff;
                colour.setCount(colour.getCount() + diff);
                Collections.sort(seq);
            }
        }

        seq.remove(colour);
        colour.setCount(originalVal); // Reversed back to the original state
        return result;
    }

    /* Generate feasible colour sequences */
    // return the list of all feasible sequences
    // Ref: https://github.com/csa2022/Color-Spliting-Algorithm-CSA/blob/1279c42b3be778199260b7893e799b3eca544bb5/CSA.java#L490
    public static List<List<Colour>> getFeasibleSequenceList(int t){
        int h = getTreeHeight(t);
        int m = 0;  //the current position of colour m
        List<Colour> sequence = new ArrayList<>(h);
        List<List<Colour>> sequenceList = new ArrayList<>();
        feasibleSequenceListRecursive(sequenceList, sequence, m, h, t);
        return sequenceList;
    }

    public static void feasibleSequenceListRecursive(List<List<Colour>> sequenceList, List<Colour> sequence, int m, int h, int t) {
        // If sequence is complete, then add it to the sequenceList
        if (m == h) {
            List<Colour> tempSequence = new ArrayList<>(h);
            for (int i = 0; i < h; i++)
                tempSequence.add(new Colour(i, sequence.get(i).getCount()));
            int[] testSequence = tempSequence.stream().mapToInt(c -> c.getCount()).toArray();
            if (isValidSequence(testSequence, t, h))
                sequenceList.add(tempSequence);
            return;
        }

        int currentSum = 0;
        int sum = getTotalNodes(t, h);
        for (int i = 0; i < m; i++)
            currentSum += sequence.get(i).getCount();

        int remainingSum = sum - currentSum;
        int upperBound = (m == 0) ? (getRightLeafNodes(t, h) + 1) : (int)Math.floor(remainingSum / (double)(h - m)); // If first colour, then the upperBound is based on condition 3 (right leaf nodes + 1)
        int lowerBound = (m == 0) ? getRequiredNodesUpToDepth(t, h, m + 1) : Math.max(getRequiredNodesUpToDepth(t, h, m + 1) - currentSum, sequence.get(m - 1).getCount());
        for (int cm = lowerBound; cm <= upperBound; cm++) {
            if (sequence.size() <= m)
                sequence.add(m, new Colour(m, cm));
            else
                sequence.set(m, new Colour(m, cm));
            feasibleSequenceListRecursive(sequenceList, sequence, m + 1, h, t);
        }
    }

    public static void printSequence(List<Colour> sequence) {
        String text = "[" + sequence.stream().map(c -> String.valueOf(c.getCount())).collect(Collectors.joining(" ")) + "]";
        System.out.println(text);
    }

    public static void printSequenceList(List<List<Colour>> list) {
        list.forEach(l -> printSequence(l));
    }
}

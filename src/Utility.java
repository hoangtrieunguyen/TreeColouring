import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
    public static boolean isValidColourSequence(int[] sequence, int transactionCount, int height) {
        boolean isSatisfiedCondition1 = validateAgainstCondition1(sequence, transactionCount, height);
        boolean isSatisfiedCondition2 = Arrays.stream(sequence).sum() == calculateTreeNodes(transactionCount, height);
        boolean isSatisfiedCondition3 = validateAgainstCondition3(sequence[0], transactionCount, height);
        boolean isSequenceSorted = isSequenceSorted(sequence);
        return isSatisfiedCondition1 && isSatisfiedCondition2 && isSatisfiedCondition3 && isSequenceSorted;
    }

    public static boolean validateAgainstCondition1(int[] sequence, int transactionCount, int height) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int requiredSum = 0;
        int actualSum = 0;
        for (int i = 1; i <= height; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(transactionCount, height, i);
            requiredSum += requiredNodes;
            actualSum += sequence[i - 1];
            if (actualSum < requiredSum) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateAgainstCondition3(int firstColour, int transactionCount, int height) {
        int rightLeafNodes = getRightLeafNodes(transactionCount, height);
        if (firstColour > (rightLeafNodes + 1))
            return false;
        return true;
    }

    public static int getRightLeafNodes(int transactionCount, int height) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int leftBottomNodes = getLeftBottomNodes(height, transactionCount);
        int rightBottomNodes = transactionCount - leftBottomNodes;
        int rightLeafNodes = rightBottomNodes + calculateDuplicateNodes(transactionCount, height);
        return rightLeafNodes;
    }

    public static boolean isSequenceSorted(int[] sequence) {
        for (int i = 0; i < sequence.length - 1; i++) {
            if (sequence[i] > sequence[i + 1])
                return false;
        }
        return true;
    }

    // This function will ignore the root node of the tree as this node will not be coloured
    public static int calculateTreeNodes(int transactionCount, int height) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int sum = 0;
        for (int i = 1; i <= height; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(transactionCount, height, i);
            sum += 2 * (int)Math.ceil(requiredNodes / 2.0); // Round up when the number of nodes is an odd number
        }
        return sum;
    }

    // Duplicate nodes are nodes that being replicated itself (exclude nodes at highest depth)
    public static int calculateDuplicateNodes(int transactionCount, int height) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int duplicateNodes = 0;
        for (int i = 1; i <= height; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(transactionCount, height, i);
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

    public static int getTreeHeight(int transactionCount) {
        return (int)Math.ceil(Math.log(transactionCount) / Math.log(2));
    }

    public static int getRequiredNodesAtDepth(int t, int h, int i) {
        return (int)Math.ceil(t / Math.pow(2, h - i));
    }

    public static int getLeftBottomNodes(int height, int transactionCount) {
        int expectedLeft = (int)(Math.pow(2, height) / 2);
        if (transactionCount >= expectedLeft)
            return expectedLeft;
        else
            return transactionCount;
    }

    public static int getRightBottomNodes(int height, int transactionCount) {
        int expectedLeft = (int)(Math.pow(2, height) / 2);
        if (transactionCount > expectedLeft)
            return transactionCount - expectedLeft;
        else
            return 0;
    }

    public static boolean isPerfectTree(int height, int transactionCount) {
        return height == (Math.log(transactionCount) / Math.log(2));
    }

    public static int getRequiredNodesUpToDepth(int t, int h, int i) {
        int sum = 0;
        for (int j = 1; j <= i; j++) {
            sum += getRequiredNodesAtDepth(t, h, j);
        }
        return sum;
    }

    /*Generate feasible colour sequences*/
    //return the list of all feasible sequences
    // Ref: https://github.com/csa2022/Color-Spliting-Algorithm-CSA/blob/1279c42b3be778199260b7893e799b3eca544bb5/CSA.java#L490
    // t is the number of transaction transaction
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
            if (isValidColourSequence(testSequence, t, h))
                sequenceList.add(tempSequence);
            return;
        }

        int currentSum = 0;
        int sum = calculateTreeNodes(t, h);
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

    public static void printColourSequence(List<Colour> sequence) {
        String text = "[" + sequence.stream().map(c -> String.valueOf(c.getCount())).collect(Collectors.joining(" ")) + "]";
        System.out.println(text);
    }

    public static void printSequenceList(List<List<Colour>> list) {
        list.forEach(l -> printColourSequence(l));
    }
}

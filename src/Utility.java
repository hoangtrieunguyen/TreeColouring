import java.util.Arrays;

public class Utility {
    public static boolean isValidColourSequence(int[] sequence, int transactionCount) {
        boolean isSatisfiedCondition1 = validateAgainstCondition1(sequence, transactionCount);
        boolean isSatisfiedCondition2 = Arrays.stream(sequence).sum() == calculateTreeNodes(transactionCount);
        boolean isSatisfiedCondition3 = validateAgainstCondition3(sequence, transactionCount);
        boolean isSequenceSorted = isSequenceSorted(sequence);
        return isSatisfiedCondition1 && isSatisfiedCondition2 && isSatisfiedCondition3 && isSequenceSorted;
    }

    public static boolean validateAgainstCondition1(int[] sequence, int transactionCount) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int height = getTreeHeight(transactionCount);
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

    public static boolean validateAgainstCondition3(int[] sequence, int transactionCount) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int height = getTreeHeight(transactionCount);
        int totalBottomNodes = (int)Math.pow(2, height);
        int leftBottomNodes = totalBottomNodes - (totalBottomNodes / 2); // First left child is always a perfect tree
        int rightBottomNodes = totalBottomNodes - leftBottomNodes;
        int rightLeafNodes = rightBottomNodes + calculateDuplicateNodes(transactionCount);
        if (sequence[0] > rightLeafNodes)
            return false;
        return true;
    }

    public static boolean isSequenceSorted(int[] sequence) {
        for (int i = 0; i < sequence.length - 1; i++) {
            if (sequence[i] > sequence[i + 1])
                return false;
        }
        return true;
    }

    // This function will ignore the root node of the tree as this node will not be coloured
    public static int calculateTreeNodes(int transactionCount) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int height = getTreeHeight(transactionCount);
        int sum = 0;
        for (int i = 1; i <= height; i++ ) {
            int requiredNodes = getRequiredNodesAtDepth(transactionCount, height, i);
            sum += 2 * (int)Math.ceil((double)requiredNodes / 2.0); // Round up when the number of nodes is an odd number
        }
        return sum;
    }

    // Duplicate nodes are nodes that being replicated itself (exclude nodes at highest depth)
    public static int calculateDuplicateNodes(int transactionCount) {
        transactionCount = roundUpToEvenNumber(transactionCount);
        int duplicateNodes = 0;
        int height = getTreeHeight(transactionCount);
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
        return (int)Math.ceil((double)t / Math.pow(2, h - i));
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
}

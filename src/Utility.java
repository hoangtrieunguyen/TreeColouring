import java.util.Arrays;

public class Utility {
    public static boolean isValidColourSequence(int[] sequence, int transactionCount) {
        boolean isSatisfiedCondition1 = validateAgainstCondition1(sequence, transactionCount);
        boolean isSatisfiedCondition2 = Arrays.stream(sequence).sum() == calculateTreeNodes(transactionCount);
        return isSatisfiedCondition1 && isSatisfiedCondition2;
    }

    // This function will ignore the root node of the tree as this node will not be coloured
    public static int calculateTreeNodes(int transactionCount) {
        int height = (int)Math.ceil(Math.log(transactionCount) / Math.log(2));
        int sum = 0;
        for (int i = 1; i < height; i++ ) {
            int requiredNodes = (int)Math.ceil(transactionCount / Math.pow(2, height - i));
            sum += 2 * (int)Math.ceil(requiredNodes / 2); // Round up when the number of nodes is an odd number
        }
        return sum;
    }

    public static boolean validateAgainstCondition1(int[] sequence, int transactionCount) {
        int height = (int)Math.ceil(Math.log(transactionCount) / Math.log(2));
        int requiredSum = 0;
        int actualSum = 0;
        for (int i = 1; i < height; i++ ) {
            int requiredNodes = (int)Math.ceil(transactionCount / Math.pow(2, height - i));
            requiredSum += requiredNodes;
            actualSum += sequence[i];
            if (actualSum < requiredSum) {
                return false;
            }
        }
        return true;
    }
}

import java.util.Arrays;
import java.util.List;

public class Program {

    public static void main(String[] args) {
        // Arguments: tx - number of transactions
        if (args.length != 2) {
            System.out.println("Invalid parameter(s). Please input: [transactions] [colour sequence].");
            System.exit(1);
        }

        int tx = Integer.parseInt(args[0]);
        if (tx < 1) {
            System.out.println("Invalid number. The number of transactions must be greater than 0.");
        }

        String[] transactions = new String[tx];
        for (int i = 0; i < transactions.length; i++)
            transactions[i] = String.valueOf(i);

        int[] sequence = Arrays.stream(args[1].split(",")).mapToInt(Integer::parseInt).toArray();
        MerkleTree aTree = new MerkleTree(transactions);
        aTree.colourSplitting(sequence);
        aTree.validateTreeColouring();
        /*
        int h = 5;
        int t1 = (int)Math.pow(2, h - 1) + 2;
        int t2 = (int)Math.pow(2, h - 1) + (int)Math.pow(2, h - 2);
        int t3 = (int)Math.pow(2, h);
        int[] arr = {t1, t2, t3};
        for (int i = 0; i < arr.length; i++) {
            transactions = new String[arr[i]];
            for (int j = 0; j < arr[i]; j++)
                transactions[j] = String.valueOf(j);


            System.out.println("Transaction t = " + arr[i]);
            System.out.println("Tree's nodes n = " + Utility.getTotalNodes(arr[i], h));

            List<List<Colour>> layer = Utility.getFeasibleSequenceList(arr[i], 1);
            List<List<Colour>> balance = Utility.getFeasibleSequenceList(arr[i], 2);

            System.out.println("Layer-based");
            for (List<Colour> seq: layer) {
                Utility.printSequence(seq);
                MerkleTree tree = new MerkleTree(transactions);
                tree.colourSplitting(seq.stream().mapToInt(e -> e.getCount()).toArray());
                tree.validateTreeColouring();
            }

            System.out.println("Balanced");
            for (List<Colour> seq: balance) {
                Utility.printSequence(seq);
                MerkleTree tree = new MerkleTree(transactions);
                tree.colourSplitting(seq.stream().mapToInt(e -> e.getCount()).toArray());
                tree.validateTreeColouring();
            }
        }

        */

    }
}
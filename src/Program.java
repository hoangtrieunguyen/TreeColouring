import java.util.Arrays;
import java.util.List;

public class Program {

    public static void main(String[] args) {
        // Arguments: tx - number of transactions
        if (args.length != 2) {
            System.out.println("Invalid parameter(s). Please input: [transactions] [colour sequence].");
            System.exit(1);
        }

        int txCount = Integer.parseInt(args[0]);
        if (txCount < 1) {
            System.out.println("Invalid number. The number of transactions must be greater than 0.");
        }

        String[] transactions = new String[txCount];

        for (int i = 0; i < transactions.length; i++)
            transactions[i] = String.valueOf(i);

        int[] sequence = Arrays.stream(args[1].split(",")).mapToInt(Integer::parseInt).toArray();
        if (!Utility.isValidColourSequence(sequence, txCount, Utility.getTreeHeight(Utility.roundUpToEvenNumber(txCount)))) {
            System.out.println("Invalid colour sequence. This colour sequence is not feasible to use for colouring this tree.");
            System.exit(1);
        } else {
            System.out.println("Feasible colour sequence.");
        }

        MerkleTree tree0 = new MerkleTree(transactions);
        tree0.colourSplitting(sequence);
        tree0.validateTreeColouring();

        List<List<Colour>> list = Utility.getFeasibleSequenceList(txCount);
        MerkleTree tree = new MerkleTree(transactions);
        for (List<Colour> s: list) {
            Utility.printColourSequence(s);
            int[] tempSequence = s.stream().mapToInt(c -> c.getCount()).toArray();
            tree.colourSplitting(tempSequence);
            tree.validateTreeColouring();
        }
    }
}

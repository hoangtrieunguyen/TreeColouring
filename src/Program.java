import java.util.Arrays;
import java.util.List;

public class Program {

    public static void main(String[] args) {
        // Arguments: tx - number of transactions
        if (args.length != 2) {
            System.out.println("Invalid parameter(s). Please input: [transactions] [colour sequence].");
            System.exit(1);
        }

        int t = Integer.parseInt(args[0]);
        if (t < 1) {
            System.out.println("Invalid number. The number of transactions must be greater than 0.");
        }

        String[] transactions = new String[t];
        for (int i = 0; i < transactions.length; i++)
            transactions[i] = String.valueOf(i);

        int[] sequence = Arrays.stream(args[1].split(",")).mapToInt(Integer::parseInt).toArray();
        MerkleTree aTree = new MerkleTree(transactions);
        aTree.colourSplitting(sequence);
        aTree.validateTreeColouring();

        int h = 6;
        for (int i = 66; i <= 128; i++) {
            System.out.println("Current transaction length: " + i);
            int n = (int)(Math.pow(2, h)/2 + Math.pow(2, h - i));
            List<List<Colour>> list = Utility.getFeasibleSequenceList(i);

            transactions = new String[i];
            for (int j = 0; j < i; j++)
                transactions[j] = String.valueOf(j);

            MerkleTree tree = new MerkleTree(transactions);
            for (List<Colour> seq: list) {
                //System.out.print("=======================================Tx: " + i);
                //Utility.printSequence(seq);
                tree.colourSplitting(seq.stream().mapToInt(e -> e.getCount()).toArray());
                tree.validateTreeColouring();
            }

        }

    }
}
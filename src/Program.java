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
        //aTree.colourSplitting(sequence);
        //aTree.validateTreeColouring();

        int h = 12;
        int min = (int)Math.pow(2, h - 1);
        int max = (int)Math.pow(2, h);
        for (int i = min; i <= max; i++) {
            //int n = (int)(Math.pow(2, h)/2 + Math.pow(2, h - i));
            List<List<Colour>> list = Utility.getFeasibleSequenceList(i);
            System.out.println("Current transaction length: " + i + " --- Total sequences: " + list.size());
            if (list.size() == 0) {
                System.out.println("Empty list. Exit.");
                System.exit(2);
            }

            int count = 1;

            transactions = new String[i];
            for (int j = 0; j < i; j++)
                transactions[j] = String.valueOf(j);

            MerkleTree tree = new MerkleTree(transactions);
            for (List<Colour> seq: list) {
                System.out.print(count + " ");
                Utility.printSequence(seq);
                tree.colourSplitting(seq.stream().mapToInt(e -> e.getCount()).toArray());
                tree.validateTreeColouring();
                count++;
            }

        }

    }
}
public class Program {

    public static void main(String[] args) {
        // Arguments: tx - number of transactions
        if (args.length != 1) {
            System.out.println("Invalid parameter(s). Please enter the number of transactions.");
            System.exit(1);
        }

        int txCount = Integer.parseInt(args[0]);
        if (txCount < 1) {
            System.out.println("Invalid number. The number of transactions must be greater than 0.");
        }

        String[] transactions = new String[txCount];
        for (int i = 0; i < transactions.length; i++)
            transactions[i] = String.valueOf(i);

        MerkleTree tree = new MerkleTree(transactions);
        tree.print();
    }
}

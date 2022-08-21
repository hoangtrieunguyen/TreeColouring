import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Node {
    private String value;
    private Node parent;
    private Node left;
    private Node right;
    private int colourGroup;

    public Node(String value, Node left, Node right) {
        this.value = value; // Use getSHA256Hash for hash
        this.left = left;
        this.right = right;
        this.colourGroup = -1;
    }

    public String getSHA256Hash(String value) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashValue = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return hex(hashValue);
    }

    // Ref: https://mkyong.com/java/java-how-to-convert-bytes-to-hex/
    public String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

    public String getValue() {
        return value;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public int getColourGroup() {
        return colourGroup;
    }

    public void setColourGroup(int colourGroup) {
        this.colourGroup = colourGroup;
    }
}

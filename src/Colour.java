public class Colour implements Comparable<Colour> {
    private int colourCode;
    private int count;

    public Colour() {}

    public Colour(int colourCode, int count) {
        this.colourCode = colourCode;
        this.count = count;
    }

    public int getColourCode() {
        return colourCode;
    }

    public void setColourCode(int colourCode) {
        this.colourCode = colourCode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(Colour o) {
        return this.count - o.getCount();
    }
}

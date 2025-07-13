package expression;

public class Add extends BinaryOperation {
    public Add(Expressions left, Expressions right) {
        super(left, right);
    }

    @Override
    public int evaluate(int x) {
        return left.evaluate(x) + right.evaluate(x);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        super.toString(sb);
        return sb.toString();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return left.evaluate(x, y, z) + right.evaluate(x, y, z);
    }

    @Override
    public String getSymbol() {
        return " + ";
    }
}
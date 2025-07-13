package expression;

import java.util.Objects;


public abstract class BinaryOperation implements Expressions {
    protected final Expressions left;
    protected final Expressions right;

    public abstract String getSymbol();

    public BinaryOperation(Expressions left, Expressions right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BinaryOperation) {
            BinaryOperation other = (BinaryOperation) object;
            return this.getClass().equals(other.getClass()) &&
                    this.left.equals(other.left) &&
                    this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append("(");
        left.toString(sb);
        sb.append(getSymbol());
        right.toString(sb);
        sb.append(")");
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, getClass(), right) * 2;
    }
}

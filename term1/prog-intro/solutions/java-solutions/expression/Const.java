package expression;

public class Const implements Expressions {
    private final int value;
    public Const(int value) {
        this.value = value;
    }

    @Override
    public int evaluate(int x) {
        return value;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return value;
    }

    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append(value);
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Const other = (Const) object;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
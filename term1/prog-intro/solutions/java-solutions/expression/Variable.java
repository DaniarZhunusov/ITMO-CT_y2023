package expression;

import java.util.*;

public class Variable implements Expressions {
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (var) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new IllegalArgumentException("Error: " + var);
        }
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append(var);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Variable other = (Variable) object;
        return Objects.equals(this.var, other.var);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(var);
    }
}
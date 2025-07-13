package expression;

public interface Expressions extends Expression, TripleExpression {
    int evaluate(int x);
    int evaluate(int x, int y, int z);
    void toString(StringBuilder sb);
}
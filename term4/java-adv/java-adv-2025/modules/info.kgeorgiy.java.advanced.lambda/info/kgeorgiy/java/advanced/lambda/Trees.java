package info.kgeorgiy.java.advanced.lambda;

import java.util.List;
import java.util.Objects;

/**
 * Trees definitions.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Trees {
    /**
     * Tree leaf.
     */
    record Leaf<T>(T value) implements Binary<T>, SizedBinary<T>, Nary<T> {
        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public int size() {
            return 1;
        }
    }

    /**
     * Binary tree.
     */
    @SuppressWarnings("unused")
    sealed interface Binary<T> {
        record Branch<T>(Binary<T> left, Binary<T> right) implements Binary<T> {
            @Override
            public String toString() {
                return "[%s, %s]".formatted(left, right);
            }
        }
    }

    /**
     * Binary tree with known size.
     */
    @SuppressWarnings("unused")
    sealed interface SizedBinary<T> {
        int size();

        record Branch<T>(SizedBinary<T> left, SizedBinary<T> right, int size) implements SizedBinary<T> {
            public Branch(final SizedBinary<T> left, final SizedBinary<T> right) {
                this(left, right, left.size() + right.size());
            }
        }
    }

    /**
     * N-ary tree.
     *
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    @SuppressWarnings("unused")
    sealed interface Nary<T> {
        record Node<T>(List<Nary<T>> children) implements Nary<T> {
            @Override
            public String toString() {
                return children.toString();
            }
        }
    }
}

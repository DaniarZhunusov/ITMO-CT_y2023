package info.kgeorgiy.java.advanced.lambda;

import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collector;

/**
 * Easy-version interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface EasyLambda {
    /**
     * Returns binary tree spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree);

    /**
     * Returns sized binary tree spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree);

    /**
     * Returns nary tree spliterator.
     * This method runs in O({@code n}) time, where {@code n} is the size of the root.
     */
    <T> Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree);

    /**
     * Returns the first element of the stream, if present.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, Optional<T>> first();

    /**
     * Returns the last element of the stream, if present.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, Optional<T>> last();

    /**
     * Returns middle element of the stream, if present.
     * This collector runs in O({@code n}) memory, where {@code n} is the index of the returned element.
     */
    <T> Collector<T, ?, Optional<T>> middle();

    /**
     * Computes a common prefix of input {@link CharSequence}s.
     * This collector runs in O({@code n}) memory, where {@code n} is the length of the longest sequence.
     */
    Collector<CharSequence, ?, String> commonPrefix();

    /**
     * Computes a common suffix of input {@link CharSequence}s.
     * This collector runs in O({@code n}) memory, where {@code n} is the length of the longest sequence.
     */
    Collector<CharSequence, ?, String> commonSuffix();
}

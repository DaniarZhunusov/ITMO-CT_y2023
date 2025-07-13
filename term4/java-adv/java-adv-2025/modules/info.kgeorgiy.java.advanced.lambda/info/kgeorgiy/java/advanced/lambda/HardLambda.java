package info.kgeorgiy.java.advanced.lambda;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collector;

/**
 * Hard-version interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface HardLambda extends EasyLambda {
    /**
     * Returns binary tree of lists spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> nestedBinaryTreeSpliterator(Trees.Binary<List<T>> tree);

    /**
     * Returns sized binary tree of lists spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> nestedSizedBinaryTreeSpliterator(Trees.SizedBinary<List<T>> tree);

    /**
     * Returns nary tree of lists spliterator.
     * This method runs in O({@code n}) time, where {@code n} is the size of the root.
     */
    <T> Spliterator<T> nestedNaryTreeSpliterator(Trees.Nary<List<T>> tree);

    /**
     * Returns at most {@code k} first elements.
     * This collector runs in O({@code k}) memory.
     */
    <T> Collector<T, ?, List<T>> head(int k);

    /**
     * Returns at most {@code k} last elements.
     * This collector runs in O({@code k}) memory.
     */
    <T> Collector<T, ?, List<T>> tail(int k);

    /**
     * Returns {@code k}-th element of the stream, if present.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, Optional<T>> kth(final int k);
}

package info.kgeorgiy.java.advanced.lambda;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Advanced-version interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface AdvancedLambda extends HardLambda {
    /**
     * Computes the {@link List} of the  input elements for which given mapper function returns distinct results.
     * This collector runs in O({@code n}) memory, where {@code n} is the size of the result list.
     */
    <T> Collector<T, ?, List<T>> distinctBy(Function<? super T, ?> mapper);

    /**
     * Finds the index of the minimal stream element according to the specified {@link Comparator}.
     * If there are several minimal elements, the index of the first one is returned.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, OptionalLong> minIndex(Comparator<? super T> comparator);

    /**
     * Finds the index of the maximal stream element according to the specified {@link Comparator}.
     * If there are several maximal elements, the index of the first one is returned.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, OptionalLong> maxIndex(Comparator<? super T> comparator);
}

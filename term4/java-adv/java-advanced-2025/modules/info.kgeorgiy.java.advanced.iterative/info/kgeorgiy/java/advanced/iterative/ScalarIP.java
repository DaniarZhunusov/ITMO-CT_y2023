package info.kgeorgiy.java.advanced.iterative;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Scalar iterative parallelism support.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface ScalarIP {
    /**
     * Returns index of the first maximum.
     *
     * @param threads number of concurrent threads.
     * @param values values to find maximum in.
     * @param comparator value comparator.
     * @param <T> value type.
     *
     * @return index of the first maximum in given values.
     *
     * @throws InterruptedException if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    <T> int argMax(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException;

    /**
     * Returns index of the first minimum.
     *
     * @param threads number of concurrent threads.
     * @param values values to find minimum in.
     * @param comparator value comparator.
     * @param <T> value type.
     *
     * @return index of the first minimum in given values.
     *
     * @throws InterruptedException if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    <T> int argMin(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException;

    /**
     * Returns the index of the first value satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return index of the first value satisfying the predicate, or {@code -1}, if there are none.
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> int indexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException;

    /**
     * Returns the index of the last value satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return index of the last value satisfying the predicate, or {@code -1}, if there are none.
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> int lastIndexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException;

    /**
     * Returns sum of the indices of the values satisfying a predicate.
     *
     * @param threads number of concurrent threads.
     * @param values values to test.
     * @param predicate test predicate.
     * @param <T> value type.
     *
     * @return sum of the indices of values satisfying the predicate.
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> long sumIndices(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException;
}

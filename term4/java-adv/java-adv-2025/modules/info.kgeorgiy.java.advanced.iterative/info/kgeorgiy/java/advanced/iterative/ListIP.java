package info.kgeorgiy.java.advanced.iterative;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * List iterative parallelism support.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface ListIP extends ScalarIP {
    /**
     * Returns indices of the values matching predicate.
     *
     * @param threads number of concurrent threads.
     * @param values values to join.
     *
     * @return indices of the values matching predicate in increasing order.
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> int[] indices(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException;

    /**
     * Filters values by predicate.
     *
     * @param threads number of concurrent threads.
     * @param values values to filter.
     * @param predicate filter predicate.
     *
     * @return list of values satisfying given predicate. Order of values is preserved.
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> List<T> filter(
            int threads,
            List<? extends T> values,
            Predicate<? super T> predicate
    ) throws InterruptedException;

    /**
     * Maps values.
     *
     * @param threads number of concurrent threads.
     * @param values values to map.
     * @param f mapper function.
     *
     * @return list of values mapped by given function.
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T, U> List<U> map(
            int threads,
            List<? extends T> values,
            Function<? super T, ? extends U> f
    ) throws InterruptedException;
}

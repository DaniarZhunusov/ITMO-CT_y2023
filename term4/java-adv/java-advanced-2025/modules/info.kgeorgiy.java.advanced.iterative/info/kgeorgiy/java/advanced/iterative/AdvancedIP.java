package info.kgeorgiy.java.advanced.iterative;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Advanced iterative parallelism support.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface AdvancedIP extends ListIP {
    /**
     * Reduces values using monoid.
     *
     * @param threads number of concurrent threads.
     * @param values values to reduce.
     * @param identity monoid identity element.
     * @param operator monoid operation.
     *
     * @return values reduced by provided monoid or {@code identity} if no values specified.
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> T reduce(
            int threads,
            List<T> values,
            T identity,
            BinaryOperator<T> operator
    ) throws InterruptedException;

    /**
     * Maps and reduces values using monoid.
     *
     * @param threads number of concurrent threads.
     * @param values values to reduce.
     * @param lift mapping function.
     * @param identity monoid identity element.
     * @param operator monoid operation.
     *
     * @return values reduced by provided monoid or {@code identity} if no values specified.
     *
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T, R> R mapReduce(
            int threads,
            List<T> values,
            Function<T, R> lift,
            R identity,
            BinaryOperator<R> operator
    ) throws InterruptedException;
}

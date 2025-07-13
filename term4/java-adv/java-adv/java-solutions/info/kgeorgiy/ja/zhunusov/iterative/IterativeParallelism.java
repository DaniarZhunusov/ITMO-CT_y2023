package info.kgeorgiy.ja.zhunusov.iterative;

import info.kgeorgiy.java.advanced.iterative.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.*;

public class IterativeParallelism implements ScalarIP {

    private final ParallelMapper parallelMapper;

    /**
     * Creates instance with independent thread management.
     */
    public IterativeParallelism() {
        this.parallelMapper = null;
    }

    /**
     * Creates instance using shared {@link ParallelMapper}.
     * @param parallelMapper the ParallelMapper to use for parallel operations.
     */
    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    /**
     * Finds index of the first maximum element in the list using multiple threads.
     *
     * @param threads number of threads to use
     * @param values list of values to search
     * @param comparator comparator to determine maximum
     * @param <T> type of elements in the list
     * @return index of the first maximum element, or -1 if list is empty
     * @throws InterruptedException if any thread was interrupted
     */
    @Override
    public <T> int argMax(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        return findArg(threads, values, comparator, cmp -> cmp > 0);
    }

    /**
     * Finds index of the first minimum element in the list using multiple threads.
     *
     * @param threads number of threads to use
     * @param values list of values to search
     * @param comparator comparator to determine minimum
     * @param <T> type of elements in the list
     * @return index of the first minimum element, or -1 if list is empty
     * @throws InterruptedException if any thread was interrupted
     */
    @Override
    public <T> int argMin(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        return findArg(threads, values, comparator, cmp -> cmp < 0);
    }

    /**
     * Finds first index of element matching the predicate using multiple threads.
     *
     * @param threads number of threads to use
     * @param values list of values to search
     * @param predicate predicate to test elements
     * @param <T> type of elements in the list
     * @return first matching index, or -1 if not found
     * @throws InterruptedException if any thread was interrupted
     */
    @Override
    public <T> int indexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException {
        return findIndex(threads, values, predicate, i -> i, true);
    }

    /**
     * Finds last index of element matching the predicate using multiple threads.
     *
     * @param threads number of threads to use
     * @param values list of values to search
     * @param predicate predicate to test elements
     * @param <T> type of elements in the list
     * @return last matching index, or -1 if not found
     * @throws InterruptedException if any thread was interrupted
     */
    @Override
    public <T> int lastIndexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException {
        return findIndex(threads, values, predicate, i -> values.size() - 1 - i, false);
    }

    /**
     * Sums indices of elements matching the predicate using multiple threads.
     *
     * @param threads number of threads to use
     * @param values list of values to process
     * @param predicate predicate to test elements
     * @param <T> type of elements in the list
     * @return sum of indices of matching elements
     * @throws InterruptedException if any thread was interrupted
     */
    @Override
    public <T> long sumIndices(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return parallelSearch(threads, values,
                (start, end) -> {
                    long sum = 0;
                    for (int i = start; i < end; i++) {
                        if (predicate.test(values.get(i))) {
                            sum += i;
                        }
                    }
                    return sum;
                },
                Long::sum
        );
    }

    private <T> int findArg(int threads, List<T> values, Comparator<? super T> comparator,
                            IntPredicate condition) throws InterruptedException {
        if (values.isEmpty()) return -1;
        return parallelSearch(threads, values,
                (start, end) -> {
                    int idx = start;
                    for (int i = start + 1; i < end; i++) {
                        int cmp = comparator.compare(values.get(i), values.get(idx));
                        if (condition.test(cmp) || (cmp == 0 && i < idx)) {
                            idx = i;
                        }
                    }
                    return idx;
                },
                (a, b) -> {
                    int cmp = comparator.compare(values.get(a), values.get(b));
                    return cmp != 0 ? (condition.test(cmp) ? a : b) : Math.min(a, b);
                }
        );
    }

    private <T> int findIndex(int threads, List<T> values, Predicate<? super T> predicate,
                              IntUnaryOperator indexMapper, boolean findFirst) throws InterruptedException {
        return parallelSearch(threads, values,
                (start, end) -> {
                    for (int i = start; i < end; i++) {
                        int mappedIndex = indexMapper.applyAsInt(i);
                        if (predicate.test(values.get(mappedIndex))) {
                            return mappedIndex;
                        }
                    }
                    return -1;
                },
                (a, b) -> {
                    if (a == -1) return b;
                    if (b == -1) return a;
                    return findFirst ? Math.min(a, b) : Math.max(a, b);
                }
        );
    }

    private <T, R> R parallelSearch(int threads, List<T> values,
                                    BiFunction<Integer, Integer, R> processor,
                                    BinaryOperator<R> merger) throws InterruptedException {
        if (threads <= 0) {
            throw new IllegalArgumentException("Number of threads must be positive");
        }

        int size = values.size();
        threads = Math.min(threads, size);
        List<int[]> ranges = splits(threads, size);

        if (parallelMapper != null) {
            return parallelSearchWithMapper(ranges, processor, merger);
        } else {
            return parallelSearchWithThreads(ranges, processor, merger);
        }
    }

    private <R> R parallelSearchWithMapper(List<int[]> ranges,
                                           BiFunction<Integer, Integer, R> processor,
                                           BinaryOperator<R> merger) throws InterruptedException {
        List<R> results;
        results = parallelMapper.map(range -> processor.apply(range[0], range[1]), ranges);
        if (results != null) {
            return results.stream().reduce(merger).orElseThrow();
        }
        return null;
    }

    private <R> R parallelSearchWithThreads(List<int[]> ranges,
                                            BiFunction<Integer, Integer, R> processor,
                                            BinaryOperator<R> merger) throws InterruptedException {
        int threads = ranges.size();
        List<R> results = new ArrayList<>(Collections.nCopies(threads, null));
        Thread[] workers = new Thread[threads];
        InterruptedException hadError = null;

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            int start = ranges.get(i)[0];
            int end = ranges.get(i)[1];

            workers[i] = new Thread(() -> results.set(threadId, processor.apply(start, end)));
            workers[i].start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                if (hadError == null) {
                    hadError = e;
                } else {
                    hadError.addSuppressed(e);
                }
                Thread.currentThread().interrupt();
            }
        }

        if (hadError != null) {
            throw hadError;
        }

        return results.stream().reduce(merger).orElseThrow();
    }

    private List<int[]> splits(int threads, int size) {
        threads = Math.min(threads, size);
        int chunkSize = size / threads;
        int remainder = size % threads;

        List<int[]> lists = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < threads; i++) {
            int end = start + chunkSize + (i < remainder ? 1 : 0);
            lists.add(new int[]{start, end});
            start = end;
        }
        return lists;
    }
}
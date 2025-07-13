package info.kgeorgiy.java.advanced.iterative;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ScalarIPTest<P extends ScalarIP> extends BaseIPTest<P> {
    public static final int PROCESSORS = 4; //Runtime.getRuntime().availableProcessors();

    public ScalarIPTest() {
    }

    @Test
    public void test01_argMax() throws InterruptedException {
        test((values, comparator) -> values.indexOf(Collections.max(values, comparator)), P::argMax, COMPARATORS);
    }

    @Test
    public void test02_argMin() throws InterruptedException {
        test((values, comparator) -> values.indexOf(Collections.min(values, comparator)), P::argMin, COMPARATORS);
    }

    @Test
    public void test03_indexOf() throws InterruptedException {
        testS((values, predicate) -> values.map(predicate::test).toList().indexOf(true), P::indexOf, PREDICATES);
    }

    @Test
    public void test04_lastIndexOf() throws InterruptedException {
        testS((values, predicate) -> values.map(predicate::test).toList().lastIndexOf(true), P::lastIndexOf, PREDICATES);
    }

    @Test
    public void test05_sumIndices() throws InterruptedException {
        testS(
                (stream, predicate) -> {
                    final List<Boolean> flags = stream.map(predicate::test).toList();
                    return IntStream.range(0, flags.size()).filter(flags::get).mapToLong(a -> a).sum();
                },
                P::sumIndices,
                PREDICATES
        );
        factors.stream().map(this::createInstance).forEachOrdered(instance ->
                IntStream.rangeClosed(1, MAX_THREADS).forEachOrdered(threads ->
                        IntStream.iterate(1, n -> n * 10).limit(7).forEachOrdered(n -> {
                            try {
                                Assertions.assertEquals(
                                        n * (n - 1L) / 2,
                                        instance.sumIndices(threads, Collections.nCopies(n, null), v -> true),
                                        () -> "For threads = %d, n = %d".formatted(threads, n)
                                );
                            } catch (final InterruptedException e) {
                                throw new AssertionError(e);
                            }
                        }))
        );
    }

    @Test
    public void test10_sleepPerformance() throws InterruptedException {
        testPerformance("argMax", MAX_THREADS, 3, 1, 1.5, (instance, threads, values) ->
                instance.argMax(threads, values, (o1, o2) -> sleep(Integer.compare(o1, o2))));
        testPerformance("sumIndices", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.sumIndices(threads, values, v -> sleep(v % 3 == 1)));
    }

    @Test
    public void test11_burnPerformance() throws InterruptedException {
        testPerformance("argMax", PROCESSORS, 50, 1, 1.5, (instance, threads, values) ->
                instance.argMax(threads, values, (o1, o2) -> burn(Integer.compare(o1, o2))));
        testPerformance("sumIndices", PROCESSORS, 50, 0, 1.5, (instance, threads, values) ->
                instance.sumIndices(threads, values, v1 -> burn(v1 % 3 == 1)));
    }

    protected void testPerformance(
            final String name,
            final int threads,
            final int sizeMultiplier,
            final int sequentialWeight,
            final double delta,
            final ConcurrentConsumer<P, Integer, List<Integer>> consumer
    ) throws InterruptedException {
        new PerformanceTest(name, threads, sequentialWeight, consumer).test(sizeMultiplier * threads, delta);
    }

    protected static <T> T sleep(final T result) {
        try {
            Thread.sleep(50);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    protected static <T> T burn(final T result) {
        int total = result.hashCode();
        for (int i = 0; i < 10_000_000; i++) {
            total += i;
        }
        if (total == result.hashCode()) {
            throw new AssertionError("No burn");
        }
        return result;
    }


    protected class PerformanceTest {
        private static final int WARMUP_CYCLES = 3;

        private final String name;
        private final int threads;
        private final int sequentialWeight;
        private final ConcurrentConsumer<P, Integer, List<Integer>> consumer;

        protected PerformanceTest(
                final String name,
                final int threads,
                final int sequentialWeight,
                final ConcurrentConsumer<P, Integer, List<Integer>> consumer
        ) {
            this.name = name;
            this.threads = threads;
            this.sequentialWeight = sequentialWeight;
            this.consumer = consumer;
        }

        private double measure(final int realThreads, final List<Integer> data, final String name) throws InterruptedException {
            return context.context(name, () -> {
                final int subtasks = getSubtasks(realThreads, threads);
                assert subtasks % realThreads == 0 && data.size() % subtasks == 0;

                final long time = measureTime(realThreads, data, consumer, subtasks);

                final int sequential = (subtasks - 1) * sequentialWeight;
                final int parallel = (data.size() - subtasks) / realThreads;
                return time / 1e6 / (sequential + parallel);
            });
        }

        private double speedup(final int size) throws InterruptedException {
            return context.context(name, () -> {
                final int subtasks = getSubtasks(threads, threads);
                final List<Integer> data = List.copyOf(randomList(((size - 1) / subtasks + 1) * subtasks));

                for (int i = 0; i < WARMUP_CYCLES; i++) {
                    measure(threads, data, "Warm up (%d/%d)".formatted(i + 1, WARMUP_CYCLES));
                }

                final double performance1 = measure(1, data, "Measure single-threaded");
                final double performance2 = measure(threads, data, "Measure multi-threaded");
                final double speedup = performance2 / performance1;
                context.println("Performance ratio %.1f for %d threads (%.1f %.1f ms/op)"
                        .formatted(speedup, threads, performance1, performance2));
                return speedup;
            });
        }

        protected void test(final int size, final double delta) throws InterruptedException {
            checkRatio(speedup(size), delta);
        }
    }

    protected static void checkRatio(final double value, final double delta) {
        Assertions.assertTrue(value > 1 / delta, "Lower bound hit: %.1f".formatted(value));
        Assertions.assertTrue(value < delta, "Upper bound hit: %.1f".formatted(value));
    }

    protected long measureTime(
            final int threads,
            final List<Integer> data,
            final ConcurrentConsumer<P, Integer, List<Integer>> consumer,
            final int subtasks
    ) throws InterruptedException {
        final P instance = createInstance(threads);
        final long start = System.nanoTime();
        consumer.accept(instance, subtasks, data);
        return System.nanoTime() - start;
    }
}

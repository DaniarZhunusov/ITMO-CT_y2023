package info.kgeorgiy.java.advanced.iterative;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Base iterative parallelism test.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseIPTest<P> extends BaseTest {
    protected static final List<Integer> SIZES = List.of(10, 5, 2, 1);
    protected static final int MAX_THREADS = 10;

    protected static final List<Named<Comparator<Integer>>> COMPARATORS = List.of(
            named("Natural order", Integer::compare),
            named("Reverse order", Comparator.reverseOrder()),
            named("Div 100", Comparator.comparingInt(v -> v / 100)),
            named("Even first", Comparator.<Integer>comparingInt(v -> v % 2).thenComparingInt(v -> v)),
            named("All equal", (v1, v2) -> 0)
    );

    protected static final List<Named<Predicate<Integer>>> PREDICATES = List.of(
            named("Equal 0", Predicate.isEqual(0)),
            named("Greater than 0", i -> i > 0),
            named("Even", i -> i % 2 == 0),
            named("True", i -> true),
            named("False", i -> false)
    );

    protected static final List<Named<Function<Integer, ?>>> FUNCTIONS = List.of(
            named("* 2", v -> v * 2),
            named("is even", v -> v % 2 == 0),
            named("toString", Object::toString)
    );

    protected static final List<Named<Void>> UNIT = List.of(named("Common", null));

    private final Random random = new Random(4875043285743285204L);
    protected List<Integer> factors = List.of(0);

    protected BaseIPTest() {
    }

    protected final <T, U> void test(
            final BiFunction<List<Integer>, U, T> fExpected,
            final ConcurrentFunction<P, U, T> fActual,
            final List<Named<U>> cases
    ) throws InterruptedException {
        final List<Named<Supplier<U>>> suppliers = cases.stream()
                .map(n -> named(n.name(), (Supplier<U>) n::value))
                .toList();
        test(suppliers, (instance, threads, data, value) -> {
            final T expected = fExpected.apply(data, value);
            Assertions.assertEquals(
                    expected,
                    fActual.apply(instance, threads, data, value),
                    () -> "For " + data
            );
            return null;
        });
    }

    protected <U> void test(final List<Named<Supplier<U>>> cases, final ConcurrentFunction<P, U, Void> checker) {
        for (final int factor : factors) {
            final P instance = createInstance(factor);
            for (final int n : SIZES) {
                context.context("Size " + n, () -> {
                    final List<Integer> data = randomList(n);
                    for (final Named<Supplier<U>> named : cases) {
                        context.context(named.name, () -> {
                            context.print("threads:");
                            IntStream.rangeClosed(1, MAX_THREADS).forEachOrdered(threads -> {
                                context.print(" " + threads);
                                context.checked(() -> checker.apply(instance, threads, data, named.value().get()));
                            });
                            context.println("");
                        });
                    }
                });
            }
        }
    }

    protected final <T, U> void testException(
            final ConcurrentFunction<P, U, T> fActual,
            final String name,
            final Supplier<U> supplier,
            final Class<? extends Throwable> exceptionClass,
            final String exceptionMessage
    ) {
        test(List.of(named(name, supplier)), (instance, threads, data, value) -> {
            final Throwable exception = Assertions.assertThrowsExactly(
                    exceptionClass,
                    () -> fActual.apply(instance, threads, data, value)
            );
            Assertions.assertTrue(
                    exception.getMessage().contains(exceptionMessage),
                    () -> "Expected message containing '%s', found '%s".formatted(exceptionMessage, exception.getMessage())
            );
            return null;
        });
    }

    protected final <T, U> void testS(
            final BiFunction<Stream<Integer>, U, T> fExpected,
            final ConcurrentFunction<P, U, T> fActual,
            final List<Named<U>> cases
    ) throws InterruptedException {
        test((data, value) -> fExpected.apply(data.stream(), value), fActual, cases);
    }

    protected final List<Integer> randomList(final int size) {
        final List<Integer> pool = random.ints(Math.min(size, 1000_000)).boxed().toList();
        return random.ints(0, pool.size()).limit(size).mapToObj(pool::get).toList();
    }

    protected P createInstance(final int threads) {
        return createCUT();
    }

    protected int getSubtasks(final int threads, final int totalThreads) {
        return threads;
    }

    protected static <T> Named<T> named(final String name, final T value) {
        return new Named<>(name, value);
    }

    public interface ConcurrentFunction<P, U, T> {
        T apply(P instance, int threads, List<Integer> data, U value) throws InterruptedException;
    }

    public interface ConcurrentConsumer<P, T, U> {
        void accept(P instance, T t, U u) throws InterruptedException;
    }

    protected record Named<T>(String name, T value) {
    }
}

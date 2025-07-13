package info.kgeorgiy.java.advanced.lambda;

import info.kgeorgiy.java.advanced.base.BaseTest;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.iterable.Iterables;

import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.CombinedGenerators.pairs;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * Base tests.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class LambdaTest<L> extends BaseTest {
    final L lambda = createCUT();


    // === Spliterators

    static final int MAX_TREE_SIZE = 1 << 7;
    private static final List<IntUnaryOperator> SHAPES = List.of(s -> 1, s -> s - 1, s -> s / 2);

    @SuppressWarnings("MethodMayBeStatic")
    <M> List<TreeTest<String, M>> plain(final Builder<M> builder) {
        return builder.generate(strings(3), Function.identity());
    }

    @SuppressWarnings("MethodMayBeStatic")
    <M> List<TreeTest<String, M>> nested(final Builder<M> builder) {
        return builder.generate(lists(strings(3), 3), values -> values.stream().flatMap(List::stream).toList());
    }

    <T, M> Stream<Named<Consumer<TreeTest<T, M>>>> checkers(final TestSpliterator<T, M> spliterator) {
        return Stream.of(
                spliterator.checker(".forEachRemaining()", sit -> {
                    final List<T> actual = new ArrayList<>();
                    sit.forEachRemaining(actual::add);
                    return actual.stream();
                }),
                spliterator.checker(".toList()", LambdaTest::stream)
        );
    }

    <M, T> void testSpliterator(
            final List<TreeTest<T, M>> tests,
            final Function<M, Spliterator<T>> split,
            final BiConsumer<Spliterator<?>, Long> check
    ) {
        checkers(new TestSpliterator<>(split, check))
                .forEachOrdered(checker -> context.context(checker.name, () -> tests.forEach(checker.value)));
    }

    <M, T> void testSpliterator(final List<TreeTest<T, M>> tests, final Function<M, Spliterator<T>> split) {
        testSpliterator(tests, split, (sit, size) -> {});
    }

    static <T> Stream<T> stream(final Spliterator<T> sit) {
        return StreamSupport.stream(sit, false);
    }

    static <M> Builder<M> binary(final BinaryOperator<M> branch) {
        //noinspection SequencedCollectionMethodCanBeUsed
        return new Builder<>(2, children -> branch.apply(children.get(0), children.get(1)));
    }

    static <M> Builder<M> nary(final Function<List<M>, M> branch) {
        return new Builder<>(5, branch);
    }

    record Builder<M>(int arity, Function<List<M>, M> node) {
        private <V, T> List<TreeTest<T, M>> generate(
                final Generator<V> generator,
                final Function<List<V>, List<T>> flatten
        ) {
            return IntStream.rangeClosed(1, MAX_TREE_SIZE)
                    .mapToObj(size -> lists(generator, size).next())
                    .flatMap(values -> SHAPES.stream()
                            .map(split -> new TreeTest<>(flatten.apply(values), tree(values, split))))
                    .toList();
        }

        @SuppressWarnings("unchecked")
        private <V> M tree(final List<V> values, final IntUnaryOperator split) {
            final int size = values.size();
            if (size == 1) {
                //noinspection SequencedCollectionMethodCanBeUsed
                return (M) new Trees.Leaf<>(values.get(0));
            } else {
                List<V> tail = values;
                final List<List<V>> children = new ArrayList<>(arity);
                for (int i = 1; i < arity && tail.size() > 1; i++) {
                    final int mid = split.applyAsInt(tail.size());
                    children.add(tail.subList(0, mid));
                    tail = tail.subList(mid, tail.size());
                }
                children.add(tail);
                return node.apply(children.stream().map(child -> tree(child, split)).toList());
            }
        }
    }

    record TestSpliterator<T, M>(
            Function<M, Spliterator<T>> spliterator,
            BiConsumer<Spliterator<?>, Long> checker
    ) {

        Named<Consumer<TreeTest<T, M>>> checker(
                final String method,
                final Function<Spliterator<T>, Stream<T>> check
        ) {
            return named(method, test -> Assertions.assertEquals(
                    test.values,
                    check.apply(spliterator(test)).toList(),
                    test.tree + method
            ));
        }

        private Spliterator<T> spliterator(final TreeTest<T, M> test) {
            final Spliterator<T> sit = spliterator.apply(test.tree);
            checker.accept(sit, (long) test.values.size());
            return sit;
        }

        @SuppressWarnings("SameParameterValue")
        Named<Consumer<TreeTest<T, M>>> performance(final int size) {
            return named("Performance " + size, test -> IntStream.range(0, size).forEachOrdered(i -> spliterator(test)));
        }
    }

    record TreeTest<T, M>(List<T> values, M tree) {
    }

    static BiConsumer<Spliterator<?>, Long> checker(
            final LongPredicate fixed,
            final Characteristic... rest
    ) {
        return (sit, size) -> {
            final EnumSet<Characteristic> present = EnumSet.of(Characteristic.ORDERED, rest);
            final EnumSet<Characteristic> missing = EnumSet.complementOf(present);
            missing.removeAll(Characteristic.SIZE);

            Characteristic.check(present, sit, Assertions::assertTrue);
            Characteristic.check(missing, sit, Assertions::assertFalse);
            if (sit.getExactSizeIfKnown() != -1) {
                Characteristic.check(Characteristic.SIZE, sit, Assertions::assertTrue);
            }
            if (fixed.test(size)) {
                Assertions.assertEquals(size, sit.getExactSizeIfKnown(), "getExactSizeIfKnown()");
            }
        };
    }

    @SuppressWarnings("unused")
    enum Characteristic {
        DISTINCT(Spliterator.DISTINCT),
        IMMUTABLE(Spliterator.IMMUTABLE),
        SORTED(Spliterator.SORTED),
        ORDERED(Spliterator.ORDERED),
        SIZED(Spliterator.SIZED),
        SUBSIZED(Spliterator.SUBSIZED),
        NONNULL(Spliterator.NONNULL),
        CONCURRENT(Spliterator.CONCURRENT);

        private static final Set<Characteristic> SIZE = Set.copyOf(EnumSet.of(SIZED, SUBSIZED));

        private final int value;

        Characteristic(final int value) {
            this.value = value;
        }

        private static <T> void check(
                final Set<Characteristic> present,
                final Spliterator<T> sit,
                final BiConsumer<Boolean, String> checker
        ) {
            for (final Characteristic characteristic : present) {
                checker.accept(
                        (sit.characteristics() & characteristic.value) != 0,
                        "has " + characteristic
                );
            }
        }
    }


    // === Collectors

    void testCollectorIndex(
            final String method,
            final Collector<String, ?, Optional<String>> collector,
            final IntUnaryOperator index
    ) {
        testCollector(
                method,
                collector,
                lists(strings(3)),
                maxStreamSize(),
                values -> get(values, index.applyAsInt(values.size()))
        );
    }

    static <T> Optional<T> get(final List<T> values, final Integer i) {
        return 0 <= i && i < values.size() ? Optional.of(values.get(i)) : Optional.empty();
    }

    static String commonPrefix(final String a, final String b) {
        final int bound = Math.min(a.length(), b.length());
        for (int i = 0; i < bound; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, bound);
    }

    void testCommon(
            final String method,
            final Collector<CharSequence, ?, String> collector,
            final BinaryOperator<String> mangle,
            final BinaryOperator<String> answer
    ) {
        testCollector(
                method,
                collector,
                pairs(lists(strings(3)), strings(2)),
                maxStreamSize(),
                test -> test.getFirst()
                        .stream()
                        .map(s -> mangle.apply(s, test.getSecond()))
                        .toList(),
                strings -> Optional.ofNullable(strings.stream()
                                .reduce(null, (a, b) -> a == null ? b : answer.apply(a, b)))
                        .orElse("")
        );
    }

    static <T, I extends T, R> void testCollector(
            final String method,
            final Collector<T, ?, R> collector,
            final Generator<List<I>> generator,
            final int size,
            final Function<List<I>, R> answer
    ) {
        testCollector(method, collector, generator, size, Function.identity(), answer);
    }

    private static <Q, T, I extends T, R> void testCollector(
            final String method,
            final Collector<T, ?, R> collector,
            final Generator<Q> generator,
            final int size,
            final Function<Q, List<I>> prepare,
            final Function<List<I>, R> answer
    ) {
        for (final Q test : Iterables.toIterable(generator, size)) {
            final List<? extends I> values = prepare.apply(test);
            final String message = values + "." + method;
            final R result = Assertions.assertDoesNotThrow(() -> values.stream().collect(collector), message);
            Assertions.assertEquals(answer.apply(Collections.unmodifiableList(values)), result, message);
        }
    }

    void testSized(
            final String method,
            final IntFunction<Collector<String, ?, List<String>>> collector,
            final BiFunction<List<String>, Integer, List<String>> get
    ) {
        testSizedCollector(
                method,
                collector,
                (values, k) -> k <= 0 ? List.of() : values.size() > k ? get.apply(values, k) : values
        );
    }

    <R> void testSizedCollector(
            final String method,
            final IntFunction<Collector<String, ?, R>> collector,
            final BiFunction<List<String>, Integer, R> get
    ) {
//        size(k) * k * k
        IntStream.rangeClosed(indexOrigin(), maxStreamSize()).forEachOrdered(k -> testCollector(
                method.formatted(k),
                collector.apply(k),
                lists(strings(10)),
                maxStreamSize() / Math.max(k, 1) / Math.max(k, 1) / 5,
                values -> get.apply(values, k)
        ));
    }

    int indexOrigin() {
        return 0;
    }

    int maxStreamSize() {
        return 1 << 9;
    }

    <T, R> void testCollectorWith(
            final String method,
            final Function<T, Collector<String, ?, R>> collector,
            final List<Named<T>> args,
            final BiFunction<List<String>, T, R> answer
    ) {
        for (final Named<T> comparator : args) {
            testCollector(
                    method.formatted(comparator.name),
                    collector.apply(comparator.value),
                    lists(strings(10)),
                    maxStreamSize(),
                    values -> answer.apply(values, comparator.value)
            );
        }
    }

    record Named<T>(String name, T value) {
    }

    static <T> Named<T> named(final String name, final T value) {
        return new Named<>(name, value);
    }
}

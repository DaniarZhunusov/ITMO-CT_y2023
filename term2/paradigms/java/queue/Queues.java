package queue;

import base.ExtendedRandom;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Queues {
    private Queues() {
    }

    protected interface QueueModel {
        @ReflectionTest.Ignore
        ArrayDeque<Object> model();

        default Object dequeue() {
            return model().removeFirst();
        }

        default int size() {
            return model().size();
        }

        default boolean isEmpty() {
            return model().isEmpty();
        }

        default void clear() {
            model().clear();
        }

        default void enqueue(final Object element) {
            model().addLast(element);
        }

        default Object element() {
            return model().getFirst();
        }
    }

    protected interface QueueChecker<T extends QueueModel> {
        T wrap(ArrayDeque<Object> reference);

        default List<T> linearTest(final T queue, final ExtendedRandom random) {
            // Do nothing by default
            return List.of();
        }

        default void check(final T queue, final ExtendedRandom random) {
            queue.element();
        }

        default void add(final T queue, final Object element, final ExtendedRandom random) {
            queue.enqueue(element);
        }

        default Object randomElement(final ExtendedRandom random) {
            return ArrayQueueTester.ELEMENTS[random.nextInt(ArrayQueueTester.ELEMENTS.length)];
        }

        default void remove(final T queue, final ExtendedRandom random) {
            queue.dequeue();
        }

        @SuppressWarnings("unchecked")
        default T cast(final QueueModel model) {
            return (T) model;
        }
    }

    @FunctionalInterface
    protected interface Splitter<M extends QueueModel> {
        List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);
    }

    @FunctionalInterface
    /* package-private */ interface LinearTester<M extends QueueModel> extends Splitter<M> {
        void test(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);

        @Override
        default List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random) {
            test(tester, queue, random);
            return List.of();
        }
    }


    // === Reflection

    /* package-private */ interface ReflectionModel extends QueueModel {
        Field ELEMENTS = getField("elements");
        Field HEAD = getField("head");

        @SuppressWarnings("unchecked")
        private <Z> Z get(final Field field) {
            try {
                return (Z) field.get(model());
            } catch (final IllegalAccessException e) {
                throw new AssertionError("Cannot access field " + field.getName() + ": " + e.getMessage(), e);
            }
        }

        private static Field getField(final String name) {
            try {
                final Field field = ArrayDeque.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (final NoSuchFieldException e) {
                throw new AssertionError("Reflection error: " + e.getMessage(), e);
            }
        }

        @ReflectionTest.Ignore
        default int head() {
            return get(HEAD);
        }

        @ReflectionTest.Ignore
        default Object[] elements() {
            return get(ELEMENTS);
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Predicate<Object> p, final BiFunction<R, Integer, R> f) {
            final int size = size();
            final Object[] elements = elements();
            final int head = head();
            R result = zero;
            for (int i = 0; i < size; i++) {
                if (p.test(elements[(head + i) % elements.length])) {
                    result = f.apply(result, i);
                }
            }
            return result;
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Object v, final BiFunction<R, Integer, R> f) {
            return reduce(zero, o -> Objects.equals(v, o), f);
        }
    }


    // === Deque

    /* package-private */ interface DequeModel extends QueueModel {
        default void push(final Object element) {
            model().addFirst(element);
        }

        @SuppressWarnings("UnusedReturnValue")
        default Object peek() {
            return model().getLast();
        }

        default Object remove() {
            return model().removeLast();
        }
    }


    /* package-private */ interface DequeChecker<T extends DequeModel> extends QueueChecker<T> {
        @Override
        default void add(final T queue, final Object element, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.add(queue, element, random);
            } else {
                queue.push(element);
            }
        }

        @Override
        default void check(final T queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.check(queue, random);
            } else {
                queue.peek();
            }
        }

        @Override
        default void remove(final T queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.remove(queue, random);
            } else {
                queue.remove();
            }
        }
    }


    // === CountIf

    /* package-private */ interface CountIfModel extends ReflectionModel {
        default int countIf(final Predicate<Object> p) {
            return reduce(0, p, (v, i) -> v + 1);
        }
    }

    /* package-private */ static final LinearTester<CountIfModel> COUNT_IF =
            (tester, queue, random) -> queue.countIf(tester.randomElement(random)::equals);

    /* package-private */ interface DequeCountIfModel extends DequeModel, CountIfModel {
    }

    /* package-private */ static final LinearTester<DequeCountIfModel> DEQUE_COUNT_IF = COUNT_IF::test;


    // === IndexIf

    /* package-private */
    interface IndexIfModel extends ReflectionModel {
        default int indexIf(final Predicate<Object> p) {
            return reduce(-1, p, (v, i) -> v == -1 ? i : v);
        }

        default int lastIndexIf(final Predicate<Object> p) {
            return reduce(-1, p, (v, i) -> i);
        }
    }

    /* package-private */ static final LinearTester<IndexIfModel> INDEX_IF = (tester, queue, random) -> {
        if (random.nextBoolean()) {
            queue.indexIf(tester.randomElement(random)::equals);
        } else {
            queue.lastIndexIf(tester.randomElement(random)::equals);
        }
    };

    /* package-private */ interface DequeIndexIfModel extends DequeModel, IndexIfModel {
    }

    /* package-private */ static final LinearTester<DequeIndexIfModel> DEQUE_INDEX_IF = INDEX_IF::test;

    // === FlatMap

    /* package-private */ interface FlatMapModel extends Queues.QueueModel {
        @ReflectionTest.Wrap
        default FlatMapModel flatMap(final Function<Object, List<Object>> f) {
            final ArrayDeque<Object> deque = model().stream().flatMap(f.andThen(List::stream)).collect(Collectors.toCollection(ArrayDeque::new));
            return () -> deque;
        }
    }

    /* package-private */ static final Queues.Splitter<FlatMapModel> FLAT_MAP = (tester, queue, random) ->
            List.of(tester.cast(queue.flatMap(value -> switch (Math.abs(value.hashCode() % 5)) {
                case 0 -> List.of();
                case 1 -> List.of(value);
                case 2 -> List.of(value, value);
                case 3 -> List.of("value");
                case 4 -> List.of(value, "value");
                default -> throw new AssertionError("Invalid variant");
            })));

    // === Reduce

    /* package-private */ interface ReduceModel extends Queues.QueueModel {
        default Object reduce(final Object init, final BinaryOperator<Object> op) {
            return model().stream().reduce(init, op);
        }
    }

    /* package-private */ static final Queues.LinearTester<ReduceModel> REDUCE = (tester, queue, random) -> {
        if (random.nextBoolean()) {
            queue.reduce("", (a, b) -> a.toString() + ", " + b.toString());
        } else {
            queue.reduce(0, (a, b) -> a.hashCode() + b.hashCode());
        }
    };

    // === Dedup

    /* package-private */ interface DedupModel extends Queues.QueueModel {
        default void dedup() {
            Object prev = null;
            for (final Iterator<Object> it = model().iterator(); it.hasNext(); ) {
                final Object next = it.next();
                if (next.equals(prev)) {
                    it.remove();
                } else {
                    prev = next;
                }
            }
        }
    }

    /* package-private */ static final Queues.LinearTester<DedupModel> DEDUP = (tester, queue, random) -> queue.dedup();

    // === Distinct

    /* package-private */ interface DistinctModel extends Queues.QueueModel {
        default void distinct() {
            model().removeIf(Predicate.not(new HashSet<>()::add));
        }
    }

    /* package-private */ static final Queues.LinearTester<DistinctModel> DISTINCT = (tester, queue, random) -> queue.distinct();

}

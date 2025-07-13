package info.kgeorgiy.java.advanced.iterative;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Full tests for hard version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ListIPTest<P extends ListIP> extends ScalarIPTest<P> {
    public ListIPTest() {
    }

    @Test
    public void test12_sleepPerformance() throws InterruptedException {
        testPerformance("filter", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.filter(threads, values, v -> sleep(v % 3 == 1)));
        testPerformance("map", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.map(threads, values, v -> sleep(v + 10)));
    }

    @Test
    public void test51_indices() throws InterruptedException {
        testS(
                (values, predicate) -> {
                    final List<Boolean> flags = values.map(predicate::test).toList();
                    return IntStream.range(0, flags.size()).filter(flags::get).boxed().toList();
                },
                (p, threads, values, predicate) -> Arrays.stream(p.indices(threads, values, predicate))
                        .boxed()
                        .toList(),
                PREDICATES
        );
    }

    @Test
    public void test52_filter() throws InterruptedException {
        testS((data, predicate) -> data.filter(predicate).toList(), P::filter, PREDICATES);
    }

    @Test
    public void test53_map() throws InterruptedException {
        testS((data, f) -> data.map(f).toList(), P::map, FUNCTIONS);
    }

    @Test
    public void test54_mapMaximum() throws InterruptedException {
        testS(
                (data, f) -> {
                    final List<String> strings = data.map(f).map(Objects::toString).toList();
                    return strings.indexOf(Collections.max(strings, Comparator.naturalOrder()));
                },
                (instance, threads, data, f) -> {
                    final List<String> mapped = instance.map(threads, data, f.andThen(Objects::toString));
                    return instance.argMax(threads, mapped, Comparator.naturalOrder());
                },
                FUNCTIONS
        );
    }

    @Test
    public void test61_null() {
        testException(
                P::map,
                "NPE",
                () -> (Function<Integer, String>) null,
                NullPointerException.class,
                ""
        );
    }
}

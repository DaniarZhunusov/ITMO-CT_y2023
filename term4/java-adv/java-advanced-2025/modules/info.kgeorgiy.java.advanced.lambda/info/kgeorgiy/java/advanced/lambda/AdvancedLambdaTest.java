package info.kgeorgiy.java.advanced.lambda;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * Tests for advanced version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedLambdaTest extends HardLambdaTest<AdvancedLambda> {
    private static final List<Named<Comparator<String>>> COMPARATORS = List.of(
            named("naturalOrder", Comparator.naturalOrder()),
            named("String.CASE_INSENSITIVE_ORDER", String.CASE_INSENSITIVE_ORDER),
            named("String::length", Comparator.comparingInt(String::length))
    );

    public AdvancedLambdaTest() {
    }

    @Test
    public void test71_distinctBy() {
        testCollector(
                "distinctBy()",
                lambda.distinctBy(String::length),
                lists(strings(10)),
                maxStreamSize(),
                strings -> {
                    final LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
                    strings.forEach(string -> map.putIfAbsent(string.length(), string));
                    return List.copyOf(map.values());
                }
        );
    }

    @Test
    public void test72_minIndex() {
        testExtremeIndex("minIndex(%s)", lambda::minIndex, Collections::min);
    }

    @Test
    public void test73_maxIndex() {
        testExtremeIndex("maxIndex(%s)", lambda::maxIndex, Collections::max);
    }

    private void testExtremeIndex(
            final String method,
            final Function<Comparator<String>, Collector<String, ?, OptionalLong>> collector,
            final BiFunction<List<String>, Comparator<String>, String> answer
    ) {
        testCollectorWith(method, collector, COMPARATORS, (strings, comparator) -> strings.isEmpty()
                ? OptionalLong.empty()
                : OptionalLong.of(strings.indexOf(answer.apply(strings, comparator)))
        );
    }
}

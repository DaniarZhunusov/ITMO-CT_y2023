package info.kgeorgiy.java.advanced.arrayset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-arrayset">ArraySet</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedSetTest extends NavigableSetTest {
    private static final List<String> VALUES = Collections.unmodifiableList(Arrays.asList("hello", "world", null));

    public AdvancedSetTest() {
    }

    @Test
    @Override
    public void test01_implements() {
        super.test01_implements();
        assertImplements(AdvancedSet.class);
    }

    @Test
    public void test51_mutableSource() {
        final List<Integer> data = List.of(1, 10, 50);

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        final List<Integer> list = new ArrayList<>(data);

        final TreeSet<Integer> set = treeSet(data);
        final SortedSet<Integer> integers = set(data);
        assertEq("initial", integers, set);
        list.set(1, 20);
        assertEq("mutated", integers, set);
    }

    @Test
    public void test52_immutableSource() {
        final List<Integer> data = List.of(1, 100, 10);
        assertEq("initial", treeSet(data), set(data));
    }

    @Test
    public void test53_descendingSet() {
        testDescendingSet(TEST_DESCENDING_SET_DATA, null);
    }

    @Test
    public void test54_descendingSetPerformance() {
        testDescendingSetPerformance(10, comparator("unsigned", Integer::compareUnsigned), 300);
        testDescendingSetPerformance(10, null, 300);
        testDescendingSetPerformance(PERFORMANCE_SIZE / 250, comparator("plane", Integer::compare), 2);
        testDescendingSetPerformance(PERFORMANCE_SIZE / 250, null, 2);
    }

    private void testDescendingSetPerformance(final int size, final NamedComparator<Integer> comparator, final int iterations) {
        final SetPair<Integer, NavigableSet<Integer>> pair = this.<NavigableSet<Integer>>pair(performanceList(size), comparator)
                .transformTested((NavigableSet<Integer> model) -> Stream.iterate(model, NavigableSet::descendingSet)
                        .skip(PERFORMANCE_SIZE & -2).findFirst().orElseThrow());
        performance(
                "descendingSet",
                () -> {
                    for (int i = 0; i < iterations; i++) {
                        testDescendingSet(pair);
                    }
                }
        );
    }

    @Override
    protected List<Integer> values(final Comparator<? super Integer> comparator, final Collection<Integer> elements) {
        return super.values(
                comparator,
                comparator == null
                        ? elements
                        : Stream.concat(elements.stream(), Stream.of(null, null)).collect(Collectors.toList())
        );
    }

    @Test
    public void test61_asMap_Get() {
        testMap((pair, keys) -> keys
                .forEach(key -> pair.testGet("asMap().get(%d)", Map::get, key)));
    }

    @Test
    public void test61_asMap_containsKey() {
        testMap((pair, keys) -> keys
                .forEach(key -> pair.testGet("asMap().containsKey(%d)", Map::containsKey, key)));
    }

    @Test
    public void test62_asMap_containsValue() {
        testMap((pair, keys) -> VALUES
                .forEach(value -> pair.testGet("asMap().containsValue(%d)", Map::containsValue, value)));
    }

    @Test
    public void test63_asMap_keySet() {
        testMap((pair, keys) -> pair.testGet("asMap().keySet()", Map::keySet));
    }

    @Test
    public void test64_asMap_values() {
        testMap((pair, keys) -> pair.testGet("asMap().values()", map -> new ArrayList<>(map.values())));
    }

    @Test
    public void test65_asMap_entrySet() {
        testMap((pair, keys) -> pair.testGet("asMap().entrySet()", Map::entrySet));
    }

    @Test
    @SuppressWarnings("RedundantCollectionOperation")
    public void test66_asMap_performance() {
        performance("asMap", keys -> {
            final List<Integer> keysList = List.copyOf(keys);
            final AdvancedSet<Integer> set = set(keysList, Comparator.naturalOrder());
            for (final String value : VALUES) {
                context.context(".asMap(" + value + ")", () -> keysList.forEach(key -> {
                    Assertions.assertEquals(keys.size(), set.asMap(value).size(),               ".size()");
                    Assertions.assertEquals(keys.size(), set.asMap(value).keySet().size(),      ".keySet().size()");
                    Assertions.assertEquals(keys.size(), set.asMap(value).values().size(),      ".values().size()");
                    Assertions.assertEquals(keys.size(), set.asMap(value).entrySet().size(),    ".entrySet().size()");
                    Assertions.assertEquals(value, set.asMap(value).get(key), ".get(" + key + ")");
                    Assertions.assertTrue(set.asMap(value).containsKey(key), ".containsKey(" + key + ")");
                    Assertions.assertTrue(set.asMap(value).containsValue(value), ".containsValue(" + value + ")");
                }));
            }
        });
    }

    protected void testMap(final BiConsumer<SetPair<Integer, Map<Integer, String>>, List<Integer>> testCase) {
        for (final String value : VALUES) {
            context.context("asMap(" + value + ")", () -> test(pair -> testCase.accept(
                    pair.transform(
                            keys -> asMap(keys, value),
                            keys -> ((AdvancedSet<Integer>) keys).asMap(value)
                    ),
                    Stream.concat(
                            random.ints().boxed().limit(100).filter(Predicate.not(pair.model::contains)),
                            pair.model.stream()
                    ).collect(Collectors.toCollection(ArrayList::new))
            )));
        }
    }

    private static <K, V> Map<K, V> asMap(final Collection<K> keys, final V value) {
        final HashMap<K, V> result = new LinkedHashMap<>();
        keys.forEach(key -> result.put(key, value));
        return result;
    }
}

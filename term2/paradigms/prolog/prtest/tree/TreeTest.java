package prtest.tree;

import base.Selector;
import base.TestCounter;
import prtest.Rule;
import prtest.Value;
import prtest.map.MapChecker;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Search Trees</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class TreeTest {
    private static <T, R> R extract(final Function<T, R> extractor, final T value) {
        return value == null ? null : extractor.apply(value);
    }

    private static <T, R> Consumer<MapChecker<Void>> func(
            final String rule,
            final Function<NavigableMap<Integer, Value>, T> getter,
            final Function<T, R> extractor
    ) {
        return test -> test.checker(Rule.func("map_" + rule, 1), map -> extract(extractor, getter.apply(map)));
    }

    private static <T, R> Consumer<MapChecker<Void>> keyFunc(
            final String name,
            final BiFunction<NavigableMap<Integer, Value>, Integer, T> getter,
            final Function<T, R> extractor
    ) {
        return test -> test.keyChecker(Rule.func("map_" + name, 2), (map, key) -> extract(extractor, getter.apply(map, key)));
    }

    private static Consumer<MapChecker<Void>> getRemove(
            final String name,
            final BiFunction<NavigableMap<Integer, Value>, Integer, Map.Entry<Integer, Value>> f
    ) {
        return test -> {
            test.keyChecker(Rule.func("map_" + name.toLowerCase(Locale.ROOT) + "Entry", 2), f);
            test.keyUpdater(Rule.func("map_remove" + name, 2), (state, key, value) -> {
                final Map.Entry<Integer, Value> entry = f.apply(state.expected, key);
                if (entry != null) {
                    state.expected.remove(entry.getKey());
                    state.keys.remove(entry.getKey());
                    state.values.remove(entry.getValue());
                }
            });
        };
    }

    private static final Consumer<MapChecker<Void>> REMOVE_CEILING =
            getRemove("Ceiling", NavigableMap::ceilingEntry);

    private static final Consumer<MapChecker<Void>> HEAD =
            keyFunc("headMapSize", NavigableMap::headMap, Map::size);
    private static final Consumer<MapChecker<Void>> TAIL =
            keyFunc("tailMapSize", NavigableMap::tailMap, Map::size);



    private static final Consumer<MapChecker<Void>> LAST_KEY = func("lastKey", map -> map.isEmpty() ? null : map, NavigableMap::lastKey);
    private static final Consumer<MapChecker<Void>> LAST_VALUE = func("lastValue", NavigableMap::lastEntry, Map.Entry::getValue);
    private static final Consumer<MapChecker<Void>> LAST_ENTRY = func("lastEntry", NavigableMap::lastEntry, Function.identity());

    private static final Consumer<MapChecker<Void>> REPLACE = test -> test.updater(
            Rule.func("map_replace", 3),
            (state, key, value) -> {
                final Value old = state.expected.replace(key, value);
                if (old != null) {
                    state.values.remove(old);
                    state.values.add(value);
                }
            }
    );


    public static final Selector SELECTOR = new Selector(TreeTest.class, "easy", "hard")
            .variant("base", variant(tests -> {}))
            .variant("Last", variant(LAST_KEY, LAST_VALUE, LAST_ENTRY))
            .variant("Replace", variant(true, REPLACE))
            .variant("RemoveCeiling", variant(REMOVE_CEILING))
            .variant("HeadTail", variant(HEAD, TAIL))
            ;

    private TreeTest() {
    }



    @SafeVarargs
    /* package-private */ static Consumer<TestCounter> variant(final Consumer<MapChecker<Void>>... addTests) {
        return variant(false, addTests);
    }

    @SafeVarargs
    /* package-private */ static Consumer<TestCounter> variant(final boolean alwaysUpdate, final Consumer<MapChecker<Void>>... addTests) {
        return counter -> {
            final boolean hard = counter.mode() == 1;
            TreeTester.test(
                    counter, hard || alwaysUpdate, true,
                    tests -> {
                        if (!hard) {
                            tests.clearUpdaters();
                        }
                        Arrays.stream(addTests).forEachOrdered(adder -> adder.accept(tests));
                    }
            );
        };
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

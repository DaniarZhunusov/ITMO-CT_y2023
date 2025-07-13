package jstest.example;

import base.Asserts;
import base.Selector;
import base.TestCounter;
import jstest.EngineException;
import jstest.JSEngine;

import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for Example JavaScript
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExampleTest {
    public static final Selector SELECTOR = new Selector(ExampleTest.class, "easy", "hard")
            .variant("base", counter -> {
                final Tester tester = new Tester(counter);
                counter.scope("add", () -> IntStream.range(0, 10).forEachOrdered(i ->
                        IntStream.range(0, 10).forEachOrdered(j ->
                                tester.test("add(%d, %d)".formatted(i, j), Number.class, i + j)
                        )
                ));
                counter.scope("hello", () -> Stream.of("from JS", "world").forEachOrdered(name ->
                        tester.test("hello(\"%s\")".formatted(name), String.class, "Hello, " + name + "!")
                ));
                counter.scope("strict", () -> {
                    try {
                        tester.eval("checkStrict()", Void.class);
                        Asserts.assertTrue("Error expected", false);
                    } catch (EngineException e) {
                        System.err.println("Error message: " + e.getMessage());
                        final String expected = "ReferenceError: UNDEFINED is not defined";
                        Asserts.assertTrue("Error message", e.getMessage().contains(expected));
                    }
                });
            });

    private static class Tester {
        private final JSEngine engine;
        private final TestCounter counter;

        private Tester(final TestCounter counter) {
            engine = new JSEngine(Path.of("example.js"));
            this.counter = counter;
        }

        public <T> void test(final String code, final Class<T> type, final T expected) {
            counter.test(() -> Asserts.assertEquals(code, expected, eval(code, type)));
        }

        public <T> T eval(final String code, final Class<T> type) {
            return engine.eval(code, code, type).value();
        }
    }

    private ExampleTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

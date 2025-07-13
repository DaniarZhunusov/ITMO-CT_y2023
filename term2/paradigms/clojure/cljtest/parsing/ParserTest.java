package cljtest.parsing;

import base.Selector;
import jstest.expression.Operation;

import java.util.function.BiConsumer;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-expression-parsing">Clojure Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final Operation VARIABLES = checker -> {
        final BiConsumer<Character, Integer> var = (first, index) -> {
            final char prefix = checker.random().nextBoolean() ? first : Character.toUpperCase(first);
            checker.variable(prefix + checker.random().randomString("xyzXYZ"), index);
        };
        for (int i = 0; i < 10; i++) {
            var.accept('x', 0);
            var.accept('y', 1);
            var.accept('z', 2);
        }
    };

    private static final Selector SELECTOR = ParserTester.builder()
            .variant("Base",            NARY_ARITH)
            .variant("Variables",       VARIABLES)
            .variant("MinMax",          VARIABLES,  MIN,            MAX)
            .variant("Complex",         VARIABLES,  INFIX_PHI,  INFIX_ABS)
            .variant("ComplexTrig",     VARIABLES,  SIN, COS, INFIX_SIN, INFIX_COS, POSTFIX_SIN, POSTFIX_COS)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

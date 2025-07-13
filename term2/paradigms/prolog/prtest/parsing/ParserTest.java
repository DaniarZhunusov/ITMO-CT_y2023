package prtest.parsing;

import base.Selector;
import jstest.expression.Operation;

import java.util.function.BiConsumer;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-expression-parsing">Prolog Expression Parser</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final Operation VARIABLES = checker -> {
        final BiConsumer<Integer, Integer> var = (first, index) ->
                checker.variable((char) first.intValue() + checker.random().randomString("xyzXYZ"), index);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                var.accept('x' + j, j);
                var.accept('X' + j, j);
            }
        }
    };

    private static final Selector SELECTOR = ParserTester.builder()
            .variant("Base", ARITH)
            .variant("Square",      SQUARE,     SQRT)
            .variant("VarBitwise", VARIABLES, NOT, AND, OR, XOR)
            .variant("VarIfMux",   VARIABLES, NOT, AND, OR, XOR, IF, MUX)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

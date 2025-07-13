package cljtest.functional;

import base.Selector;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-functional-expressions">Clojure Functional Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class FunctionalTest {
    public static final Selector SELECTOR = FunctionalTester.builder()
            .variant("Base",            NARY_ARITH)
            .variant("SinCos",          SIN,        COS)
            .variant("SinhCosh",        SINH,       COSH)
            .variant("MeanVarn",        MEAN,       VARN)
            .variant("Means",           ARITH_MEAN, GEOM_MEAN, HARM_MEAN)
            .selector();

    private FunctionalTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

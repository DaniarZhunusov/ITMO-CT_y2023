package cljtest.object;

import base.Selector;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-object-expressions">Clojure Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ObjectTest {
    public static final Selector SELECTOR = ObjectTester.builder()
            .variant("Base", NARY_ARITH)
            .variant("ExpLn",           EXP,         LN)
            .variant("PowLog",          POW,         LOG)
            .variant("MeanVarn",        MEAN,        VARN)
            .variant("Means",           ARITH_MEAN,  GEOM_MEAN, HARM_MEAN)
            .selector();

    private ObjectTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

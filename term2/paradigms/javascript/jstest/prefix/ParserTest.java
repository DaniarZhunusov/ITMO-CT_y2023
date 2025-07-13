package jstest.prefix;

import base.Selector;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-expression-parsing">JavaScript Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    public static final Selector SELECTOR = Kind.selector(
                    ParserTest.class,
                    "prefix", "parsePrefix", ParserTester.PREFIX
            )
            .variant("Base", ARITH)
            .variant("ProdGeom", PRODUCT, GEOM)
            .variant("MeanVar", MEAN, VAR)
            .variant("Means", ARITH_MEAN, GEOM_MEAN, HARM_MEAN)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

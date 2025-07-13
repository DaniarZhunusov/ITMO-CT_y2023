package jstest.object;

import base.Selector;
import jstest.expression.Builder;
import jstest.expression.OperationsBuilder;
import jstest.functional.FunctionalTest;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-object-expressions">JavaScript Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ObjectTest {
    /* package-private */
    static Selector.Composite<OperationsBuilder> selector() {
         return Builder.selector(
                ObjectTest.class,
                mode -> false,
                (builder, counter) -> new ObjectTester(
                        counter,
                        builder.language(ObjectTester.OBJECT, FunctionalTest.POLISH),
                        "toString", "parse"
                ),
                "easy", "", "hard", "bonus"
        );
    }

    public static final Selector SELECTOR = selector()
            .variant("Base", ARITH)
            .variant("SinCos", SIN, COS)
            .variant("SinhCosh", SINH, COSH)
            .variant("ArcTan", ATAN, ATAN2)
            .variant("Harmonic", HYPOT, HMEAN)
            .selector();

    private ObjectTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

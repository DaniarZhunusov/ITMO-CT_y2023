package info.kgeorgiy.java.advanced.i18n;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test runner.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("text-statistics", TextStatisticsTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

package info.kgeorgiy.java.advanced.iterative;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("scalar", ScalarIPTest.class)
            .add("list", ListIPTest.class)
            .add("advanced", AdvancedIPTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

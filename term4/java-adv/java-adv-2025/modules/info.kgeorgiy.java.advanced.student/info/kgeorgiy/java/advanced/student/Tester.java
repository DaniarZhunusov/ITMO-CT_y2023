package info.kgeorgiy.java.advanced.student;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("StudentQuery", StudentQueryTest.class)
            .add("GroupQuery", GroupQueryTest.class)
            .add("AdvancedQuery", AdvancedQueryTest.class);

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

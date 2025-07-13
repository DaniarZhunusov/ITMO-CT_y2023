package info.kgeorgiy.java.advanced.arrayset;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test runner.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;
    public static final BaseTester TESTER = new BaseTester()
            .add("SortedSet", SortedSetTest.class)
            .add("NavigableSet", NavigableSetTest.class)
            .add("AdvancedSet", AdvancedSetTest.class);


    public static void main(final String... args) {
        TESTER.main(args);
    }
}

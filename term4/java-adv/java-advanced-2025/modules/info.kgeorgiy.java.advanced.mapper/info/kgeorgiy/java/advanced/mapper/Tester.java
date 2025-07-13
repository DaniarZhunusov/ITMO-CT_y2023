package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("scalar", ScalarMapperTest.class)
            .add("list", ListMapperTest.class)
            .add("advanced", AdvancedMapperTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

package info.kgeorgiy.java.advanced.lambda;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("Easy", EasyLambdaTest.class)
            .add("Hard", HardLambdaTest.class)
            .add("Advanced", AdvancedLambdaTest.class);

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

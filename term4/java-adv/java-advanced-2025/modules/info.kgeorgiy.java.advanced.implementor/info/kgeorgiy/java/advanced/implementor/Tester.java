package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Tests launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .depends(Impler.class)
            .add("interface", InterfaceImplementorTest.class)
            .add("class", ClassImplementorTest.class)
            .add("advanced", AdvancedImplementorTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

package info.kgeorgiy.java.advanced.implementor.tools;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Tests launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .depends(JarImpler.class)
            .add("interface", InterfaceJarImplementorTest.class)
            .add("class", ClassJarImplementorTest.class)
            .add("advanced", AdvancedJarImplementorTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

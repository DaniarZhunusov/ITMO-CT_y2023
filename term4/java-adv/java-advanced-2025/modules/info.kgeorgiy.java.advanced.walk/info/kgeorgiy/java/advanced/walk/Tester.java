package info.kgeorgiy.java.advanced.walk;

import info.kgeorgiy.java.advanced.base.BaseTester;

import java.util.Map;

/**
 * Test runner.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("Walk", WalkTest.class)
            .add("_RecursiveWalk", RecursiveWalkTest.class)
            .add("_AdvancedWalk", AdvancedWalkTest.class)
            .add("RecursiveWalk", cut -> Map.of(
                    "Walk", cut.replace(".RecursiveWalk", ".Walk"),
                    "_RecursiveWalk", cut
            ))
            .add("AdvancedWalk", cut -> Map.of(
                    "Walk", cut.replace(".RecursiveWalk", ".Walk"),
                    "_RecursiveWalk", cut,
                    "_AdvancedWalk", cut
            ))
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

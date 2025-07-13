/**
 * Tests for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-lambda">Lambda</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
module info.kgeorgiy.java.advanced.lambda {
    requires transitive info.kgeorgiy.java.advanced.base;
    requires quickcheck;

    exports info.kgeorgiy.java.advanced.lambda;

    opens info.kgeorgiy.java.advanced.lambda to org.junit.platform.launcher;
}

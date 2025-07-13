package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTester;

import java.util.List;
import java.util.Map;

/**
 * Tester for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-hello-udp">Hello UDP</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = TesterHelper.tester(Util::setMode, List.of("", "-i18n", "-evil"), Map.of(
            "server", HelloServerTest.class,
            "client", HelloClientTest.class,
            "new-server", HelloServerTest.class,
            "new-client", NewHelloClientTest.class
    ));

    public static void main(final String... args) {
        TESTER.main(args);
    }
}

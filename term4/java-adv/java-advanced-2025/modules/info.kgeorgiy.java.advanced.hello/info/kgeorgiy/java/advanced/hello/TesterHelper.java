package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTester;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class for {@link Tester}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum TesterHelper {
    ;

    public static BaseTester.Test test(final BaseTester.Test test, final String prefix, final String name) {
        final String baseTest = name.replaceAll("^new-", "");
        return test
                .withName(name)
                .withCut(prefix + Character.toUpperCase(baseTest.charAt(0)) + baseTest.substring(1, (baseTest + "-").indexOf("-")));
    }

    static BaseTester tester(final Consumer<String> mode, final List<String> suffixes, final Map<String, Class<?>> tests) {
        final BaseTester tester = new BaseTester(test -> {
            final String name = test.name();
            final String prefix = test.cut().replaceAll("Server$", "").replaceAll("Client$", "");

            mode.accept(name);
            if (name.contains("both")) {
                return List.of(
                        test(test, prefix, name.replaceAll("(?<=^|-)both", "client")),
                        test(test, prefix, name.replaceAll("(?<=^|-)both", "server"))
                );
            } else {
                return List.of(test(test, prefix, name));
            }
        });
        for (final String suffix : suffixes) {
            tests.forEach((name, test) -> tester.add(name + suffix, test));
        }
        return tester;
    }
}

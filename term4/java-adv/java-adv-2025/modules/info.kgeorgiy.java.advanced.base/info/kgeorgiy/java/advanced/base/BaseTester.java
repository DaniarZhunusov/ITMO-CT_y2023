package info.kgeorgiy.java.advanced.base;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Test runners base class.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class BaseTester {
    private final Map<String, BiFunction<BaseTester, String, Stream<Class<?>>>> tests = new LinkedHashMap<>();
    private final Class<?> tester;
    private final Context context = new Context();
    private final Function<Test, List<Test>> mangler;

    public BaseTester() {
        this(List::of);
    }

    public BaseTester(final Function<Test, List<Test>> mangler) {
        tester = StackWalker.getInstance()
                .walk(stack -> stack
                        .map(StackWalker.StackFrame::getClassName)
                        .filter(name -> name.endsWith("Tester"))
                        .findFirst()
                        .map(name -> {
                            try {
                                return Class.forName(name);
                            } catch (final ClassNotFoundException e) {
                                throw new AssertionError("Cannot load class " + name, e);
                            }
                        })
                        .orElseThrow(() -> new AssertionError("Unknown caller")));
        this.mangler = mangler;
    }

    @SuppressWarnings("ConfusingMainMethod")
    public void main(final String[] args) {
        if (args.length != 2 && args.length != 3) {
            throw printUsage();
        }

        run(args[0], args[1], args.length > 2 ? args[2] : "");
    }

    public void run(final String name, final String cut, final String salt) {
        final long start = System.currentTimeMillis();

        final List<Class<?>> results = mangler.apply(new Test(name, cut)).stream()
                .flatMap(test -> test(test.name, test.cut))
                .toList();

        final long time = System.currentTimeMillis() - start;
        context.println("============================");
        context.context("OK %s for %s in %dms".formatted(name, cut, time), () -> certify(results.get(0), name + salt));
        context.println("");
        context.println("");
    }

    private Stream<Class<?>> test(final String test, final String cut) {
        final BiFunction<BaseTester, String, Stream<Class<?>>> tester = tests.get(test);
        if (tester == null) {
            context.println("Unknown variant " + test);
            throw printUsage();
        }
        return tester.apply(this, cut);
    }

    private Class<?> test(final String cut, final Class<?> test) {
        System.setProperty(BaseTest.CUT_PROPERTY, cut);

        final SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(test))
                .build();

        final Map<String, Long> testTimes = new LinkedHashMap<>();

        final TestExecutionListener timeListener = new TestExecutionListener() {
            private long startTime;

            @Override
            public void executionStarted(final TestIdentifier id) {
                if (id.isTest()) {
                    startTime = System.currentTimeMillis();
                    context.push("=== %s".formatted(id.getDisplayName()));
                }
            }

            @Override
            public void executionFinished(final TestIdentifier id, final TestExecutionResult result) {
                if (id.isTest()) {
                    final long time = System.currentTimeMillis() - startTime;
                    context.println("%s done in %dms".formatted(id.getDisplayName(), time));
                    context.pop();
                    testTimes.put(id.getDisplayName(), time);
                }
            }
        };

        BaseTest.setGlobalContext(context);
        LauncherFactory.create().execute(request, summaryListener, timeListener);
        final TestExecutionSummary summary = summaryListener.getSummary();
        if (summary.getTestsFailedCount() == 0) {
            context.context("Test times", () -> testTimes.entrySet().forEach(context::println));
            return test;
        }

        for (final TestExecutionSummary.Failure failure : summary.getFailures()) {
            final Throwable exception = failure.getException();
            context.error(
                    "Test %s.%s failed: %s%n".formatted(
                            test.getSimpleName(),
                            failure.getTestIdentifier().getDisplayName(),
                            exception.getMessage()
                    ),
                    exception
            );
        }
        throw exit();
    }

    private void certify(final Class<?> token, final String salt) {
        try {
            final CG cg = (CG) Class.forName("info.kgeorgiy.java.advanced.base.CertificateGenerator")
                    .getDeclaredConstructor(Context.class)
                    .newInstance(context);
            cg.certify(token, salt);
        } catch (final ClassNotFoundException e) {
            // Ignore
        } catch (final Exception e) {
            context.error("Error running certificate generator", e);
        }
    }

    private AssertionError printUsage() {
        context.context("Usage:", () -> {
            for (final String name : getVariants()) {
                context.println("java -ea -cp . -p . -m %s %s Solution.class.name [salt]".formatted(
                        tester.getPackage().getName(),
                        name
                ));
            }
        });
        throw exit();
    }

    private static AssertionError exit() {
        System.exit(1);
        throw new AssertionError("Unreachable");
    }

    public BaseTester add(final String name, final Class<?> testClass) {
        tests.put(name, (tester, cut) -> context.context(
                ">>> %s %s for %s".formatted(testClass.getSimpleName(), name, cut),
                () -> Stream.of(test(cut, testClass))
        ));
        return this;
    }

    public BaseTester add(final String name, final Function<String, Map<String, String>> subvariants) {
        tests.put(name, (tester, cut) -> subvariants.apply(cut).entrySet().stream()
                .flatMap(e -> tester.test(e.getKey(), e.getValue()))
        );
        return this;
    }

    public BaseTester depends(final Class<?>... ignoredDependencies) {
        return this;
    }

    public List<String> getVariants() {
        return tests.keySet().stream()
                .filter(Predicate.not(variant -> variant.startsWith("_")))
                .distinct()
                .toList();
    }

    public record Test(String name, String cut) {
        public Test withName(final String name) {
            return new Test(name, cut);
        }

        public Test withCut(final String cut) {
            return new Test(name, cut);
        }

        public Test withCut(final Function<String, String> cutF) {
            return withCut(cutF.apply(cut));
        }
    }
}

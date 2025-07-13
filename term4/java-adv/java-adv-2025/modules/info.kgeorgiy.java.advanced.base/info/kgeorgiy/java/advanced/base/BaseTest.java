package info.kgeorgiy.java.advanced.base;

import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Tests base class.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class BaseTest {
    public static final String CUT_PROPERTY = "cut";
    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();
    protected Context context;

    protected String testName;

    public BaseTest() {
        final Context context = CONTEXT.get();
        this.context = context == null ? new Context() : context;
    }

    public static void setGlobalContext(final Context context) {
        CONTEXT.set(context);
    }

    @BeforeEach
    public final void beforeTest(final TestInfo test) {
        testName = test.getDisplayName();
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static <T> T createCUT() {
        try {
            return (T) loadClass().getDeclaredConstructor().newInstance();
        } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new AssertionError(e);
        } catch (final InvocationTargetException e) {
            throw new AssertionError(e.getCause());
        }
    }

    public static Class<?> loadClass() {
        final String className = System.getProperty(CUT_PROPERTY);
        Assertions.assertNotNull(className, "Class name not specified");

        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static <E extends Exception> void parallelCommands(final int threads, final List<Command<E>> commands) {
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            for (final Future<Void> future : executor.invokeAll(commands)) {
                future.get();
            }
            executor.shutdown();
        } catch (final InterruptedException | ExecutionException e) {
            throw new AssertionError(e);
        }
    }

    public static <E extends Exception> void parallel(final int threads, final Command<E> command) {
        parallelCommands(threads, Collections.nCopies(threads, command));
    }

    public static void checkConstructor(final String description, final Class<?> token, final Class<?>... params) {
        Assertions.assertDoesNotThrow(
                () -> token.getConstructor(params),
                token.getName() + " should have " + description
        );
    }

    protected static void checkImplements(final Class<?> type, final Class<?> value) {
        Assertions.assertTrue(
                type.isAssignableFrom(value),
                value.getName() + " should implement " + type.getSimpleName() + " interface"
        );
    }

    public interface Command<E extends Exception> extends Callable<Void> {
        @Override
        default Void call() throws E {
            run();
            return null;
        }

        void run() throws E;
    }

    public interface ConsumerCommand<T, E extends Exception> {
        void run(T value) throws E;
    }


    protected static <E extends Exception> ParallelTester<E> parallel(final int threads) {
        return new ParallelTester<>(threads);
    }
}

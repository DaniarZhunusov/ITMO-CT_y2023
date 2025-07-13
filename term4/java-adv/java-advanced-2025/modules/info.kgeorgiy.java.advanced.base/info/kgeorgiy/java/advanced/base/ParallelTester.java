package info.kgeorgiy.java.advanced.base;

import org.junit.jupiter.api.Assertions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Concurrent tests launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("try")
public class ParallelTester<E extends Exception> implements AutoCloseable {
    private final AtomicReference<Throwable> exception = new AtomicReference<>();
    private final ExecutorService executor;

    public ParallelTester(final int threads) {
        executor = Executors.newFixedThreadPool(threads);
    }

    public void parallel(final RunnableE<E> action) {
        executor.execute(() -> {
            try {
                action.run();
            } catch (final Throwable e) {
                exception.compareAndSet(null, e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void close() throws InterruptedException, E {
        executor.shutdown();
        Assertions.assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS), "Not terminated");
        final Throwable exception = this.exception.get();
        if (exception != null) {
            if (exception instanceof final Error e) {
                throw e;
            }
            throw (E) exception;
        }
    }
}

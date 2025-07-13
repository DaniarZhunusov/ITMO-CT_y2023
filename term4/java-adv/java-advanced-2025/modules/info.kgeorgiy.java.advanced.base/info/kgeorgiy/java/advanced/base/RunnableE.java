package info.kgeorgiy.java.advanced.base;

/**
 * Exceptions-aware {@link java.lang.Runnable}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface RunnableE<E extends Throwable> {
    void run() throws E;

    static RunnableE<RuntimeException> of(final Runnable runnable) {
        return runnable::run;
    }
}

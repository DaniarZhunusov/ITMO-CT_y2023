package info.kgeorgiy.java.advanced.base;

/**
 * Exceptions-aware {@link java.util.function.Supplier}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface SupplierE<T, E extends Throwable> {
    T get() throws E;

}

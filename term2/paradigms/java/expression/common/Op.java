package expression.common;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record Op<T>(String name, T value) {
    public static <T> Op<T> of(final String name, final T f) {
        return new Op<>(name, f);
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }
}

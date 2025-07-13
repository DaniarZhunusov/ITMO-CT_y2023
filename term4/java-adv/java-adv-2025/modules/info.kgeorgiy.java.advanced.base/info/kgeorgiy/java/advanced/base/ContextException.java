package info.kgeorgiy.java.advanced.base;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ContextException extends RuntimeException {
    private final List<String> context;

    public ContextException(final List<String> context, final Throwable cause) {
        super(
                "%s / %s: %s".formatted(String.join(" / ", context), cause.getClass().getName(), cause.getMessage()),
                cause
        );
        this.context = context;
    }
}

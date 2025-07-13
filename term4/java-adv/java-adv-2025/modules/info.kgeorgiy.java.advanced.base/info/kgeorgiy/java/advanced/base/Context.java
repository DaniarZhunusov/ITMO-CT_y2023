package info.kgeorgiy.java.advanced.base;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Logging context.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Context {
    private static final byte[] INDENTS = " ".repeat(1024).getBytes();

    private final PrintStream out;
    private final Deque<String> contexts = new ArrayDeque<>();

    public Context(final PrintStream out) {
        this.out = new PrintStream(new FilterOutputStream(out) {
            boolean newLine = false;
            @Override
            public void write(final int b) throws IOException {
                if (newLine) {
                    out.write(INDENTS, 0, contexts.size() * 4);
                }
                out.write(b);
                newLine = b == '\n';
            }
        });
        System.setOut(this.out);
        System.setErr(this.out);
    }

    public Context() {
        this(System.out);
    }

    public void println(final Object message) {
        out.println(message);
    }

    public void print(final Object message) {
        out.print(message);
    }

    public <E extends Exception> void context(final String name, final RunnableE<E> action) throws E {
        context(name, () -> {
            action.run();
            return null;
        });
    }

    public <E extends Exception> long measureTime(final RunnableE<E> runnable) throws E {
        final long start = System.currentTimeMillis();
        runnable.run();
        final long time = System.currentTimeMillis() - start;
        println("Done in " + time + "ms");
        return time;
    }

    public <T, E extends Throwable> T context(final String name, final SupplierE<T, E> action) {
        push(name);
        try {
            return checked(action);
        } finally {
            pop();
        }
    }

    public <T, E extends Throwable> T checked(final SupplierE<T, E> action) {
        try {
            return action.get();
        } catch (final ContextException e) {
            throw e;
        } catch (final Throwable e) {
            throw new ContextException(List.copyOf(contexts), e);
        }
    }


    public void push(final String context) {
        println(context);
        contexts.add(context);
    }
    
    public void pop() {
        contexts.removeLast();
    }

    public void error(final String message, final Throwable cause) {
        println("ERROR: " + message);
        if (cause != null) {
            cause.printStackTrace(out);
        }
    }
}

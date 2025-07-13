package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.iterative.ListIP;
import info.kgeorgiy.java.advanced.iterative.ListIPTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Full tests for {@link ParallelMapper} and {@link ListIP}.
 * 
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ListMapperTest extends ListIPTest<ListIP> {
    public ListMapperTest() {
        factors = List.of(1, 2, 5, 10);
    }

    @Test
    public void test62_rethrowAllNPE() {
        testException(
                ListIP::map,
                "NPE",
                () -> (Function<Integer, String>) v -> { throw new NullPointerException("Hello"); },
                NullPointerException.class,
                "Hello"
        );
    }

    @Test
    public void test63_rethrowAllIOBE() {
        testException(
                ListIP::filter,
                "IOBE",
                () -> (Predicate<Integer>) v -> { throw new IndexOutOfBoundsException("World"); },
                IndexOutOfBoundsException.class,
                "World"
        );
    }

    @Test
    public void test64_rethrowSingleNPE() {
        testException(
                ListIP::map,
                "NPE",
                () -> {
                    final AtomicBoolean thrown = new AtomicBoolean();
                    return (Function<Integer, String>) v -> {
                        if (thrown.compareAndSet(false, true)) {
                            throw new NullPointerException("single");
                        } else {
                            return "";
                        }
                    };
                },
                NullPointerException.class,
                "single"
        );
    }

    @Test
    public void test71_afterClose() {
        final ListIP instance = createInstance(10);
        close();
        Assertions.assertThrowsExactly(IllegalStateException.class, () -> instance.map(1, List.of("Hello", "World"), String::length));
    }

    @Override
    protected ListIP createInstance(final int threads) {
        return TestHelper.instance(threads);
    }

    @Override
    protected int getSubtasks(final int threads, final int totalThreads) {
        return TestHelper.subtasks(totalThreads);
    }

    @AfterEach
    public void close() {
        TestHelper.close();
    }
}

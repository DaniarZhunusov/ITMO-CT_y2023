package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.base.ParallelTester;
import info.kgeorgiy.java.advanced.iterative.AdvancedIP;
import info.kgeorgiy.java.advanced.iterative.AdvancedIPTest;
import info.kgeorgiy.java.advanced.iterative.ListIP;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests for {@link ParallelMapper} and {@link AdvancedIP}.
 * 
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedMapperTest extends AdvancedIPTest {
    private final ListMapperTest listTest = new ListMapperTest();

    public AdvancedMapperTest() {
        factors = List.of(1, 2, 5, 10, 20);
    }

    @Test
    public void test62_rethrowAllNPE() {
        listTest.test62_rethrowAllNPE();
    }

    @Test
    public void test63_rethrowAllIOBE() {
        listTest.test63_rethrowAllIOBE();
    }

    @Test
    public void test64_rethrowSingleNPE() {
        listTest.test64_rethrowSingleNPE();
    }

    @Test
    public void test71_afterClose() {
        listTest.test71_afterClose();
    }

    @Test @SuppressWarnings("try")
    public void test72_parallelClose() throws InterruptedException {
        try (final ParallelTester<RuntimeException> test = parallel(1)) {
            final ListIP instance = createInstance(10);
            test.parallel(() -> Assertions.assertThrowsExactly(
                    IllegalStateException.class,
                    () -> instance.map(1, List.of("Hello", "World"), s -> sleep(sleep(s.length())))
            ));
            sleep(null);
            close();
        }
    }

    @Override
    protected AdvancedIP createInstance(final int threads) {
        return TestHelper.instance(threads);
    }

    @Override
    protected int getSubtasks(final int threads, final int totalThreads) {
        return TestHelper.subtasks(totalThreads);
    }

    @AfterAll
    public static void close() {
        TestHelper.close();
    }
}

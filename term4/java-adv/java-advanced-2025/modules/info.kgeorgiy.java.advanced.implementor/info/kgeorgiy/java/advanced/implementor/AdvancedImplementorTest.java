package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.implementor.advanced.*;
import info.kgeorgiy.java.advanced.implementor.advanced.standard.Collection;
import info.kgeorgiy.java.advanced.implementor.advanced.standard.Iterable;
import info.kgeorgiy.java.advanced.implementor.advanced.standard.Map;

import org.junit.jupiter.api.Test;

/**
 * Full {@link Impler} tests for advanced version
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedImplementorTest extends ClassImplementorTest {
    public AdvancedImplementorTest() {
    }

    @Test
    public void test41_overridden() {
        test(Overridden.OK, Overridden.FAILED);
    }

    @Test
    public void test44_weird() {
        testOk(WeirdInheritance.OK);
    }

    @Test
    public void test45_arrays() {
        testOk(ArraysTest.class);
    }

    @Test
    public void test46_covariantReturns() {
        testOk(CovariantReturns.OK);
    }

    @Test
    public void test47_collectionInterfaces() {
        testOk(Iterable.class, Collection.class, Map.class, Map.Entry.class);
    }

    @Test
    public void test48_sealed() {
        test(SealedClass.OK, SealedClass.FAILED);
    }

    @Test
    public void test49_weirdConstructors() {
        test(WeirdConstructors.OK, WeirdConstructors.FAILED);
    }
}

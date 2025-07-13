package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.implementor.basic.interfaces.InterfaceWithDefaultMethod;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.InterfaceWithStaticMethod;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Accessible;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Descriptor;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Logger;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.RandomAccess;
import info.kgeorgiy.java.advanced.implementor.full.interfaces.EmptyInheritedInterface;
import info.kgeorgiy.java.advanced.implementor.full.interfaces.EmptyInterface;
import info.kgeorgiy.java.advanced.implementor.full.interfaces.NestedInterfaces;
import info.kgeorgiy.java.advanced.implementor.full.interfaces.SameTypesInterface;
import info.kgeorgiy.java.advanced.implementor.full.interfaces.standard.*;

import org.junit.jupiter.api.Test;

/**
 * Full {@link Impler} tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class InterfaceImplementorTest extends BaseImplementorTest {
    public InterfaceImplementorTest() {
    }

    @Test
    public void test01_constructor() {
        assertConstructor(Impler.class);
    }

    @Test
    public void test02_methodlessInterfaces() {
        testOk(EmptyInterface.class, RandomAccess.class, EmptyInheritedInterface.class);
    }

    @Test
    public void test03_standardInterfaces() {
        testOk(Accessible.class, AccessibleAction.class, SDeprecated.class);
    }

    @Test
    public void test04_extendedInterfaces() {
        testOk(Descriptor.class, CachedRowSet.class, DataInput.class, DataOutput.class, Logger.class);
    }

    @Test
    public void test05_standardNonInterfaces() {
        testFail(void.class, String[].class, int[].class, String.class, boolean.class);
    }

    @Test
    public void test06_java8Interfaces() {
        testOk(InterfaceWithStaticMethod.class, InterfaceWithDefaultMethod.class);
    }

    @Test
    public void test07_duplicateClasses() {
        testOk(SameTypesInterface.class);
    }

    @Test
    public void test08_nestedInterfaces() {
        test(NestedInterfaces.OK, NestedInterfaces.FAILED);
    }
}

package info.kgeorgiy.java.advanced.implementor.tools;

import info.kgeorgiy.java.advanced.implementor.BaseImplementorTest;
import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.InterfaceImplementorTest;
import info.kgeorgiy.java.advanced.implementor.tools.full.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Full {@link JarImpler} tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class InterfaceJarImplementorTest extends InterfaceImplementorTest {
    @SuppressWarnings("this-escape")
    public InterfaceJarImplementorTest() {
        addDependency(JarImpler.class);
    }

    @Test
    @Override
    public void test01_constructor() {
        BaseImplementorTest.assertConstructor(Impler.class, JarImpler.class);
    }

    @Test
    public void test09_encoding() {
        testOk(\u041f\u0440\u0438\u0432\u0435\u0442Interface.class);
    }

    @Override
    protected void implement(final Path root, final Impler implementor, final Class<?> clazz) throws ImplerException {
        super.implement(root, implementor, clazz);
        implementJar(root, implementor, clazz);
    }

    public static void implementJar(final Path root, final Impler implementor, final Class<?> clazz) throws ImplerException {
        final Path jarFile = root.resolve(clazz.getName() + ".jar");
        ((JarImpler) implementor).implementJar(clazz, jarFile);
        Assertions.assertTrue(Files.isRegularFile(jarFile), "Jar file not found");
        try (final URLClassLoader classLoader = getClassLoader(jarFile)) {
            BaseImplementorTest.check(classLoader, clazz);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Base {@link Impler} test class.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseImplementorTest extends BaseTest {
    /* package-private */ static final Path DIR = Path.of("__Test__Implementor__");

    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    private static final List<Charset> CHARSETS = List.of(
            StandardCharsets.UTF_8,
            Charset.forName("windows-1251"),
            Charset.forName("KOI8-R"),
            Charset.forName("IBM866")
    );

    private static final Random RANDOM = new Random(2317402983750294387L);

    private final List<Class<?>> dependencies = new ArrayList<>();

    protected BaseImplementorTest() {
        addDependency(Impler.class);
    }

    protected final void addDependency(final Class<?> dependency) {
        dependencies.add(dependency);
    }

    @AfterAll
    public static void cleanUp() {
        clean(DIR);
    }

    protected static void check(final URLClassLoader loader, final Class<?> token) {
        final String name = getImplName(token);
        System.out.println("Loading class " + name);
        final Class<?> impl;
        try {
            impl = loader.loadClass(name);
        } catch (final ClassNotFoundException e) {
            throw new AssertionError("Error loading class " + name, e);
        } catch (final IllegalAccessError e) {
            // Ok
            System.out.println("\t\tAccess issue: " + e.getMessage());
            return;
        }

        if (token.isInterface()) {
            Assertions.assertTrue(token.isAssignableFrom(impl), name + " should implement " + token);
        } else {
            Assertions.assertSame(token, impl.getSuperclass(), name + " should extend " + token);
        }
        Assertions.assertFalse(Modifier.isAbstract(impl.getModifiers()), name + " should not be abstract");
        Assertions.assertFalse(Modifier.isInterface(impl.getModifiers()), name + " should not be interface");
    }

    private static String getImplName(final Class<?> token) {
        return token.getPackageName() + "." + token.getSimpleName() + "Impl";
    }

    protected static void clean(final Path root) {
        if (Files.exists(root)) {
            try {
                Files.walkFileTree(root, DELETE_VISITOR);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static Path getFile(final Path root, final Class<?> clazz) {
        return root.resolve(getImplName(clazz).replace(".", File.separator) + ".java").toAbsolutePath();
    }

    private void implement(final boolean shouldFail, final Path root, final Class<?>... classes) {
        final Impler implementor = createCUT();
        for (final Class<?> clazz : classes) {
            context.context("Implementing " + clazz.getName(), () -> {
                try {
                    implement(root, implementor, clazz);
                } catch (final ImplerException e) {
                    if (shouldFail) {
                        return;
                    }
                    throw new AssertionError("Error implementing", e);
                } catch (final AssertionError e) {
                    throw e;
                } catch (final Throwable e) {
                    throw new AssertionError("Error implementing", e);
                }

                try {
                    compile(root, clazz);

                    Assertions.assertFalse(shouldFail, "Impossible, but true");
                } catch (final Throwable e) {
                    throw new AssertionError("Error compiling", e);
                }

                final Path file = getFile(root, clazz);
                Assertions.assertTrue(Files.exists(file), "Error implementing clazz: File '" + file + "' not found");
            });
        }
    }

    protected void implement(final Path root, final Impler implementor, final Class<?> clazz) throws ImplerException {
        implementor.implement(clazz, root);
    }

    private static void check(final Path root, final Class<?>... classes) {
        final URLClassLoader loader = getClassLoader(root);
        for (final Class<?> token : classes) {
            check(loader, token);
        }
    }

    private void compileFiles(final List<Path> files) {
        Compiler.compile(files, dependencies, CHARSETS.get(RANDOM.nextInt(CHARSETS.size())));
    }

    private void compile(final Path root, final Class<?>... classes) {
        final List<Path> files = Arrays.stream(classes).map(token -> getFile(root, token)).collect(Collectors.toList());
        compileFiles(files);
    }

    protected static URLClassLoader getClassLoader(final Path root) {
        try {
            return new URLClassLoader(new URL[]{root.toUri().toURL()});
        } catch (final MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    private void test(final boolean shouldFail, final Class<?>[] classes) {
        final Path root = DIR.resolve(testName);
        try {
            implement(shouldFail, root, classes);
            if (!shouldFail) {
                check(root, classes);
            }
        } finally {
            clean(root);
        }
    }

    protected static void assertConstructor(final Class<?>... interfaces) {
        final Class<?> token = loadClass();
        for (final Class<?> iface : interfaces) {
            Assertions.assertTrue(iface.isAssignableFrom(token),
                                  token.getName() + " should implement " + iface.getName() + " interface");
        }
        checkConstructor("public default constructor", token);
    }

    protected void testOk(final Class<?>... classes) {
        test(false, classes);
    }

    protected void testFail(final Class<?>... classes) {
        test(true, classes);
    }

    protected void test(final Class<?>[] ok, final Class<?>[] failed) {
        testOk(ok);
        testFail(failed);
    }
}
